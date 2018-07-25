package silicon.service;

import silicon.handler.Pagination;
import silicon.model.*;

import java.util.HashMap;
import java.util.List;

public interface SubscriberService {
    void save(Subscriber subscriber);
    void delete(Subscriber subscriber);
    Pagination list(Pagination pagination, Project project);
    Pagination list(Pagination pagination);
    List<Subscriber> list(Project project);
    List<Subscriber> list(User user);
    Subscriber findById(Integer id);
    Subscriber findByEmail(String email);
    Subscriber findByEmailAndProject(String email, Project project);
    Subscriber findByApiToken(String token);
    Subscriber findByTransactionId(String transactionId);
    List<Object[]> subscriberPerDay(int days);
    HashMap contributionPerDay(int days);
    Subscriber findByAwsAccessKeyId(String awsAccessKeyId);
    Subscriber findByProjectAndUser(Project project, User user);
}
