package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "VerifyAccount", urlPatterns = {"/VerifyAccount"})
public class VerifyAccount extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            jsonResponse.addProperty("status", false);
            jsonResponse.addProperty("message", "Session expired");
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }

        String email = session.getAttribute("email").toString();
        JsonObject input = gson.fromJson(request.getReader(), JsonObject.class);
        String code = input.get("verificationCode").getAsString();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session ses = sf.openSession();
        Transaction tx = ses.beginTransaction();

        Criteria criteria = ses.createCriteria(User.class);
        criteria.add(Restrictions.eq("email", email));
        criteria.add(Restrictions.eq("verification", code));
        User user = (User) criteria.uniqueResult();

        if (user != null) {
            user.setVerification("Verified");
            ses.update(user);
            tx.commit();

            session.setAttribute("user", user);
            jsonResponse.addProperty("status", true);
            jsonResponse.addProperty("message", "Verification successful");
        } else {
            jsonResponse.addProperty("status", false);
            jsonResponse.addProperty("message", "Invalid verification code");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonResponse));
    }
}
