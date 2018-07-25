package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Client;
import silicon.repository.ClientDAO;
import silicon.service.ClientService;

import java.util.List;

@Service
public class ClientServiceImp implements ClientService {

    @Autowired
    private ClientDAO clientDAO;

    @Transactional
    public void save(Client client) {
        clientDAO.save(client);
    }

    @Override
    public Client findById(Integer id) {
        return clientDAO.findById(id);
    }

    @Override
    public List list() {
        return clientDAO.list();
    }

}