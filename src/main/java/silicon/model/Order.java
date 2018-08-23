package silicon.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import silicon.handler.Utils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "orders")
public class Order implements Serializable{

    public static final String NEO = "NEO";
    public static final String BTC = "BTC";
    public static final String ETH = "ETH";
    public static final String USD = "USD";


    public static final String ACCEPTED = "0000";
    public static final String PENDING = "0001";
    public static final String REJECTED = "0002";
    public static final String NOT_EXIST_TRANSACTION_ID = "0003";
    public static final String INVALID_LENGTH_TRANSACTION_ID = "0004";
    public static final String INVALID_FROM_WALLET = "0005";
    public static final String INVALID_TO_WALLET = "0006";
    public static final String INVALID_AMOUNT = "0007";
    public static final String INVALID_COIN = "0008";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Long id;

    @Column(name="payment_method")
    private String paymentMethod;

    @Column(name="amount_token")
    private Double amountToken;

    @Column(name="amount_sent")
    private Double amountSent;

    @Column(name="wallet_address")
    private String walletAddress;

    @Column(name="status_code")
    private String statusCode;

    @Column(name="status_description")
    private String statusDescription;

    @Column(name="transaction_id")
    private String transactionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_at", columnDefinition = "timestamptz")
    private Date createdAt;

    @JsonManagedReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    @NotNull
    private User user;

    public static final Double TOKEN_PRICE = 0.05;

    public Order() {
        this.createdAt = new Date();
    }

    public Order(Coin coin, Double amountSent, String walletAddress, String transactionId, User user, String statusCode) {
        this.paymentMethod = coin.getSymbol();
        this.amountSent = amountSent;
        this.walletAddress = walletAddress;
        this.transactionId = transactionId;
        this.user = user;
        this.statusCode = statusCode;
        calculateAmountToken(coin);
        this.createdAt = new Date();
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    private void calculateAmountToken(Coin coin){
        this.amountToken = (this.amountSent * coin.getValue()) / Order.TOKEN_PRICE;
        this.amountToken = Utils.round8Decimals(this.amountToken);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getAmountToken() {
        return amountToken;
    }

    public void setAmountToken(Double amountToken) {
        this.amountToken = amountToken;
    }

    public Double getAmountSent() {
        return amountSent;
    }

    public void setAmountSent(Double amountSent) {
        this.amountSent = amountSent;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName(){
        if(this.user != null){
            return this.user.getFirstName() + " " + this.user.getLastName();
        }
        return null;
    }
    public String getEmail(){
        if(this.user != null){
            return this.user.getEmail();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", amountToken=" + amountToken +
                ", amountSent=" + amountSent +
                ", walletAddress='" + walletAddress + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}

