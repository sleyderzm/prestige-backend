package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.IMResponse;
import silicon.model.Session;
import silicon.repository.IMResponseDAO;
import silicon.repository.SessionDAO;

import java.util.List;

@Repository
@Transactional
public class IMResponseDAOImp implements IMResponseDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(IMResponse imResponse) {
        sessionFactory.getCurrentSession().save(imResponse);
    }

    @Override
    public List list(Integer subscriberId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from IMResponse WHERE subscriber_id = :subscriberId")
                .setParameter("subscriberId", subscriberId);
        return query.list();
    }

    @Override
    public IMResponse findById(Integer id) {
        return (IMResponse)sessionFactory.getCurrentSession().createQuery("from IMResponse WHERE id = :id")
                .setMaxResults(1).setParameter("id", id).uniqueResult();
    }
}