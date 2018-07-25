package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.CheckStatusResult;
import silicon.repository.CheckStatusResultDAO;

import java.util.List;

@Repository
@Transactional
public class CheckStatusResultDAOImp implements CheckStatusResultDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(CheckStatusResult checkStatusResult) {
        if(checkStatusResult.getId() == null){
            sessionFactory.getCurrentSession().save(checkStatusResult);
        }else{
            sessionFactory.getCurrentSession().update(checkStatusResult);
        }

    }
    
    @Override
    public CheckStatusResult findById(Long id) {
        return (CheckStatusResult)sessionFactory.getCurrentSession().createQuery("from CheckStatusResult WHERE id = :id")
                .setMaxResults(1).setParameter("id", id).uniqueResult();
    }


    @Override
    public CheckStatusResult findByEcheckResponseId(Long checkStatusResultId) {
        return (CheckStatusResult) sessionFactory.getCurrentSession().createQuery("from CheckStatusResult WHERE echeck_response_id = :checkStatusResultId")
                .setMaxResults(1).setParameter("checkStatusResultId", checkStatusResultId).uniqueResult();
    }
}