package silicon.repository;

import silicon.handler.Pagination;
import silicon.model.Project;
import silicon.model.Subscriber;
import silicon.model.User;

import java.util.HashMap;
import java.util.List;

public interface SubscriberDAO {
    void save(Subscriber subscriber);
    Pagination list(Pagination pagination, Integer projectId);
    List<Subscriber> list(Project project);
    List<Subscriber> list(User user);

    Subscriber findById(Integer id);
    Subscriber findByEmail(String email);
    Subscriber findByEmailAndProject(String email, Integer projectId);
    Subscriber findByApiToken(String token);
    Subscriber findByTransactionId(String transactionId);
    void delete(Subscriber subscriber);
    List<Object[]> subscriberPerDay(int days);
    HashMap contributionPerDay(int days);
    Subscriber findByAwsAccessKeyId(String awsAccessKeyId);
    Subscriber findByProjectAndUser(Integer projectId, Integer userId);
}
