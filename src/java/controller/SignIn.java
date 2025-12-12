package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author pamii
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        // CORS headers
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        String email = signIn.get("email").getAsString();
        String password = signIn.get("password").getAsString();

        JsonObject responseObject = new JsonObject();

        responseObject.addProperty("status", false);

        if (email.isEmpty()) {
            responseObject.addProperty("message2", "Email cannot be empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message2", "Please enter valid email !");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message2", "Password cannot be empty");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            Criteria c1 = s.createCriteria(User.class);

            Criterion crt1 = Restrictions.eq("email", email);
            Criterion crt2 = Restrictions.eq("password", password);

            c1.add(crt1);
            c1.add(crt2);
//            System.out.println("SignIn attempt: " + email + ", " + password);

            if (c1.list().isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message2", "Invaild details");

            } else {

                User user = (User) c1.list().get(0);

                responseObject.addProperty("status", true);

                HttpSession ses = request.getSession();

                if (!user.getVerification().equals("Verified")) {

                    // Session creation
                    ses.setAttribute("email", email);

                    responseObject.addProperty("message2", "1");
                } else {
                    ses.setAttribute("user", user);
                    responseObject.addProperty("message2", "2");
                }
            }
            s.close();
        }

        response.setContentType(
                "application/json");
        response.setCharacterEncoding(
                "UTF-8");
        response.getWriter()
                .write(gson.toJson(responseObject));
    }

}
