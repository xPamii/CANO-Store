package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Orders;
import hibernate.OrderItems;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "PurchasedOrderDetails", urlPatterns = {"/PurchasedOrderDetails"})
public class PurchasedOrderDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        // ✅ Validate orderId param
        String orderIdParam = request.getParameter("orderId");
        if (orderIdParam == null) {
            responseObject.addProperty("error", "Missing orderId parameter");
            writeResponse(response, responseObject);
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdParam);
        } catch (NumberFormatException e) {
            responseObject.addProperty("error", "Invalid orderId parameter");
            writeResponse(response, responseObject);
            return;
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            // Fetch order with all necessary joins
            Criteria c = session.createCriteria(Orders.class, "o")
                    .add(Restrictions.idEq(orderId))
                    .setFetchMode("o.items", FetchMode.JOIN)
                    .setFetchMode("o.paymentStatus", FetchMode.JOIN)
                    .setFetchMode("o.address", FetchMode.JOIN)
                    .createAlias("o.items", "i")
                    .setFetchMode("i.product", FetchMode.JOIN)
                    .setFetchMode("i.orderStatus", FetchMode.JOIN)
                    .setFetchMode("i.deliveryTypes", FetchMode.JOIN);

            Orders order = (Orders) c.uniqueResult();

            if (order == null) {
                responseObject.addProperty("error", "Order not found");
            } else {
                // Initialize lazy collections
                Hibernate.initialize(order.getItems());
                Hibernate.initialize(order.getPaymentStatus());
                Hibernate.initialize(order.getAddress());

                if (order.getItems() != null) {
                    for (OrderItems item : order.getItems()) {
                        Hibernate.initialize(item.getProduct());
                        Hibernate.initialize(item.getOrderStatus());
                        Hibernate.initialize(item.getDeliveryTypes());
                    }
                }

                // Gson builder
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();

                // Convert order to JsonObject to add extra fields
                JsonObject orderJson = gson.toJsonTree(order).getAsJsonObject();

                // ✅ Add customerName from Address
                if (order.getAddress() != null) {
                    String customerName = (order.getAddress().getFirstName() != null ? order.getAddress().getFirstName() : "")
                            + " "
                            + (order.getAddress().getLastName() != null ? order.getAddress().getLastName() : "");
                    customerName = customerName.trim().isEmpty() ? "N/A" : customerName;

                    String mobile = (order.getAddress().getMobile() != null ? order.getAddress().getMobile() : "");

                    orderJson.addProperty("customerName", customerName);
                    orderJson.addProperty("mobile", mobile);
                } else {
                    orderJson.addProperty("customerName", "N/A");
                }

                String orderStatus = order.getItems() != null && !order.getItems().isEmpty()
                        ? order.getItems().get(0).getOrderStatus().getValue()
                        : "Unknown";
                orderJson.addProperty("orderStatus", orderStatus);

                String paymentStatus = order.getPaymentStatus().getStatus() != null ? order.getPaymentStatus().getStatus(): "Unknown";
                orderJson.addProperty("paymentStatus", paymentStatus);

                
                
                responseObject.add("order", orderJson);
                responseObject.addProperty("status", true);
            }

            writeResponse(response, responseObject);

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("error", "Failed to fetch order details");
            writeResponse(response, responseObject);
        } finally {
            session.close();
        }

    }

    // ✅ Utility to send JSON response
    private void writeResponse(HttpServletResponse response, JsonObject responseObject) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseObject.toString());
    }

}
