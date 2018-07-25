package silicon.repository;

import silicon.model.CheckProcessInfo;

public interface CheckProcessInfoDAO {
    void save(CheckProcessInfo checkProcessInfo);
    CheckProcessInfo findById(Long id);
    CheckProcessInfo findByEcheckResponseId(Long echeckResponseId);
}
