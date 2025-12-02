package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Type;
import java.io.IOException;
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
@WebServlet(name = "LoadProductsByType", urlPatterns = {"/LoadProductsByType"})
public class LoadProductsByType extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String typeParam = request.getParameter("type");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            Criteria criteria = session.createCriteria(Product.class, "p");

            if (typeParam != null && !typeParam.equalsIgnoreCase("all")) {
                criteria.createAlias("type", "t");
                criteria.add(Restrictions.eq("t.value", typeParam));
            }

            List<Product> products = criteria.list();

            Gson gson = new Gson();
            JsonObject responseObject = new JsonObject();
            responseObject.add("productList", gson.toJsonTree(products));
            responseObject.addProperty("status", true);

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseObject));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":false,\"error\":\"Server error\"}");
        } finally {
            session.close();
        }
    }
}
