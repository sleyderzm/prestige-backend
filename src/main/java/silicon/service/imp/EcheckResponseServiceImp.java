package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.EcheckResponse;
import silicon.model.Subscriber;
import silicon.repository.EcheckResponseDAO;
import silicon.service.EcheckResponseService;

import java.util.ArrayList;
import java.util.List;

@Service
public class EcheckResponseServiceImp implements EcheckResponseService {

    @Autowired
    private EcheckResponseDAO echeckResponseDAO;

    @Transactional
    public void save(EcheckResponse echeckResponse) {
        echeckResponseDAO.save(echeckResponse);
    }

    public EcheckResponse findById(Long id) {
        return echeckResponseDAO.findById(id);
    }

    @Override
    public List list(Subscriber subscriber) {
        if(subscriber.getId() != null){
            return echeckResponseDAO.list(subscriber.getId());
        }
        return new ArrayList();
    }

    @Override
    public EcheckResponse findByCheckId(String checkId) {
        return echeckResponseDAO.findByCheckId(checkId);
    }

    @Override
    public EcheckResponse findByTransactionId(String TransactionId) {
        return echeckResponseDAO.findByTransactionId(TransactionId);
    }


}