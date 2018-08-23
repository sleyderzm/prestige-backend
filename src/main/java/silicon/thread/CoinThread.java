package silicon.thread;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import silicon.model.Coin;
import silicon.service.CoinService;

import java.util.Date;

public class CoinThread extends Thread {

    private static final String COIN_API = "https://api.coinmarketcap.com/";

    private Coin coin;
    private CoinService coinService;


    public CoinThread(Coin coin,CoinService coinService) {
        this.coin = coin;
        this.coinService = coinService;
    }

    private static JsonNode getCoinData(Coin coin){
        try{
            HttpClient client = HttpClientBuilder.create().build();
            URIBuilder builder = new URIBuilder(CoinThread.COIN_API + "v2/ticker/" + coin.getApiId() + "/");

            if(builder == null){
                throw new Exception("Invalid URIBuilder");
            };

            HttpGet g = new HttpGet(builder.build());
            g.setHeader("Content-type", "application/json");
            HttpResponse resp = client.execute(g);
            String json_string = EntityUtils.toString(resp.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(json_string);
            return actualObj;
        }catch (Exception e){
            System.out.println(e.toString());
        }

        return null;
    }

    public void run() {
        JsonNode response = CoinThread.getCoinData(coin);
        if(response == null){
            return;
        }

        JsonNode data = response.get("data");
        if(data == null){
            return;
        }

        JsonNode quotes = data.get("quotes");
        if(quotes == null){
            return;
        }

        JsonNode usd = quotes.get("USD");
        if(usd == null){
            return;
        }

        JsonNode price = usd.get("price");
        if(price == null){
            return;
        }

        coin.setValue(price.asDouble());
        coin.setUpdatedAt(new Date());
        coinService.save(coin);
    }
}
