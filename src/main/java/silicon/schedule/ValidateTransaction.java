package silicon.schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import silicon.handler.MailHandler;
import silicon.handler.TransactionHandler;
import silicon.model.Order;
import silicon.model.TransactionResponse;
import silicon.service.OrderService;

import java.util.Date;
import java.util.List;

@Component
public class ValidateTransaction {

    @Autowired
    OrderService orderService;

    private static final Logger log = LoggerFactory.getLogger(ValidateTransaction.class);

    @Scheduled(fixedRate = 5*60*1000)
    public void validatePendingTransactions() {
        log.info("initializing validate transactions");
        List<Order> orders =  orderService.list(Order.PENDING);
        Date fiveMinutesBefore = new Date(System.currentTimeMillis()-5*60*1000);
        for(Order order : orders){
            if(order.getCreatedAt().after(fiveMinutesBefore)){
                continue;
            }

            TransactionResponse transaction = TransactionHandler.validateTransaction(order);
            if (transaction.getError() == null) {
                order.setStatusCode(Order.ACCEPTED);
                order.setStatusDescription("The transaction was accepted");
            }else{
                order.setStatusDescription(transaction.getError());
                order.setStatusCode(transaction.getStatus());
            }

            orderService.save(order);

            if(!order.getStatusCode().equals(Order.PENDING)){
                MailHandler.sendTransactionStatusResult(order);
            }

        }
    }
}
