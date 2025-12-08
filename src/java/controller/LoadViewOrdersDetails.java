package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
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

/**
 *
 * @author pamii
 */
@WebServlet(name = "LoadViewOrdersDetails", urlPatterns = {"/LoadViewOrdersDetails"})
public class LoadViewOrdersDetails extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        Gson gson = new Gson();
        responseObject.addProperty("status", false);

        String orderIdParam = request.getParameter("orderId");
        if (orderIdParam == null) {
            responseObject.addProperty("error", "Missing orderId parameter");
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdParam);
        } catch (NumberFormatException e) {
            responseObject.addProperty("error", "Invalid orderId parameter");
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Criteria c = session.createCriteria(Orders.class, "o")
                    .add(Restrictions.idEq(orderId))
                    .setFetchMode("o.items", FetchMode.JOIN)
                    .setFetchMode("o.paymentStatus", FetchMode.JOIN)
                    .setFetchMode("o.address", FetchMode.JOIN)
                    .createAlias("o.items", "i", Criteria.LEFT_JOIN)
                    .setFetchMode("i.product", FetchMode.JOIN)
                    .setFetchMode("i.orderStatus", FetchMode.JOIN)
                    .setFetchMode("i.deliveryTypes", FetchMode.JOIN);

            Orders order = (Orders) c.uniqueResult();

            if (order == null) {
                responseObject.addProperty("error", "Order not found");
            } else {
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

                JsonObject orderJson = new JsonObject();

                // Basic order info
                orderJson.addProperty("id", order.getId());
                orderJson.addProperty("createdAt", order.getCreatedAt() != null ? order.getCreatedAt().toString() : "");

                orderJson.addProperty("subTotal", order.getSubTotal());
                orderJson.addProperty("grandTotal", order.getGrandTotal());
                orderJson.addProperty("discount", order.getDiscount());

                // Customer info
                Address addr = order.getAddress();
                String customerName = "";
                String fullAddress = "";
                String mobile = "";
                String email = order.getUser().getEmail() != null ? order.getUser().getEmail() : "N/A";

                if (addr != null) {
                    customerName = ((addr.getFirstName() != null ? addr.getFirstName() : "") + " "
                            + (addr.getLastName() != null ? addr.getLastName() : "")).trim();
                    customerName = customerName.isEmpty() ? "N/A" : customerName;

                    String line1 = addr.getLineOne() != null ? addr.getLineOne() : "";
                    String line2 = addr.getLineTwo() != null ? addr.getLineTwo() : "";
                    String city = addr.getCity() != null ? addr.getCity().getName() : "";
                    String postal = addr.getPostalCode() != null ? addr.getPostalCode() : "";

                    fullAddress = line1 + ", " + line2 + ", " + city + ", " + postal;
                    mobile = addr.getMobile() != null ? addr.getMobile() : "";

                } else {
                    customerName = "N/A";
                    fullAddress = "N/A";
                    mobile = "N/A";
                }
                orderJson.addProperty("customerName", customerName);
                orderJson.addProperty("fullAddress", fullAddress);
                orderJson.addProperty("mobile", mobile);
                orderJson.addProperty("customerEmail", email);

                // Payment status
                String paymentStatus = order.getPaymentStatus() != null
                        && order.getPaymentStatus().getStatus() != null
                        ? order.getPaymentStatus().getStatus()
                        : "Unknown";
                orderJson.addProperty("paymentStatus", paymentStatus);

                // Order items
                JsonArray itemsArray = new JsonArray();
                double totalDeliveryFee = 0.0;
                if (order.getItems() != null) {
                    for (OrderItems item : order.getItems()) {
                        JsonObject itemJson = new JsonObject();

                        // Product info
                        int productId = 0;
                        if (item.getProduct() != null) {
                            productId = item.getProduct().getId();
                            itemJson.addProperty("productName", item.getProduct().getName() != null ? item.getProduct().getName() : "N/A");
                            itemJson.addProperty("productPrice", item.getProduct().getPrice());
                            itemJson.addProperty("productColor", item.getProduct().getColor().getValue());
                            itemJson.addProperty("productSize", item.getProduct().getSize().getValue());
                        } else {
                            itemJson.addProperty("productName", "N/A");
                            itemJson.addProperty("productPrice", 0);
                            itemJson.addProperty("productColor", "N/A");
                            itemJson.addProperty("productSize", "N/A");
                        }
                        // Product image path
                        itemJson.addProperty("productImage", "product-images/" + productId + "/image1.png");

                        //Delivery Fee
                        if (item.getDeliveryTypes() != null) {
                            totalDeliveryFee += item.getDeliveryTypes().getPrice();
                        }

                        // Order item info
                        itemJson.addProperty("qty", item.getQty());
                        itemJson.addProperty("itemSubtotal", item.getItemSubtotal());

                        // Delivery fee
                        orderJson.addProperty("deliveryFee", totalDeliveryFee);

                        // Order status
                        String orderStatus = item.getOrderStatus() != null
                                ? (item.getOrderStatus().getValue() != null ? item.getOrderStatus().getValue() : "Unknown")
                                : "Unknown";
                        itemJson.addProperty("orderStatus", orderStatus);

                        itemsArray.add(itemJson);
                    }
                }
                orderJson.add("items", itemsArray);

                // Overall order status (first item's status or "Unknown")
                String overallStatus = !itemsArray.isJsonNull() && itemsArray.size() > 0
                        ? itemsArray.get(0).getAsJsonObject().get("orderStatus").getAsString()
                        : "Unknown";
                orderJson.addProperty("orderStatus", overallStatus);

                responseObject.add("order", orderJson);
                responseObject.addProperty("status", true);
            }

            System.out.println(order);

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("error", "Failed to fetch order details: " + e.getMessage());
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));
        } finally {
            session.close();
        }
    }
}
