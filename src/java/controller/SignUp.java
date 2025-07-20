package controller;


import com.google.gson.Gson;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author pamii
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        Gson gson = new Gson();
        com.google.gson.JsonObject fromJson = gson.fromJson(request.getReader(), com.google.gson.JsonObject.class);

        String firstName = fromJson.get("firstName").getAsString().trim();
        String lastName = fromJson.get("lastName").getAsString().trim();
        final String email = fromJson.get("email").getAsString().trim();
        String password = fromJson.get("password").getAsString().trim();

        com.google.gson.JsonObject responseJson = new com.google.gson.JsonObject();
        responseJson.addProperty("status", false);

        // Validation
        if (firstName.isEmpty()) {
            responseJson.addProperty("message", "First Name cannot be empty!");
        } else if (lastName.isEmpty()) {
            responseJson.addProperty("message", "Last Name cannot be empty!");
        } else if (email.isEmpty()) {
            responseJson.addProperty("message", "Email cannot be empty!");
        } else if (!Util.isEmailValid(email)) {
            responseJson.addProperty("message", "Please enter valid email!");
        } else if (password.isEmpty()) {
            responseJson.addProperty("message", "Password cannot be empty!");
        } else if (!Util.isPasswordValid(password)) {
            responseJson.addProperty("message", "Password must contain upper, lower, number, special char, and be at least 8 characters.");
        } else {
            Session session = null;
            try {
                SessionFactory sf = HibernateUtil.getSessionFactory();
                session = sf.openSession();
                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("email", email));

                if (!criteria.list().isEmpty()) {
                    responseJson.addProperty("message", "User with this email already exists!");
                } else {
                    hibernate.User u = new hibernate.User();
                    u.setFirst_name(firstName);
                    u.setLast_name(lastName);
                    u.setEmail(email);

                    // Optional: hash password
                    // String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                    u.setPassword(password);

                    String verificationCode = Util.generateCode();
                    u.setVerification(verificationCode);
                    u.setCreated_at(new Date());

                    session.beginTransaction();
                    session.save(u);
                    session.getTransaction().commit();

                    // Async mail sending
//                    new Thread(() -> {
//                        Mail.sendMail(email, "CANO Store Verification Code", "<h1>" + verificationCode + "</h1>");
//                    }).start();

                    // Create Session
                    HttpSession ses = request.getSession();
                    ses.setAttribute("email", email);

                    responseJson.addProperty("status", true);
                    responseJson.addProperty("message", "Registration successful. Check your email for verification code.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                responseJson.addProperty("message", "Something went wrong. Please try again.");
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
