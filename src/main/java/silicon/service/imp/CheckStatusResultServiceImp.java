package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.CheckStatusResult;
import silicon.model.Subscriber;
import silicon.repository.CheckStatusResultDAO;
import silicon.service.CheckStatusResultService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckStatusResultServiceImp implements CheckStatusResultService {

    @Autowired
    private CheckStatusResultDAO checkStatusResultDAO;

    @Transactional
    public void save(CheckStatusResult checkStatusResult) {
        checkStatusResultDAO.save(checkStatusResult);
    }

    public CheckStatusResult findById(Long id) {
        return checkStatusResultDAO.findById(id);
    }

    @Override
    public CheckStatusResult findByEcheckResponseId(Long echeckResponseId) {
        return checkStatusResultDAO.findByEcheckResponseId(echeckResponseId);
    }


}