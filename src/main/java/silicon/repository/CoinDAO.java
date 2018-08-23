package silicon.repository;

import silicon.model.Coin;
import silicon.model.Project;

import java.util.List;

public interface CoinDAO {
    void save(Coin coin);
    Coin findBySymbol(String symbol);
    List<Coin> list();
    Coin findById(Long id);
}
