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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "AddProduct", urlPatterns = {"/AddProduct"})
public class AddProduct extends HttpServlet {

    private static final int ACTIVE_STATUS_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String title = request.getParameter("title");
        String quantity = request.getParameter("quantity");
        String price = request.getParameter("price");
        String description = request.getParameter("description");
        String color = request.getParameter("color");
        String category = request.getParameter("category");
        String type = request.getParameter("type");
        String size = request.getParameter("size");

        Part image1 = request.getPart("image1");
        Part image2 = request.getPart("image2");
        Part image3 = request.getPart("image3");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //validation
        if (request.getSession().getAttribute("admin") == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Please login");

        } else if (title.isEmpty()) {
            responseObject.addProperty("message", "Product title can not be empty");

        } else if (!Util.isInteger(quantity)) {
            responseObject.addProperty("message", "Invalid quantity");
        } else if (Integer.parseInt(quantity) <= 0) {
            responseObject.addProperty("message", "Quantity must be greater than 0");

        } else if (!Util.isDouble(price)) {
            responseObject.addProperty("message", "Invalid price");
        } else if (Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Price must be greater than 0");

        } else if (description.isEmpty()) {
            responseObject.addProperty("message", "Product description can not be empty");

        } else if (!Util.isInteger(color)) {
            responseObject.addProperty("message", "Invalid color");
        } else if (Integer.parseInt(color) == 0) {
            responseObject.addProperty("message", "Please select a valid color");

        } else if (!Util.isInteger(category)) {
            responseObject.addProperty("message", "Invalid category");
        } else if (Integer.parseInt(category) == 0) {
            responseObject.addProperty("message", "Please select a valid category");

        } else if (!Util.isInteger(type)) {
            responseObject.addProperty("message", "Invalid type");
        } else if (Integer.parseInt(type) == 0) {
            responseObject.addProperty("message", "Please select a valid type");

        } else if (!Util.isInteger(size)) {
            responseObject.addProperty("message", "Invalid size");
        } else if (Integer.parseInt(size) == 0) {
            responseObject.addProperty("message", "Please select a valid size");

        } else if (image1.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image one is required");
        } else if (image2.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image two is required");
        } else if (image3.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image three is required");

        } else {

            Color color1 = (Color) s.get(Color.class, Integer.valueOf(color));
            if (color1 == null) {
                responseObject.addProperty("message", "Please select a valid Color!");

            } else {
                Category category1 = (Category) s.get(Category.class, Integer.valueOf(category));
                if (category1 == null) {
                    responseObject.addProperty("message", "Please select a valid Category!");

                } else {
                    Type type1 = (Type) s.get(Type.class, Integer.valueOf(type));
                    if (type1 == null) {
                        responseObject.addProperty("message", "Please select a valid Type!");

                    } else {
                        Size size1 = (Size) s.get(Size.class, Integer.valueOf(size));
                        if (size1 == null) {
                            responseObject.addProperty("message", "Please select a valid Size!");

                        } else {

                            Product p = new Product();
                            p.setName(title);
                            p.setQty(Integer.parseInt(quantity));
                            p.setPrice(Double.parseDouble(price));
                            p.setDescription(description);
                            p.setColor(color1);
                            p.setCategory(category1);
                            p.setType(type1);
                            p.setSize(size1);

                            Status status = (Status) s.get(Status.class, AddProduct.ACTIVE_STATUS_ID);
                            p.setStatus(status);
                            AdminLG admin = (AdminLG) request.getSession().getAttribute("admin");
                            System.out.println(admin);

                            Criteria c1 = s.createCriteria(AdminLG.class);
                            c1.add(Restrictions.eq("username", admin.getUsername()));

                            AdminLG a1 = (AdminLG) c1.uniqueResult();
                            p.setAdminLG(a1);
                            p.setCreated_at(new Date());

                            int id = (int) s.save(p);
                            s.beginTransaction().commit();
                            s.close();

//                          image uploading
                            String appPath = getServletContext().getRealPath(""); //Full path of the Web Pages folder

                            String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images");

                            File productFolder = new File(newPath, String.valueOf(id));
                            productFolder.mkdir();

                            File file1 = new File(productFolder, "image1.png");
                            Files.copy(image1.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            File file2 = new File(productFolder, "image2.png");
                            Files.copy(image2.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            File file3 = new File(productFolder, "image3.png");
                            Files.copy(image3.getInputStream(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                            image uploading end

                            responseObject.addProperty("status", true);
                        }
                    }
                }
            }
        }

        //send response
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
