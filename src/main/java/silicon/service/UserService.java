package silicon.service;

import java.util.List;
import silicon.model.User;

public interface UserService {
    void save(User user);
    void delete(User user);
    List list();
    User findById(int id);
    User findByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    User findByTokenResetPassword(String tokenResetPassword);
}
