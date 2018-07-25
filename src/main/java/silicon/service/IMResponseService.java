package silicon.service;

import silicon.model.IMResponse;
import silicon.model.Session;
import silicon.model.Subscriber;
import silicon.model.User;

import java.util.List;

public interface IMResponseService {
    void save(IMResponse session);
    IMResponse findById(Integer id);
    List list(Subscriber subscriber);
}
