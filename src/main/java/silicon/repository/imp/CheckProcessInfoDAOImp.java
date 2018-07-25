package silicon.repository.imp;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.CheckProcessInfo;
import silicon.repository.CheckProcessInfoDAO;

@Repository
@Transactional
public class CheckProcessInfoDAOImp implements CheckProcessInfoDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(CheckProcessInfo checkProcessInfo) {
        if(checkProcessInfo.getId() == null){
            sessionFactory.getCurrentSession().save(checkProcessInfo);
        }else{
            sessionFactory.getCurrentSession().update(checkProcessInfo);
        }

    }
    
    @Override
    public CheckProcessInfo findById(Long id) {
        return (CheckProcessInfo)sessionFactory.getCurrentSession().createQuery("from CheckProcessInfo WHERE id = :id")
                .setMaxResults(1).setParameter("id", id).uniqueResult();
    }


    @Override
    public CheckProcessInfo findByEcheckResponseId(Long checkProcessInfoId) {
        return (CheckProcessInfo) sessionFactory.getCurrentSession().createQuery("from CheckProcessInfo WHERE echeck_response_id = :checkProcessInfoId")
                .setMaxResults(1).setParameter("checkProcessInfoId", checkProcessInfoId).uniqueResult();
    }
}