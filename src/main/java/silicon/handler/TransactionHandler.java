package silicon.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import rx.Observable;
import silicon.model.Order;
import silicon.model.TransactionResponse;

import java.math.BigInteger;

public class TransactionHandler{
   private Transaction transaction;
   private TransactionReceipt transactionReceipt;

    public TransactionHandler() {
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public TransactionReceipt getTransactionReceipt() {
        return transactionReceipt;
    }

    public void setTransactionReceipt(TransactionReceipt transactionReceipt) {
        this.transactionReceipt = transactionReceipt;
    }

    private TransactionResponse validateEthTransaction(Order order){

        TransactionResponse transaction = new TransactionResponse();
        String transactionId = order.getTransactionId();
        if(transactionId.length() == 64){
            transactionId = "0x" + transactionId;
        }else if(transactionId.length() != 66){
            transaction.setError("invalid length for transactionId");
            transaction.setStatus(Order.INVALID_LENGTH_TRANSACTION_ID);
            return transaction;
        }

        transaction.setTransactionId(transactionId);

        Web3j web3 = Web3j.build(new HttpService(System.getenv("ETH_TRANSACTION_URL")));
        Observable<EthGetTransactionReceipt> observableReceipt = web3.ethGetTransactionReceipt(transactionId).observable();
        observableReceipt.subscribe(transactionReceipt ->  transactionReceipt.getTransactionReceipt().ifPresent(this::setTransactionReceipt));

        if(this.getTransactionReceipt() == null){
            transaction.setError("Invalid TransactionId");
            transaction.setStatus(Order.NOT_EXIST_TRANSACTION_ID);
            return transaction;
        }

        if(!this.getTransactionReceipt().getStatus().equals("0x1")){
            if(this.getTransactionReceipt().getStatus().equals("0x0")){
                transaction.setError("The transaction is not success");
                transaction.setStatus(Order.REJECTED);
            }else{
                transaction.setError("The transaction is pending");
                transaction.setStatus(Order.PENDING);
            }

            return transaction;
        }
        
        Observable<EthTransaction> observable = web3.ethGetTransactionByHash(transactionId).observable();
        observable.subscribe(ethTransaction ->  ethTransaction.getTransaction().ifPresent(this::setTransaction));
        if(this.getTransaction() == null){
            transaction.setError("Invalid TransactionId");
            transaction.setStatus(Order.NOT_EXIST_TRANSACTION_ID);
            return transaction;
        }

        transaction.setFromWallet(this.getTransaction().getFrom());

        if(!transaction.getFromWallet().toUpperCase().equals(order.getWalletAddress().toUpperCase())){
            transaction.setError("the public address provided doesn't match with public address in transaction");
            transaction.setStatus(Order.INVALID_FROM_WALLET);
            return transaction;
        }

        transaction.setToWallet(this.getTransaction().getTo());

        if(!transaction.getToWallet().toUpperCase().equals(System.getenv("WALLET_ETH").toUpperCase())){
            transaction.setError("the receiver public address in transaction doesn't match with our public address");
            transaction.setStatus(Order.INVALID_TO_WALLET);
            return transaction;
        }

        Double value = this.getTransaction().getValue().doubleValue() * Math.pow(10, -8);
        transaction.setValue(value);

        if(transaction.getValue() < order.getAmountSent()){
            transaction.setError("the transaction have value less than the amount provided");
            transaction.setStatus(Order.INVALID_AMOUNT);
            return transaction;
        }

        return transaction;
    }

    private TransactionResponse validateNeoTransaction(Order order){

        TransactionResponse transaction = new TransactionResponse();
        String transactionId = order.getTransactionId();
        if(transactionId.length() == 66){
            transactionId = transactionId.substring(2);
        }else if(transactionId.length() != 64){
            transaction.setError("invalid Length for transactionId");
            transaction.setStatus(Order.INVALID_LENGTH_TRANSACTION_ID);
            return transaction;
        }

        transaction.setTransactionId(transactionId);

        try {
            HttpClient client = HttpClientBuilder.create().build();
            URIBuilder builder = new URIBuilder(System.getenv("NEO_TRANSACTION_URL") + "/get_transaction/" + transactionId);
            HttpGet g = new HttpGet(builder.build());
            g.setHeader("Content-type", "application/json");
            HttpResponse resp = client.execute(g);
            String json_string = EntityUtils.toString(resp.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode neoResponse = mapper.readTree(json_string);

            if(!neoResponse.hasNonNull("vin")){
                transaction.setError("the transaction Id doesn't exists");
                transaction.setStatus(Order.NOT_EXIST_TRANSACTION_ID);
                return transaction;
            }

            JsonNode vins = neoResponse.get("vin");

            for (final JsonNode vin : vins) {
                if(vin.get("address_hash").asText().toUpperCase().equals(order.getWalletAddress().toUpperCase())){
                    transaction.setFromWallet(order.getWalletAddress());
                    break;
                }
            }

            if(transaction.getFromWallet() == null){
                transaction.setError("the public address provided doesn't match with public address in transaction");
                transaction.setStatus(Order.INVALID_FROM_WALLET);
                return transaction;
            }

            JsonNode vouts = neoResponse.get("vouts");

            for (final JsonNode vout : vouts) {
                if(vout.get("address_hash").asText().toUpperCase().equals(System.getenv("WALLET_NEO").toUpperCase())){
                    transaction.setToWallet(System.getenv("WALLET_NEO"));
                    transaction.setValue(vout.get("value").asDouble());
                    transaction.setPaymentMethod(vout.get("asset").asText());
                    break;
                }
            }


            if(transaction.getToWallet() == null){
                transaction.setError("the receiver public address in transaction doesn't match with our public address");
                transaction.setStatus(Order.INVALID_TO_WALLET);
                return transaction;
            }

            if(!transaction.getPaymentMethod().equals(Order.NEO)){
                transaction.setError("the selected currency doesn't match with currency in transaction");
                transaction.setStatus(Order.INVALID_COIN);
                return transaction;
            }

            if(transaction.getValue() < order.getAmountSent()){
                transaction.setError("the transaction have value less than the amount provided");
                transaction.setStatus(Order.INVALID_AMOUNT);
                return transaction;
            }

        }catch (Exception ex){
            transaction.setError("error when try to validate transactionId in blockchain");
            transaction.setStatus(Order.PENDING);
        }

        return transaction;
    }


    private TransactionResponse validateBtcTransaction(Order order){

        TransactionResponse transaction = new TransactionResponse();
        String transactionId = order.getTransactionId();
        if(transactionId.length() != 64){
            transaction.setError("invalid Length for transactionId");
            transaction.setStatus(Order.INVALID_LENGTH_TRANSACTION_ID);
            return transaction;
        }

        transaction.setTransactionId(transactionId);

        try {
            HttpClient client = HttpClientBuilder.create().build();
            URIBuilder builder = new URIBuilder(System.getenv("BTC_TRANSACTION_URL") + "/txs/" + transactionId);
            HttpGet g = new HttpGet(builder.build());
            g.setHeader("Content-type", "application/json");
            HttpResponse resp = client.execute(g);
            String json_string = EntityUtils.toString(resp.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode btcResponse = mapper.readTree(json_string);

            if(btcResponse.has("error")){
                transaction.setError("the transaction Id doesn't exists");
                transaction.setStatus(Order.NOT_EXIST_TRANSACTION_ID);
                return transaction;
            }

            JsonNode inputs = btcResponse.get("inputs");

            outerloop:
            for (final JsonNode input : inputs) {
                JsonNode addresses = input.get("addresses");
                for (final JsonNode address : addresses) {
                    if(address.asText().toUpperCase().equals(order.getWalletAddress().toUpperCase())){
                        transaction.setFromWallet(order.getWalletAddress());
                        break outerloop;
                    }
                }
            }

            if(transaction.getFromWallet() == null){
                transaction.setError("the public address provided doesn't match with public address in transaction");
                transaction.setStatus(Order.INVALID_FROM_WALLET);
                return transaction;
            }

            JsonNode outputs = btcResponse.get("outputs");

            outerloop:
            for (final JsonNode output : outputs) {
                JsonNode addresses = output.get("addresses");
                for (final JsonNode address : addresses) {
                    if(address.asText().toUpperCase().equals(System.getenv("WALLET_BTC").toUpperCase())){
                        transaction.setToWallet(System.getenv("WALLET_BTC"));
                        BigInteger bigValue = output.get("value").bigIntegerValue();
                        Double value = bigValue.doubleValue() * Math.pow(10, -8);
                        transaction.setValue(value);
                        transaction.setPaymentMethod(Order.BTC);
                        break outerloop;
                    }
                }
            }


            if(transaction.getToWallet() == null){
                transaction.setError("the receiver public address in transaction doesn't match with our public address");
                transaction.setStatus(Order.INVALID_TO_WALLET);
                return transaction;
            }

            if(transaction.getValue() < order.getAmountSent()){
                transaction.setError("the transaction have value less than the amount provided");
                transaction.setStatus(Order.INVALID_AMOUNT);
                return transaction;
            }

        }catch (Exception ex){
            transaction.setError("error when try to validate transactionId in blockchain");
            transaction.setStatus(Order.PENDING);
        }

        return transaction;
    }

    public static TransactionResponse validateTransaction(Order order){

        TransactionHandler transactionHandler = new TransactionHandler();
        TransactionResponse transaction = null;
        String paymentMethod = order.getPaymentMethod();
        if (paymentMethod.equals(Order.ETH)){
            transaction = transactionHandler.validateEthTransaction(order);
        }else if(paymentMethod.equals(Order.NEO)){
            transaction = transactionHandler.validateNeoTransaction(order);
        }else if(paymentMethod.equals(Order.BTC)){
            transaction = transactionHandler.validateBtcTransaction(order);
        }

        return transaction;
    }
}

