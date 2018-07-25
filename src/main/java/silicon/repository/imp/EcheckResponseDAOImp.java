package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.EcheckResponse;
import silicon.repository.EcheckResponseDAO;

import java.util.List;

@Repository
@Transactional
public class EcheckResponseDAOImp implements EcheckResponseDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(EcheckResponse echeckResponse) {
        if(echeckResponse.getId() == null){
            sessionFactory.getCurrentSession().save(echeckResponse);
        }else{
            sessionFactory.getCurrentSession().update(echeckResponse);
        }

    }

    @Override
    public List list(Integer subscriberId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from EcheckResponse WHERE subscriber_id = :subscriberId")
                .setParameter("subscriberId", subscriberId);
        return query.list();
    }

    @Override
    public EcheckResponse findById(Long id) {
        return (EcheckResponse)sessionFactory.getCurrentSession().createQuery("from EcheckResponse WHERE id = :id")
                .setMaxResults(1).setParameter("id", id).uniqueResult();
    }


    @Override
    public EcheckResponse findByTransactionId(String transactionId) {
        return (EcheckResponse) sessionFactory.getCurrentSession().createQuery("from EcheckResponse WHERE transaction_id = :transactionId")
                .setMaxResults(1).setParameter("transactionId", transactionId).uniqueResult();
    }

    @Override
    public EcheckResponse findByCheckId(String checkId) {
        return (EcheckResponse) sessionFactory.getCurrentSession().createQuery("from EcheckResponse WHERE check_id = :transactionId")
                .setMaxResults(1).setParameter("checkId", checkId).uniqueResult();
    }
}