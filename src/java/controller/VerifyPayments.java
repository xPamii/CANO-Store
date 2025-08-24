package controller;

import hibernate.HibernateUtil;
import hibernate.Orders;
import hibernate.PaymentStatus;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@WebServlet(name = "VerifyPayments", urlPatterns = {"/VerifyPayments"})
public class VerifyPayments extends HttpServlet {

    private static final int PAYHERE_SUCCESS = 2;
    private static final int PAYMENT_STATUS_PAID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String merchantId = request.getParameter("merchant_id");
        String orderId = request.getParameter("order_id");
        String payhereAmount = request.getParameter("payhere_amount");
        String payhereCurrency = request.getParameter("payhere_currency");
        String statusCode = request.getParameter("status_code");
        String md5sig = request.getParameter("md5sig");

        String merchantSecret = System.getenv("PAYHERE_SECRET");

        try {
            // Build expected hash
            String expectedHash = PayHere.generateMD5(
                    merchantId + orderId + payhereAmount + payhereCurrency + PayHere.generateMD5(merchantSecret)
            );

            if (md5sig != null && md5sig.equalsIgnoreCase(expectedHash)) {

                if (Integer.parseInt(statusCode) == PAYHERE_SUCCESS) {
                    int numericOrderId = extractOrderId(orderId);

                    markAsPaid(numericOrderId);

                    log("Payment verified and order marked as PAID: " + orderId);
                } else {
                    log("Payment not successful for order " + orderId + ". Status: " + statusCode);
                }

                // Always return 200 OK so PayHere doesn’t retry
                response.setStatus(HttpServletResponse.SC_OK);

            } else {
                log("Invalid payment signature for order: " + orderId);
                response.setStatus(HttpServletResponse.SC_OK); // still 200 OK (PayHere best practice)
            }

        } catch (Exception e) {
            log("Exception during payment verification: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_OK); // never 400, avoid retries
        }
    }

    /**
     * Extracts numeric order ID safely from the PayHere order_id.
     * Better: use a known prefix format like "DB-12345" and parse the part after the dash.
     */
    private int extractOrderId(String orderId) {
        try {
            if (orderId.contains("-")) {
                return Integer.parseInt(orderId.split("-")[1]);
            }
            return Integer.parseInt(orderId.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid order_id format: " + orderId, e);
        }
    }

    public void markAsPaid(int orderId) {
        Transaction transaction = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            transaction = session.beginTransaction();

            Orders order = (Orders) session.get(Orders.class, orderId);
            if (order == null) {
                log("Order not found for ID: " + orderId);
                return;
            }

            PaymentStatus paymentStatus = (PaymentStatus) session.get(PaymentStatus.class, PAYMENT_STATUS_PAID);
            if (paymentStatus == null) {
                log("PaymentStatus not found for ID: " + PAYMENT_STATUS_PAID);
                return;
            }

            order.setPaymentStatus(paymentStatus);
            session.merge(order);

            log("Order " + orderId + " updated to PaymentStatus: " + paymentStatus.getStatus());

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log("Error updating order payment status: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}


//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String merchant_id = request.getParameter("merchant_id");
//        String order_id = request.getParameter("order_id");
//        String payhere_amount = request.getParameter("payhere_amount");
//        String payhere_currency = request.getParameter("payhere_currency");
//        String status_code = request.getParameter("status_code");
//        String md5sig = request.getParameter("md5sig");
//
//        String merchantSecret = "NzIyMDk0NjIwMTYwNzMwODgwNzE5MzY1MjI4NDAzNDYwMjkxODY0";
//        String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);
//        String hash = PayHere.generateMD5(merchant_id + order_id + payhere_amount + payhere_currency + merchantSecretMD5);
//
//        if (md5sig.equals(hash) && Integer.parseInt(status_code) == VerifyPayments.PAYHERE_SUCCESS) {
//            response.sendRedirect("invoice.html?orderId=" + order_id);
//
//            System.out.println("Payment Completed. Order Id:" + order_id);
//            String orderId = order_id.substring(3);
//            System.out.println(orderId); // 1
//        }
//    }
//}
