package silicon.handler;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import silicon.model.Subscriber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class Coin {

    private static final String COIN_API = "https://api.coinmarketcap.com/";

    private static final String BTC = "v2/ticker/1/";
    private static final String ETH = "v2/ticker/1027/";
    private static final String NEO = "v2/ticker/1376/";

    public static JsonNode getCoinData(String coin){
        try{
            String url = null;
            if (coin.equals("NEO")) {
                url = Coin.NEO;
            }
            else if(coin.equals("ETH")){
                url = Coin.ETH;
            }
            else if(coin.equals("BTC")){
                url = Coin.BTC;
            }else{
                throw new Exception("Invalid Coin");
            }

            HttpClient client = HttpClientBuilder.create().build();
            URIBuilder builder = new URIBuilder(Coin.COIN_API + url);

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

    public static Double getUSDValue(String coin){

        JsonNode response = Coin.getCoinData(coin);
        if(response == null){
            return null;
        }

        JsonNode data = response.get("data");
        if(data == null){
            return null;
        }

        JsonNode quotes = data.get("quotes");
        if(quotes == null){
            return null;
        }

        JsonNode usd = quotes.get("USD");
        if(usd == null){
            return null;
        }

        JsonNode price = usd.get("price");
        if(price == null){
            return null;
        }

        return price.asDouble();
    }
}