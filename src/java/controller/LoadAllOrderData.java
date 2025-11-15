package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
import org.hibernate.criterion.Order;

/**
 *
 * @author pamii
 */
@WebServlet(name = "LoadAllOrderData", urlPatterns = {"/LoadAllOrderData"})
public class LoadAllOrderData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            Criteria c = s.createCriteria(Orders.class, "o")
                    .addOrder(Order.desc("o.createdAt"))
                    .setFetchMode("o.items", FetchMode.JOIN)
                    .setFetchMode("o.paymentStatus", FetchMode.JOIN)
                    .setFetchMode("o.address", FetchMode.JOIN)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

            List<Orders> orderList = c.list();

            Gson gson = new Gson();
            List<JsonObject> ordersJson = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Orders o : orderList) {
                Hibernate.initialize(o.getItems());
                Hibernate.initialize(o.getPaymentStatus());
                Hibernate.initialize(o.getAddress());

                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("orderId", o.getId());
                orderJson.addProperty("createdAt",
                        o.getCreatedAt() != null ? sdf.format(o.getCreatedAt()) : null);
                orderJson.addProperty("paymentStatus",
                        o.getPaymentStatus() != null ? o.getPaymentStatus().getStatus() : "Unknown");
                orderJson.addProperty("grandTotal", o.getGrandTotal());

                // ✅ Add customer details from Address
                if (o.getAddress() != null) {
                    String customerName = 
                        (o.getAddress().getFirstName() != null ? o.getAddress().getFirstName() : "") + " " +
                        (o.getAddress().getLastName() != null ? o.getAddress().getLastName() : "");
                    customerName = customerName.trim().isEmpty() ? "N/A" : customerName;

                    String email = o.getUser().getEmail()!= null ? o.getUser().getEmail() : "N/A";
                    String mobile = o.getAddress().getMobile() != null ? o.getAddress().getMobile() : "N/A";

                    orderJson.addProperty("customerName", customerName);
                    orderJson.addProperty("customerEmail", email);
                    orderJson.addProperty("mobile", mobile);
                } else {
                    orderJson.addProperty("customerName", "N/A");
                    orderJson.addProperty("customerEmail", "N/A");
                    orderJson.addProperty("mobile", "N/A");
                }

                // ✅ Add items for each order
                List<JsonObject> itemsJson = new ArrayList<>();
                for (OrderItems item : o.getItems()) {
                    Hibernate.initialize(item.getProduct());
                    Hibernate.initialize(item.getOrderStatus());
                    Hibernate.initialize(item.getDeliveryTypes());

                    JsonObject itemJson = new JsonObject();
                    itemJson.addProperty("productName", item.getProduct().getName());
                    itemJson.addProperty("quantity", item.getQty());
                    itemJson.addProperty("status", item.getOrderStatus().getValue());
                    itemJson.addProperty("deliveryType", item.getDeliveryTypes().getPrice());
                    itemsJson.add(itemJson);
                }
                orderJson.add("items", gson.toJsonTree(itemsJson));

                ordersJson.add(orderJson);
            }

            responseObject.add("orders", gson.toJsonTree(ordersJson));
            responseObject.addProperty("status", true);

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("error", "Failed to fetch Orders Data");
            response.setContentType("application/json");
            response.getWriter().write(responseObject.toString());
        } finally {
            s.close();
        }
    }
}

