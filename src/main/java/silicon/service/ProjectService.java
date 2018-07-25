package silicon.service;

import silicon.model.Client;
import silicon.model.Project;

import java.util.List;

public interface ProjectService {
    void save(Project project);
    Project findByApiToken(String token);
    List list();
    Project findById(Integer id);
    List list(Client client);
}
