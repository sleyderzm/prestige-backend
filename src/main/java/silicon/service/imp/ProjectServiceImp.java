package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Client;
import silicon.model.Project;
import silicon.repository.ProjectDAO;
import silicon.service.ProjectService;

import java.util.List;

@Service
public class ProjectServiceImp implements ProjectService {

    @Autowired
    private ProjectDAO projectDAO;

    @Transactional
    public void save(Project project) {
        projectDAO.save(project);
    }

    public Project findByApiToken(String apiToken) {
        return projectDAO.findByApiToken(apiToken);
    }

    @Override
    public Project findById(Integer id) {
        return projectDAO.findById(id);
    }

    @Override
    public List list() {
        return projectDAO.list();
    }


    @Override
    public List list(Client client) {
        if (client != null){
            return projectDAO.list(client.getId());
        }
        return null;
    }

}