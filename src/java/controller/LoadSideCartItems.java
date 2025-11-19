package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author pamii
 */
@WebServlet(name = "LoadSideCartItems", urlPatterns = {"/LoadSideCartItems"})
public class LoadSideCartItems extends HttpServlet {

 
     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        User user = (User) request.getSession().getAttribute("user");
        if (user != null) { //DB Cart
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();
            if (cartList.isEmpty()) {
                System.out.println("Your cart is empty...");
                responseObject.addProperty("message", "Your cart is empty...");
            } else {
                for (Cart cart : cartList) {
                    cart.getProduct().setAdminLG(null);
                    cart.setUser(null);
                }
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Cart items successfully loded");
                responseObject.add("cartItems", gson.toJsonTree(cartList));
            }

        } else {//sessionCart
            ArrayList<Cart> sessionCarts = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");
            if (sessionCarts != null) {
                if (sessionCarts.isEmpty()) {
                    responseObject.addProperty("message", "Your cart is empty...");
                } else {
                    for (Cart sessionCart : sessionCarts) {
                        sessionCart.getProduct().setAdminLG(null);
                        sessionCart.setUser(null);
                    }
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Cart items successfully loded");
                    responseObject.add("cartItems", gson.toJsonTree(sessionCarts));
                }
            } else {
//                System.out.println("Your cart is empty...");
                responseObject.addProperty("message", "Your cart is empty...");
            }
        }
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }
    

}
