package silicon.repository;

import silicon.handler.Pagination;
import silicon.model.Order;
import silicon.model.Project;
import silicon.model.User;

import java.util.List;

public interface OrderDAO {
    void save(Order order);
    Pagination list(Pagination pagination);
    Pagination list(Pagination pagination, User user);
    Order findById(Long id);
    Order findByTransactionIdAndPaymentMethod(String transactionId, String paymentMethod);
    List<Order> list(String status);
    List<Order> list();
    Double balance(User user);
}
