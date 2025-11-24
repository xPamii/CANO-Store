package controller;

import DTO.ProductDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author pamii
 */
@WebServlet(name = "LoadProductDataTable", urlPatterns = {"/LoadProductDataTable"})
public class LoadProductDataTable extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = null;

        try {
            session = sf.openSession();

            // Load all products using Criteria
            Criteria criteria = session.createCriteria(Product.class);
            List<Product> products = criteria.list();

            // Convert to DTOs
            List<ProductDTO> dtoList = products.stream()
                    .map(ProductDTO::new)
                    .collect(Collectors.toList());

            Gson gson = new Gson();

            // Add DTO list as JSON array
            responseObject.add("products", gson.toJsonTree(dtoList));
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Error loading products");
        } finally {
            if (session != null) session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
