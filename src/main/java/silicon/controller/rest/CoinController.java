package silicon.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import silicon.model.Coin;
import silicon.service.CoinService;
import silicon.service.SessionService;
import java.util.List;

@RestController
@RequestMapping("/api/coins")
public class CoinController {

    @Autowired
    SessionService sessionService;

    @Autowired
    CoinService coinService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listCoins() {
        List<Coin> coins = coinService.list();
        return new ResponseEntity<List>(coins, HttpStatus.OK);
    }

}
