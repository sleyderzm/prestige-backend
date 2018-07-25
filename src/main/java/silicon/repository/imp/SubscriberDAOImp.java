package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.handler.Pagination;
import silicon.model.Project;
import silicon.model.Subscriber;
import silicon.model.User;
import silicon.repository.SubscriberDAO;

import java.util.*;


@Repository
@Transactional
public class SubscriberDAOImp implements SubscriberDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Subscriber subscriber) {
        if(subscriber.getId() != null){
            sessionFactory.getCurrentSession().update(subscriber);
        }else{
            sessionFactory.getCurrentSession().save(subscriber);
        }

    }

    @Override
    public void delete(Subscriber subscriber) {
        sessionFactory.getCurrentSession().delete(subscriber);
    }

    @Override
    public Pagination list(Pagination pagination, Integer projectId) {
        Query query;
        Integer count;
        if(projectId != null){
            count = ((Long)sessionFactory.getCurrentSession().createQuery("SELECT COUNT(1) FROM Subscriber WHERE project_id = :projectId")
                    .setParameter("projectId", projectId).uniqueResult()).intValue();
            query = sessionFactory.getCurrentSession().createQuery("FROM Subscriber WHERE project_id = :projectId ORDER BY created_at DESC")
                    .setParameter("projectId", projectId)
                    .setFirstResult(pagination.getFirstResult())
                    .setMaxResults(pagination.getPerPage());
        }else{
            count = ((Long)sessionFactory.getCurrentSession().createQuery("SELECT COUNT(1) FROM Subscriber").uniqueResult()).intValue();
            query = sessionFactory.getCurrentSession().createQuery("FROM Subscriber ORDER BY created_at DESC")
                    .setFirstResult(pagination.getFirstResult())
                    .setMaxResults(pagination.getPerPage());
        }
        pagination.calculate(query.list(), count);
        return pagination;
    }


    @Override
    public List<Subscriber> list(Project project) {
        Query query;
        Integer projectId = project.getId();
        if(projectId != null){
            query = sessionFactory.getCurrentSession().createQuery("FROM Subscriber WHERE project_id = :projectId ORDER BY created_at DESC")
                    .setParameter("projectId", projectId);
        }else{
            query = sessionFactory.getCurrentSession().createQuery("FROM Subscriber ORDER BY created_at DESC");
        }
        final List<Subscriber> list = query.list();
        return list;
    }

    @Override
    public List<Subscriber> list(User user) {
        Query query;
        Integer userId = user.getId();
        if(userId != null){
            query = sessionFactory.getCurrentSession().createQuery("FROM Subscriber WHERE user_id = :userId ORDER BY created_at DESC")
                    .setParameter("userId", userId);
        }else{
            query = sessionFactory.getCurrentSession().createQuery("FROM Subscriber ORDER BY created_at DESC");
        }
        final List<Subscriber> list = query.list();
        return list;
    }

    @Override
    public Subscriber findById(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        return (Subscriber)session.get(Subscriber.class,id);
    }

    @Override
    public Subscriber findByEmail(String email) {
        return (Subscriber)sessionFactory.getCurrentSession().createQuery("from Subscriber WHERE email = :email")
                .setMaxResults(1).setParameter("email", email).uniqueResult();
    }

    @Override
    public Subscriber findByEmailAndProject(String email, Integer projectId) {
        return (Subscriber)sessionFactory.getCurrentSession().createQuery("from Subscriber WHERE email = :email AND project_id = :projectId")
                .setMaxResults(1).setParameter("email", email).setParameter("projectId", projectId).uniqueResult();
    }

     @Override
     public Subscriber findByAwsAccessKeyId(String awsAccessKeyId) {
         return (Subscriber)sessionFactory.getCurrentSession().createQuery("from Subscriber WHERE aws_access_key_id = :awsAccessKeyId")
                 .setMaxResults(1).setParameter("awsAccessKeyId", awsAccessKeyId).uniqueResult();
     }

    @Override
    public Subscriber findByProjectAndUser(Integer projectId, Integer userId) {
        return (Subscriber)sessionFactory.getCurrentSession().createQuery("from Subscriber WHERE user_id = :userId AND project_id = :projectId")
                .setMaxResults(1).setParameter("userId", userId).setParameter("projectId", projectId).uniqueResult();
    }

    @Override
    public Subscriber findByTransactionId(String transactionId) {
        return (Subscriber) sessionFactory.getCurrentSession().createQuery("from Subscriber WHERE transaction_id = :transactionId")
                .setMaxResults(1).setParameter("transactionId", transactionId).uniqueResult();
    }
    @Override
    public Subscriber findByApiToken(String apiToken) {
        return (Subscriber) sessionFactory.getCurrentSession().createQuery("from Subscriber WHERE api_token = :token")
                .setMaxResults(1).setParameter("token", apiToken).uniqueResult();
    }

    @Override
    public List<Object[]> subscriberPerDay(int days) {

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Subscriber.class);

        ProjectionList projectionList = Projections.projectionList();

        projectionList.add(Projections.sqlGroupProjection("date(created_at) as createdAt", "createdAt", new String[] { "createdAt" }, new Type[] { StandardBasicTypes.DATE }),"createdAt");
        projectionList.add(Projections.rowCount());
        projectionList.add(Projections.sum("contribution"));

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -(days));
        Date daysAgo = cal.getTime();
        criteria.add(Restrictions.ge("createdAt", daysAgo));
        criteria.setProjection(projectionList).addOrder(Order.asc("createdAt"));
        List<Object[]> results = criteria.list();

        return results;
    }


    @Override
    public HashMap contributionPerDay(int days) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Subscriber.class);

        ProjectionList projectionList = Projections.projectionList();

        projectionList.add(Projections.sqlGroupProjection("date(created_at) as createdAt", "createdAt", new String[] { "createdAt" }, new Type[] { StandardBasicTypes.DATE }),"createdAt");
        projectionList.add(Projections.rowCount());
        projectionList.add(Projections.sum("contribution"));

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -(days));
        Date daysAgo = cal.getTime();
        criteria.add(Restrictions.ge("createdAt", daysAgo));
        criteria.setProjection(projectionList).addOrder(Order.asc("createdAt"));
        List<Object[]> results = criteria.list();

        criteria = sessionFactory.getCurrentSession().createCriteria(Subscriber.class);

        projectionList = Projections.projectionList();
        projectionList.add(Projections.sum("contribution"));

         cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -(days));
        daysAgo = cal.getTime();
        criteria.add(Restrictions.lt("createdAt", daysAgo));
        criteria.setProjection(projectionList);

        List<Object[]> sumTotal = criteria.list();

        HashMap map = new HashMap();

        map.put("list" ,results);
        map.put("sumTotal" ,sumTotal);

        return map;
    }
}