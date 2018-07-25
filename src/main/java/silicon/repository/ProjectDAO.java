package silicon.repository;

import silicon.model.Project;

import java.util.List;

public interface ProjectDAO {
    void save(Project project);
    Project findByApiToken(String token);
    List list();
    List list(Integer clientId);
    Project findById(Integer id);
}
