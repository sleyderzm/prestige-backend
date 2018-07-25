package silicon.repository;

import silicon.model.Role;

import java.util.List;

public interface RoleDAO {
    void save(Role role);
    void delete(Role role);
    List list();
    Role findById(int id);
}
