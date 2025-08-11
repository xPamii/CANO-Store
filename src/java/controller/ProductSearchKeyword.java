package controller;

import DAO.ProductDAO;
import com.google.gson.Gson;
import hibernate.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pamii
 */
@WebServlet(name = "ProductSearchKeyword", urlPatterns = {"/api/products/search"})
public class ProductSearchKeyword extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (keyword == null || keyword.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Keyword is required\"}");
            return;
        }

        List<Product> products = productDAO.searchProductsByName(keyword);

        Gson gson = new Gson();
        String json = gson.toJson(products);

        resp.getWriter().write(json);
    }

}
