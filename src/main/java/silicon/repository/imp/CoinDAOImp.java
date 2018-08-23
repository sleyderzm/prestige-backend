package silicon.repository.imp;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import silicon.model.Coin;
import silicon.repository.CoinDAO;

import java.util.List;

@Repository
@Transactional
public class CoinDAOImp implements CoinDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Coin coin) {
        if(coin.getId() == null){
            sessionFactory.getCurrentSession().save(coin);
        }else{
            sessionFactory.getCurrentSession().update(coin);
        }

    }

    @Override
    public Coin findBySymbol(String symbol) {
        return (Coin) sessionFactory.getCurrentSession().createQuery("from Coin WHERE symbol = :symbol")
                .setMaxResults(1).setParameter("symbol", symbol).uniqueResult();
    }

    @Override
    public List<Coin> list() {
        @SuppressWarnings("unchecked")
        Query query = sessionFactory.getCurrentSession().createQuery("from Coin");
        final List<Coin> list = query.list();
        return list;
    }

    @Override
    public Coin findById(Long id){
        Session session = sessionFactory.getCurrentSession();
        return (Coin)session.get(Coin.class,id);
    }


}