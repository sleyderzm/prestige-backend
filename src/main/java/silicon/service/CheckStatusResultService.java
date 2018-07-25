package silicon.service;

import silicon.model.CheckStatusResult;
import silicon.model.EcheckResponse;
import silicon.model.Subscriber;

import java.util.List;

public interface CheckStatusResultService {
    void save(CheckStatusResult checkStatusResult);
    CheckStatusResult findById(Long id);
    CheckStatusResult findByEcheckResponseId(Long echeckResponseId);
}
