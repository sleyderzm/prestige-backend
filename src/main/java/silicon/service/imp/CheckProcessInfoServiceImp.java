package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.CheckProcessInfo;
import silicon.repository.CheckProcessInfoDAO;
import silicon.service.CheckProcessInfoService;

@Service
public class CheckProcessInfoServiceImp implements CheckProcessInfoService {

    @Autowired
    private CheckProcessInfoDAO checkProcessInfoDAO;

    @Transactional
    public void save(CheckProcessInfo checkProcessInfo) {
        checkProcessInfoDAO.save(checkProcessInfo);
    }

    public CheckProcessInfo findById(Long id) {
        return checkProcessInfoDAO.findById(id);
    }

    @Override
    public CheckProcessInfo findByEcheckResponseId(Long echeckResponseId) {
        return checkProcessInfoDAO.findByEcheckResponseId(echeckResponseId);
    }


}