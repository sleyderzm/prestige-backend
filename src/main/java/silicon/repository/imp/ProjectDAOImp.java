package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Project;
import silicon.repository.ProjectDAO;

import java.util.List;

@Repository
@Transactional
public class ProjectDAOImp implements ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Project project) {
        if(project.getId() == null){
            sessionFactory.getCurrentSession().save(project);
        }else{
            sessionFactory.getCurrentSession().update(project);
        }

    }

    @Override
    public Project findByApiToken(String apiToken) {
        return (Project) sessionFactory.getCurrentSession().createQuery("from Project WHERE api_token = :token")
                .setMaxResults(1).setParameter("token", apiToken).uniqueResult();
    }

    @Override
    public List list() {
        @SuppressWarnings("unchecked")
        Query query = sessionFactory.getCurrentSession().createQuery("from Project");
        return query.list();
    }

    @Override
    public List list(Integer clientId) {
        @SuppressWarnings("unchecked")
        Query query = sessionFactory.getCurrentSession()
                .createQuery("from Project WHERE client_id = :clientId")
                .setParameter("clientId", clientId);
        return query.list();
    }

    @Override
    public Project findById(Integer id){
        Session session = sessionFactory.getCurrentSession();
        return (Project)session.get(Project.class,id);
    }


}