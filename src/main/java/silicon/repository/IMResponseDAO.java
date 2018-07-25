package silicon.repository;

import silicon.model.IMResponse;
import silicon.model.Session;

import java.util.List;

public interface IMResponseDAO {
    void save(IMResponse imResponse);
    List list(Integer subscriberId);
    IMResponse findById(Integer id);
}
