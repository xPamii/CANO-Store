package DAO;

import hibernate.HibernateUtil;
import hibernate.Orders;
import hibernate.PaymentStatus;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

/**
 * Order Data Access Object
 */
public class OrderDAO {

    private static final int PAYMENT_STATUS = 1;

    public Orders getOrderWithItems(int orderId) {
        Transaction transaction = null;
        Orders order = null;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(Orders.class, "o");
            criteria.add(Restrictions.eq("id", orderId));

            // Join user and address
            criteria.setFetchMode("user", FetchMode.JOIN);
            criteria.setFetchMode("address", FetchMode.JOIN);

            // Join order items
            criteria.setFetchMode("items", FetchMode.JOIN);
            criteria.createAlias("items.product", "product", CriteriaSpecification.LEFT_JOIN);
            criteria.createAlias("items.product.color", "color", CriteriaSpecification.LEFT_JOIN);
            criteria.createAlias("items.product.size", "size", CriteriaSpecification.LEFT_JOIN);

            // Return single order
            order = (Orders) criteria.uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return order;
    }


    public void markAsPaid(int orderId) {
        Transaction transaction = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            transaction = session.beginTransaction();

            Orders order = (Orders) session.get(Orders.class, orderId);
            if (order != null) {
                
                PaymentStatus paymentStatus = (PaymentStatus) session.get(PaymentStatus.class, OrderDAO.PAYMENT_STATUS);

                order.setPaymentStatus(paymentStatus);
                session.update(order);
                System.out.println(" Order " + orderId + " marked as PAID via Hibernate.");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
