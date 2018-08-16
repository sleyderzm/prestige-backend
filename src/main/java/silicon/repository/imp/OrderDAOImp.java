package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.handler.Pagination;
import silicon.model.Order;
import silicon.model.Project;
import silicon.model.User;
import silicon.repository.OrderDAO;

import java.util.List;

@Repository
@Transactional
public class OrderDAOImp implements OrderDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Order order) {
        if(order.getId() != null){
            sessionFactory.getCurrentSession().update(order);
        }else{
            sessionFactory.getCurrentSession().save(order);
        }
    }

    @Override
    public Pagination list(Pagination pagination) {
        Query query;
        Integer count;
        count = ((Long)sessionFactory.getCurrentSession().createQuery("SELECT COUNT(1) FROM Order").uniqueResult()).intValue();
        query = sessionFactory.getCurrentSession().createQuery("FROM Order ORDER BY created_at DESC")
                .setFirstResult(pagination.getFirstResult())
                .setMaxResults(pagination.getPerPage());
        pagination.calculate(query.list(), count);
        return pagination;
    }

    @Override
    public Pagination list(Pagination pagination, User user) {
        Query query;
        Integer count;
        count = ((Long)sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(1) FROM Order WHERE user_id = :userId")
                .setParameter("userId", user.getId()).uniqueResult()).intValue();
        query = sessionFactory.getCurrentSession()
                .createQuery("FROM Order WHERE user_id = :userId ORDER BY created_at DESC")
                .setParameter("userId", user.getId()).setFirstResult(pagination.getFirstResult())
                .setMaxResults(pagination.getPerPage());
        pagination.calculate(query.list(), count);
        return pagination;
    }

    @Override
    public Order findById(Long id) {
        org.hibernate.Session session = sessionFactory.getCurrentSession();
        return (Order)session.get(Order.class,id);
    }


    @Override
    public List<Order> list() {
        Query query;
        query = sessionFactory.getCurrentSession().createQuery("FROM Order ORDER BY created_at DESC");
        final List<Order> list = query.list();
        return list;
    }

    @Override
    public Double balance(User user) {
        return (Double)sessionFactory.getCurrentSession()
                .createQuery("SELECT COALESCE(SUM(amountToken), 0) as balance FROM Order WHERE user_id = :userId AND status_code = :status")
                .setParameter("userId", user.getId()).setParameter("status", Order.ACCEPTED).uniqueResult();
    }

    @Override
    public List<Order> list(String status) {
        Query query;
        query = sessionFactory.getCurrentSession().createQuery("FROM Order WHERE ( payment_method IN ('ETH', 'NEO', 'BTC') AND status_code IS NULL OR status_code = :status )").setParameter("status", status);
        final List<Order> list = query.list();
        return list;
    }

    @Override
    public Order findByTransactionIdAndPaymentMethod(String transactionId, String paymentMethod) {
        return (Order)sessionFactory.getCurrentSession().createQuery("from Order WHERE transaction_id = :transactionId AND payment_method = :paymentMethod")
                .setMaxResults(1).setParameter("transactionId", transactionId).setParameter("paymentMethod", paymentMethod).uniqueResult();
    }

}