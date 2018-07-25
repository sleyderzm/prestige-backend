package silicon.service;

import silicon.model.EcheckResponse;
import silicon.model.Subscriber;

import java.util.List;

public interface EcheckResponseService {
    void save(EcheckResponse session);
    EcheckResponse findById(Long id);
    List list(Subscriber subscriber);
    EcheckResponse findByTransactionId(String transactionId);
    EcheckResponse findByCheckId(String checkId);
}
