package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.handler.Pagination;
import silicon.model.Order;
import silicon.model.Project;
import silicon.model.User;
import silicon.repository.OrderDAO;
import silicon.service.OrderService;

import java.util.List;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderDAO orderDAO;

    @Transactional
    public void save(Order order) {
        orderDAO.save(order);
    }

    public Pagination list(Pagination pagination) {
        return orderDAO.list(pagination);
    }

    public Pagination list(Pagination pagination, User user) {
        return orderDAO.list(pagination, user);
    }

    public List<Order> list(String status) {
        return orderDAO.list(status);
    }

    public Order findByTransactionIdAndPaymentMethod(String transactionId, String paymentMethod) {
        return orderDAO.findByTransactionIdAndPaymentMethod(transactionId, paymentMethod);
    }

    @Override
    public List<Order> list() {
        return orderDAO.list();
    }

    @Override
    public Double balance(User user) {
        return orderDAO.balance(user);
    }

    @Override
    public Order findById(Long id) {
        if(id != null) return orderDAO.findById(id);
        return null;
    }


}