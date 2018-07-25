package silicon.repository;

import silicon.model.CheckStatusResult;

public interface CheckStatusResultDAO {
    void save(CheckStatusResult checkStatusResult);
    CheckStatusResult findById(Long id);
    CheckStatusResult findByEcheckResponseId(Long echeckResponseId);
}
