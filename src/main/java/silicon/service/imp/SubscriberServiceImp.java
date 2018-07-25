package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.handler.Pagination;
import silicon.model.Project;
import silicon.model.Subscriber;
import silicon.model.Client;
import silicon.model.User;
import silicon.repository.SubscriberDAO;
import silicon.service.SubscriberService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SubscriberServiceImp implements SubscriberService {

    @Autowired
    private SubscriberDAO subscriberDAO;

    @Transactional
    public void save(Subscriber subscriber) {
        subscriberDAO.save(subscriber);
    }

    @Transactional
    public void delete(Subscriber subscriber) {
        subscriberDAO.delete(subscriber);
    }

    public Subscriber findById(Integer id) {
        return subscriberDAO.findById(id);
    }

    @Override
    public Pagination list(Pagination pagination) {
        return subscriberDAO.list(pagination, null);
    }

    @Override
    public Pagination list(Pagination pagination, Project project) {
        if(project.getId() != null){
            return subscriberDAO.list(pagination, project.getId());
        }
        return new Pagination();
    }

    @Override
    public List<Subscriber> list(Project project) {
        return subscriberDAO.list(project);
    }

    @Override
    public List<Subscriber> list(User user) {
        return subscriberDAO.list(user);
    }


    @Override
    public Subscriber findByEmail(String email){
        return subscriberDAO.findByEmail(email);
    }

    public Subscriber findByEmailAndProject(String email, Project project){
        if(project.getId() != null){
            return subscriberDAO.findByEmailAndProject(email, project.getId());
        }
        return null;
    }

    @Override
    public Subscriber findByAwsAccessKeyId(String awsAccessKeyId){
        return subscriberDAO.findByAwsAccessKeyId(awsAccessKeyId);
    }

    public Subscriber findByProjectAndUser(Project project, User user){
        if(project.getId() != null && user.getId() != null){
            return subscriberDAO.findByProjectAndUser(project.getId(), user.getId());
        }
        return null;
    }

    public Subscriber findByApiToken(String apiToken) {
        return subscriberDAO.findByApiToken(apiToken);
    }
    
    public Subscriber findByTransactionId(String transactionId) {
        return subscriberDAO.findByTransactionId(transactionId);
    }


    @Override
    public List<Object[]> subscriberPerDay(int days) {
        return subscriberDAO.subscriberPerDay(days);
    }

    @Override
    public HashMap contributionPerDay(int days) {
        return subscriberDAO.contributionPerDay(days);
    }
}