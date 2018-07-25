package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import silicon.model.IMResponse;
import silicon.model.Subscriber;
import silicon.model.User;
import silicon.repository.IMResponseDAO;
import silicon.service.IMResponseService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class IMResponseServiceImp implements IMResponseService {

    @Autowired
    private IMResponseDAO imResponseDAO;

    @Transactional
    public void save(IMResponse imResponse) {
        imResponseDAO.save(imResponse);
    }

    public IMResponse findById(Integer id) {
        return imResponseDAO.findById(id);
    }

    @Override
    public List list(Subscriber subscriber) {
        if(subscriber.getId() != null){
            return imResponseDAO.list(subscriber.getId());
        }
        return new ArrayList();
    }


}