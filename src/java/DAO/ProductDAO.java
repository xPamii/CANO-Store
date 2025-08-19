package DAO;

import hibernate.HibernateUtil;
import hibernate.Product;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author pamii
 */
public class ProductDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<Product> searchProductsByName(String keyword) {
        Session session = null;
        List<Product> results = null;

        try {
            session = sessionFactory.openSession();

            Criteria criteria = session.createCriteria(Product.class);

            // Case-insensitive LIKE
            criteria.add(Restrictions.ilike("name", "%" + keyword + "%"));

            results = criteria.list();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return results;
    }

    public List<Product> getAllProducts() {
        Session session = null;
        List<Product> results = null;

        try {
            session = sessionFactory.openSession();
            Criteria criteria = session.createCriteria(Product.class);
            results = criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return results;
    }

}
