package silicon.repository;

import silicon.model.Client;
import silicon.model.Session;

import java.util.List;

public interface ClientDAO {
    void save(Client client);
    List list();
    Client findById(Integer id);
}
