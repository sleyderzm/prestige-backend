package silicon.service;

import silicon.handler.Pagination;
import silicon.model.Order;
import silicon.model.Project;
import silicon.model.User;

import java.util.List;

public interface OrderService {
    void save(Order order);
    Pagination list(Pagination pagination);
    Pagination list(Pagination pagination, User user);
    List<Order> list();
    Double balance(User user);
    List<Order> list(String status);
    Order findById(Long id);
    Order findByTransactionIdAndPaymentMethod(String transactionId, String paymentMethod);
}
