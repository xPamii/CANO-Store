package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.AdminLG;
import hibernate.Category;
import hibernate.Color;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.Size;
import hibernate.Status;
import hibernate.Type;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@MultipartConfig
@WebServlet(name = "UpdateProduct", urlPatterns = {"/UpdateProduct"})
public class UpdateProduct extends HttpServlet {

    private static final int ACTIVE_STATUS_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (idStr == null || idStr.isEmpty()) {
            responseObject.addProperty("message", "Product ID is required");
            sendResponse(response, responseObject);
            return;
        }

        int id = Integer.parseInt(idStr);

        // Get params
        String title = request.getParameter("title");
        String quantity = request.getParameter("quantity");
        String price = request.getParameter("price");
        String description = request.getParameter("description");
        String weight = request.getParameter("weight");
        String dimension = request.getParameter("dimension");
        String material = request.getParameter("material");
        String color = request.getParameter("color");
        String category = request.getParameter("category");
        String type = request.getParameter("type");
        String size = request.getParameter("size");

        Part image1 = request.getPart("image1");
        Part image2 = request.getPart("image2");
        Part image3 = request.getPart("image3");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        try {
            if (request.getSession().getAttribute("admin") == null) {
                responseObject.addProperty("message", "Please login");
                sendResponse(response, responseObject);
                return;
            }

            // Validate required fields (you can make this more flexible if needed)
            if (title == null || title.isEmpty()) {
                responseObject.addProperty("message", "Product title can not be empty");
                sendResponse(response, responseObject);
                return;
            }
            if (!Util.isInteger(quantity) || Integer.parseInt(quantity) <= 0) {
                responseObject.addProperty("message", "Invalid quantity");
                sendResponse(response, responseObject);
                return;
            }
            if (!Util.isDouble(price) || Double.parseDouble(price) <= 0) {
                responseObject.addProperty("message", "Invalid price");
                sendResponse(response, responseObject);
                return;
            }
            if (description == null || description.isEmpty()) {
                responseObject.addProperty("message", "Product description can not be empty");
                sendResponse(response, responseObject);
                return;
            }
            if (weight == null || weight.isEmpty()) {
                responseObject.addProperty("message", "Product weight can not be empty");
                sendResponse(response, responseObject);
                return;
            }
            if (dimension == null || dimension.isEmpty()) {
                responseObject.addProperty("message", "Product dimension can not be empty");
                sendResponse(response, responseObject);
                return;
            }
            if (material == null || material.isEmpty()) {
                responseObject.addProperty("message", "Product material can not be empty");
                sendResponse(response, responseObject);
                return;
            }
            if (!Util.isInteger(color) || Integer.parseInt(color) == 0) {
                responseObject.addProperty("message", "Please select a valid color");
                sendResponse(response, responseObject);
                return;
            }
            if (!Util.isInteger(category) || Integer.parseInt(category) == 0) {
                responseObject.addProperty("message", "Please select a valid category");
                sendResponse(response, responseObject);
                return;
            }
            if (!Util.isInteger(type) || Integer.parseInt(type) == 0) {
                responseObject.addProperty("message", "Please select a valid type");
                sendResponse(response, responseObject);
                return;
            }
            if (!Util.isInteger(size) || Integer.parseInt(size) == 0) {
                responseObject.addProperty("message", "Please select a valid size");
                sendResponse(response, responseObject);
                return;
            }

            // Fetch product by ID
            Product p = (Product) s.get(Product.class, id);
            if (p == null) {
                responseObject.addProperty("message", "Product not found");
                sendResponse(response, responseObject);
                return;
            }

            Color color1 = (Color) s.get(Color.class, Integer.parseInt(color));
            Category category1 = (Category) s.get(Category.class, Integer.parseInt(category));
            Type type1 = (Type) s.get(Type.class, Integer.parseInt(type));
            Size size1 = (Size) s.get(Size.class, Integer.parseInt(size));
            Status status = (Status) s.get(Status.class, ACTIVE_STATUS_ID);

            if (color1 == null || category1 == null || type1 == null || size1 == null) {
                responseObject.addProperty("message", "Invalid related entity selected");
                sendResponse(response, responseObject);
                return;
            }

            boolean updated = false; // Track if any field changed

            // Compare each field and update only if changed
            if (!title.equals(p.getName())) {
                p.setName(title);
                updated = true;
            }

            int qtyInt = Integer.parseInt(quantity);
            if (qtyInt != p.getQty()) {
                p.setQty(qtyInt);
                updated = true;
            }

            double priceDouble = Double.parseDouble(price);
            if (priceDouble != p.getPrice()) {
                p.setPrice(priceDouble);
                updated = true;
            }

            if (!description.equals(p.getDescription())) {
                p.setDescription(description);
                updated = true;
            }

            if (!weight.equals(p.getWeight())) {
                p.setWeight(weight);
                updated = true;
            }

            if (!dimension.equals(p.getDimension())) {
                p.setDimension(dimension);
                updated = true;
            }

            if (!material.equals(p.getMaterial())) {
                p.setMaterial(material);
                updated = true;
            }

            if (color1.getId() != (p.getColor() != null ? p.getColor().getId() : -1)) {
                p.setColor(color1);
                updated = true;
            }

            if (category1.getId() != (p.getCategory() != null ? p.getCategory().getId() : -1)) {
                p.setCategory(category1);
                updated = true;
            }

            if (type1.getId() != (p.getType() != null ? p.getType().getId() : -1)) {
                p.setType(type1);
                updated = true;
            }

            if (size1.getId() != (p.getSize() != null ? p.getSize().getId() : -1)) {
                p.setSize(size1);
                updated = true;
            }

            // You may want to update status & admin if needed
            // e.g., always keep status active on update:
            if (p.getStatus() == null || p.getStatus().getId() != ACTIVE_STATUS_ID) {
                p.setStatus(status);
                updated = true;
            }

            AdminLG admin = (AdminLG) request.getSession().getAttribute("admin");
            if (admin != null && (p.getAdminLG() == null || p.getAdminLG().getId() != admin.getId())) {
                p.setAdminLG(admin);
                updated = true;
            }

            if (updated) {
                p.setCreated_at(new Date()); // update date only if changed

                s.beginTransaction();
                s.update(p);
                s.getTransaction().commit();
            }

            // Handle image uploads if new files provided
            boolean imagesUpdated = false;
            if (image1 != null && image1.getSize() > 0) {
                saveImage(image1, p.getId(), "image1.png");
                imagesUpdated = true;
            }
            if (image2 != null && image2.getSize() > 0) {
                saveImage(image2, p.getId(), "image2.png");
                imagesUpdated = true;
            }
            if (image3 != null && image3.getSize() > 0) {
                saveImage(image3, p.getId(), "image3.png");
                imagesUpdated = true;
            }

            if (updated || imagesUpdated) {
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Product updated successfully");
            } else {
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "No changes detected");
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "Error updating product: " + e.getMessage());
        } finally {
            s.close();
        }

        sendResponse(response, responseObject);
    }

    private void saveImage(Part imagePart, int productId, String fileName) throws IOException {
        String appPath = getServletContext().getRealPath("");
        String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images");
        File productFolder = new File(newPath, String.valueOf(productId));
        if (!productFolder.exists()) {
            productFolder.mkdirs();
        }
        File file = new File(productFolder, fileName);
        Files.copy(imagePart.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void sendResponse(HttpServletResponse response, JsonObject responseObject) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(responseObject));
    }
}
