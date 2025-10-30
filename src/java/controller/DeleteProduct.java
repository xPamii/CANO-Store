package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.Product;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "DeleteProduct", urlPatterns = {"/DeleteProduct"})
public class DeleteProduct extends HttpServlet {

protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String idStr = request.getParameter("id");
    JsonObject responseObject = new JsonObject();
    responseObject.addProperty("status", false);

    if (idStr == null || idStr.isEmpty()) {
        responseObject.addProperty("message", "Product ID is required");
        sendResponse(response, responseObject);
        return;
    }

    int id;
    try {
        id = Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
        responseObject.addProperty("message", "Invalid product ID");
        sendResponse(response, responseObject);
        return;
    }

    SessionFactory sf = HibernateUtil.getSessionFactory();
    Session s = sf.openSession();

    try {
        Product p = (Product) s.get(Product.class, id);
        if (p == null) {
            responseObject.addProperty("message", "Product not found");
            sendResponse(response, responseObject);
            return;
        }

        s.beginTransaction();
        s.delete(p);
        s.getTransaction().commit();

        // Delete product images folder
        String appPath = getServletContext().getRealPath("");
        String productImagesPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images");
        File productFolder = new File(productImagesPath, String.valueOf(id));
        if (productFolder.exists() && productFolder.isDirectory()) {
            for (File file : productFolder.listFiles()) {
                file.delete();
            }
            productFolder.delete();
        }

        responseObject.addProperty("status", true);
        responseObject.addProperty("message", "Product deleted successfully");
    } catch (Exception e) {
        e.printStackTrace();
        responseObject.addProperty("message", "Error deleting product: " + e.getMessage());
    } finally {
        s.close();
    }

    sendResponse(response, responseObject);
}


    private void sendResponse(HttpServletResponse response, JsonObject responseObject) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
