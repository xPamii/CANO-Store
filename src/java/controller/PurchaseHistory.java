package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.OrderItems;
import hibernate.Orders;
import hibernate.User;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author pamii
 */
// ✅ DTO Classes
class OrderItemDTO {

    public String productName;
    public int quantity;
    public String status;
    public String deliveryType;
}

class OrderDTO {

    public int orderId;
    public String createdAt;
    public String paymentStatus;
    public String address;
    public List<OrderItemDTO> items;
}

@WebServlet(name = "PurchaseHistory", urlPatterns = {"/PurchaseHistory"})
public class PurchaseHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            // ✅ Get user from session
            Object sessionUser = request.getSession().getAttribute("user");
            Object sessionUserId = request.getSession().getAttribute("userId");

            User user = null;
            if (sessionUser instanceof User) {
                user = (User) sessionUser;
            } else if (sessionUserId instanceof Integer) {
                user = (User) s.get(User.class, (Integer) sessionUserId);
            }

            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseObject.addProperty("message", "Login Expired");
                response.getWriter().write(responseObject.toString());
                return;
            }

            Criteria c = s.createCriteria(Orders.class, "o")
                    .add(Restrictions.eq("o.user", user))
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
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("orderId", o.getId());
                orderJson.addProperty("createdAt",
                        o.getCreatedAt() != null ? sdf.format(o.getCreatedAt()) : null);
                orderJson.addProperty("paymentStatus",
                        o.getPaymentStatus() != null ? o.getPaymentStatus().getStatus() : null);
//                orderJson.addProperty("discount", o.getDiscount());
//                orderJson.addProperty("subTotal", o.getSubTotal());
                orderJson.addProperty("grandTotal", o.getGrandTotal());

                // ✅ Add items for each order
                List<JsonObject> itemsJson = new ArrayList<>();
                for (OrderItems item : o.getItems()) {
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
            responseObject.addProperty("error", "Failed to fetch purchase history");
            response.setContentType("application/json");
            response.getWriter().write(responseObject.toString());
        } finally {
            s.close();
        }
    }

}
