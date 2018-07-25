package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Client;
import silicon.model.User;
import silicon.repository.ClientDAO;

import java.util.List;

@Repository
@Transactional
public class ClientDAOImp implements ClientDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Client client) {
        if(client.getId() != null){
            sessionFactory.getCurrentSession().update(client);
        }else{
            sessionFactory.getCurrentSession().save(client);
        }

    }

    @Override
    public List list() {
        @SuppressWarnings("unchecked")
        Query query = sessionFactory.getCurrentSession().createQuery("from Client");
        return query.list();
    }

    @Override
    public Client findById(Integer id){
        Session session = sessionFactory.getCurrentSession();
        return (Client)session.get(Client.class,id);
    }


}