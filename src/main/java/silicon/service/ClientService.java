package silicon.service;

import silicon.model.Client;
import silicon.model.Session;
import silicon.model.User;

import java.util.List;

public interface ClientService {
    void save(Client client);
    List list();
    Client findById(Integer id);
}
