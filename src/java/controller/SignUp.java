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

// 1) Async sending with clean separation
                    new Thread(() -> {
                        String htmlContent = buildVerificationEmailHtml(u.getFirst_name() + " " + u.getLast_name(), verificationCode);
                        Mail.sendMail(email, "CANO Store - Verification Code", htmlContent);
                    }).start();

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

    public static String buildVerificationEmailHtml(String userName, String code) {
        return "<!doctype html>\n"
                + "<html lang=\"en\">\n"
                + "<head>\n"
                + "  <meta charset=\"utf-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width\">\n"
                + "  <title>Verification Code</title>\n"
                + "</head>\n"
                + "<body style=\"margin:0;padding:0;background:#f2f4f7;font-family:Arial,Helvetica,sans-serif;\">\n"
                + "  <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\n"
                + "    <tr>\n"
                + "      <td align=\"center\" style=\"padding:24px 12px;\">\n"
                + "        <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(15,23,42,0.06);\">\n"
                + "          <tr>\n"
                + "            <td style=\"padding:20px 28px;border-bottom:1px solid #eef0f3;\">\n"
                + "              <table width=\"100%\" role=\"presentation\">\n"
                + "                <tr>\n"
                + "                  <td style=\"vertical-align:middle;\">\n"
                + "                    <img src=\"images/icons/CanoLogoFull.png\" alt=\"CANO Store\" width=\"140\" style=\"display:block;border:0;\"/>\n"
                + "                  </td>\n"
                + "                  <td align=\"right\" style=\"color:#6b7280;font-size:13px;\">\n"
                + "                    Verification Code\n"
                + "                  </td>\n"
                + "                </tr>\n"
                + "              </table>\n"
                + "            </td>\n"
                + "          </tr>\n"
                + "          <tr>\n"
                + "            <td style=\"padding:28px;\">\n"
                + "              <h2 style=\"margin:0 0 8px 0;color:#0f172a;font-size:20px;\">Hello " + escapeHtml(userName) + ",</h2>\n"
                + "              <p style=\"margin:0 0 18px 0;color:#475569;line-height:1.5;font-size:15px;\">\n"
                + "                Use the code below to verify your email address for <strong>CANO Store</strong>. This code will expire in <strong>10 minutes</strong>.\n"
                + "              </p>\n"
                + "              <div style=\"margin:18px 0;padding:18px;background:#f8fafc;border:1px dashed #e6eef8;border-radius:8px;text-align:center;\">\n"
                + "                <div style=\"font-family: 'Courier New', monospace;font-size:26px;letter-spacing:4px;color:#0f172a;font-weight:700;\">\n"
                + "                  " + escapeHtml(code) + "\n"
                + "                </div>\n"
                + "              </div>\n"
                + "              <p style=\"margin:14px 0 0 0;\">\n"
                + "                <a href=\"#\" style=\"display:inline-block;padding:10px 18px;background:#2563eb;color:#fff;border-radius:6px;text-decoration:none;font-weight:600;\">\n"
                + "                  Verify now\n"
                + "                </a>\n"
                + "              </p>\n"
                + "              <p style=\"margin:20px 0 0 0;color:#6b7280;font-size:13px;line-height:1.4;\">\n"
                + "                If you didn't request this code, you can safely ignore this email. For help, contact <a href=\"mailto:support@canostore.example\" style=\"color:#2563eb;text-decoration:none;\">support@canostore.example</a>.\n"
                + "              </p>\n"
                + "            </td>\n"
                + "          </tr>\n"
                + "          <tr>\n"
                + "            <td style=\"padding:18px 28px;background:#f8fafc;color:#94a3b8;font-size:12px;text-align:center;\">\n"
                + "              CANO Store • 123 Main Street, Colombo • <a href=\"#\" style=\"color:#2563eb;text-decoration:none;\">Unsubscribe</a>\n"
                + "            </td>\n"
                + "          </tr>\n"
                + "        </table>\n"
                + "        <div style=\"margin-top:12px;color:#94a3b8;font-size:12px;\">\n"
                + "          If the button above doesn't work, copy & paste this code into the app: <strong>" + escapeHtml(code) + "</strong>\n"
                + "        </div>\n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </table>\n"
                + "</body>\n"
                + "</html>";
    }

    public static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
