package silicon.service;

import silicon.model.Role;

import java.util.List;

public interface RoleService {
    void save(Role role);
    void delete(Role role);
    List list();
    Role findById(Integer id);
}
