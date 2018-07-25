package silicon.repository;

import silicon.model.EcheckResponse;

import java.util.List;

public interface EcheckResponseDAO {
    void save(EcheckResponse echeckResponse);
    List list(Integer subscriberId);
    EcheckResponse findById(Long id);
    EcheckResponse findByCheckId(String checkId);
    EcheckResponse findByTransactionId(String transactionId);
}
