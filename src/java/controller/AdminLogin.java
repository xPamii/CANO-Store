package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.AdminLG;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AdminLogin", urlPatterns = {"/AdminLogin"})
public class AdminLogin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        String username = signIn.get("username").getAsString();
        String password = signIn.get("password").getAsString();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (username.isEmpty()) {
            responseObject.addProperty("message2", "Username cannot be empty");

        } else if (password.isEmpty()) {
            responseObject.addProperty("message2", "Password cannot be empty");

        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();

            try {
                Criteria c1 = s.createCriteria(AdminLG.class);

                Criterion crt1 = (Restrictions.eq("username", username));
                Criterion crt2 = (Restrictions.eq("password", password));
                
                c1.add(crt1);
                c1.add(crt2);     

                System.out.println("SignIn attempt: " + username + ", " + password);

                if (c1.list().isEmpty()) {
                    responseObject.addProperty("message2", "Invalid details");
                } else {
                    AdminLG admin = (AdminLG) c1.list().get(0);

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message2", "2");

                    HttpSession ses = request.getSession();
                    ses.setAttribute("username", username);
                    ses.setAttribute("admin", admin); // Storing admin object
                }
            } finally {
                s.close();
            }
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(responseObject));
    }
}
