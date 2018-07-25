package silicon.repository;

import silicon.model.Session;

import java.util.List;

public interface SessionDAO {
    void save(Session session);
    List list();
    Session findByToken(String token);
}
