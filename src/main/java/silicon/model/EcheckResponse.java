package silicon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "echeck_responses")
public class EcheckResponse implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name="created_at", columnDefinition = "timestamptz")
    private Date createdAt;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="subscriber_id")
    private Subscriber subscriber;

    @Column(name="check_id")
    private String checkID ;

    @Column(name="amount")
    private Double amount;

    @Column(name="check_amount")
    private Double checkAmount;

    @Column(name="button_id")
    private String buttonID ;

    @Column(name="transaction_id ")
    private String transactionId  ;

    @Column(name="url")
    private String url  ;

    public EcheckResponse() {
        this.createdAt = new Date();
        this.transactionId = UUID.randomUUID().toString();
    }

    public EcheckResponse(Subscriber subscriber, Double amount) {
        this.subscriber = subscriber;
        this.amount = amount;
        this.buttonID = System.getenv("GREEN_MONEY_API_BUTTON_ID");
        this.createdAt = new Date();
        this.transactionId = UUID.randomUUID().toString();

        this.url = System.getenv("GREEN_MONEY_API_URL")
                + "eCheck/eCheck.aspx?EmailAddress=" + subscriber.getEmail()
                + "&NameLast=" + subscriber.getLastName()
                + "&NameFirst=" + subscriber.getFirstName()
                + "&Amount=" + this.amount
                + "&GreenButton_id=" + this.buttonID
                + "&SandBoxButton_id=" + this.buttonID
                + "&TransactionID=" + this.transactionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public String getCheckID() {
        return checkID;
    }

    public void setCheckID(String checkID) {
        this.checkID = checkID;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getCheckAmount() {
        return checkAmount;
    }

    public void setCheckAmount(Double checkAmount) {
        this.checkAmount = checkAmount;
    }

    public String getButtonID() {
        return buttonID;
    }

    public void setButtonID(String buttonID) {
        this.buttonID = buttonID;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

