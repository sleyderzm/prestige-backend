package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Role;
import silicon.repository.RoleDAO;
import silicon.service.RoleService;

import java.util.List;

@Service
public class RoleServiceImp implements RoleService {

    @Autowired
    private RoleDAO roleDAO;

    @Transactional
    public void save(Role role) {
        roleDAO.save(role);
    }

    @Transactional
    public void delete(Role role) {
        roleDAO.delete(role);
    }

    @Transactional(readOnly = true)
    public List list() {
        return roleDAO.list();
    }

    @Override
    public Role findById(Integer id) {
        if(id != null) return roleDAO.findById(id);
        return null;
    }

}