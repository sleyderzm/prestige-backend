package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Session;
import silicon.model.User;
import silicon.repository.SessionDAO;

import java.util.List;

@Repository
@Transactional
public class SessionDAOImp implements SessionDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Session session) {
        if(session.getId() != null){
            sessionFactory.getCurrentSession().update(session);
        }else{
            sessionFactory.getCurrentSession().save(session);
        }
    }

    @Override
    public List list() {
        Query query = sessionFactory.getCurrentSession().createQuery("from Session");
        return query.list();
    }

    @Override
    public Session findByToken(String token) {
        return (Session)sessionFactory.getCurrentSession().createQuery("from Session WHERE token = :token")
                .setMaxResults(1).setParameter("token", token).uniqueResult();
    }
}