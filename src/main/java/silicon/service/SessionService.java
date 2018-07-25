package silicon.service;

import silicon.model.Session;
import silicon.model.User;

import java.util.List;

public interface SessionService {
    void save(Session session);
    Session findByToken(String token);
    List list();
    User getCurrentUser();
}
