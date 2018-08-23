package silicon.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Client;
import silicon.model.Coin;
import silicon.repository.CoinDAO;
import silicon.service.CoinService;

import java.util.List;

@Service
public class CoinServiceImp implements CoinService {

    @Autowired
    private CoinDAO coinDAO;

    @Transactional
    public void save(Coin coin) {
        coinDAO.save(coin);
    }

    public Coin findBySymbol(String symbol) {
        return coinDAO.findBySymbol(symbol);
    }

    @Override
    public Coin findById(Long id) {
        return coinDAO.findById(id);
    }

    @Override
    public List<Coin> list() {
        return coinDAO.list();
    }
}