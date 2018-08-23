package silicon.schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import silicon.handler.MailHandler;
import silicon.handler.TransactionHandler;
import silicon.model.Coin;
import silicon.model.Order;
import silicon.model.TransactionResponse;
import silicon.service.CoinService;
import silicon.service.OrderService;
import silicon.thread.CoinThread;

import java.util.Date;
import java.util.List;

@Component
public class CoinUSDValue {

    @Autowired
    CoinService coinService;

    private static final Logger log = LoggerFactory.getLogger(CoinUSDValue.class);

    @Scheduled(fixedRate = 5*60*1000)
    public void getCoinUSDValue() {
        log.info("initializing get coin USD value");
        List<Coin> coins =  coinService.list();
        for(Coin coin : coins){
            if(coin.getApiId() == null){
                continue;
            }
            CoinThread coinThread = new CoinThread(coin, coinService);
            coinThread.start();
        }
    }
}
