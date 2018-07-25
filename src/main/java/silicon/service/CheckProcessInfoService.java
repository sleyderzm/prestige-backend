package silicon.service;

import silicon.model.CheckProcessInfo;

public interface CheckProcessInfoService {
    void save(CheckProcessInfo checkProcessInfo);
    CheckProcessInfo findById(Long id);
    CheckProcessInfo findByEcheckResponseId(Long echeckResponseId);
}
