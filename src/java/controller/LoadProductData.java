package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.Color;
import hibernate.HibernateUtil;
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

/**
 *
 * @author pamii
 */
@WebServlet(name = "LoadProductData", urlPatterns = {"/LoadProductData"})
public class LoadProductData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //Load Color
        Criteria c1 = s.createCriteria(Color.class);
        List<Color> colorList = c1.list();

        //Load Category
        Criteria c2 = s.createCriteria(Category.class);
        List<Category> categoryList = c2.list();

        //Load Type
        Criteria c3 = s.createCriteria(Type.class);
        List<Type> typeList = c3.list();

        Gson gson = new Gson();

        responseObject.add("colorList", gson.toJsonTree(colorList));
        responseObject.add("categoryList", gson.toJsonTree(categoryList));
        responseObject.add("typeList", gson.toJsonTree(typeList));

        responseObject.addProperty("status", true);

        String toJSON = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJSON);
        s.close();

    }

}
