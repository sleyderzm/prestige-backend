package silicon.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transaction_responses")
public class TransactionResponse implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Long id;

    @Column(name="value")
    private Double value;

    @Column(name="payment_method")
    private String paymentMethod;

    @Column(name="from_wallet")
    private String fromWallet;

    @Column(name="to_wallet")
    private String toWallet;

    @Column(name="status")
    private String status;

    @Column(name="error")
    private String error;

    @Column(name="transaction_id")
    private String transactionId;

    public TransactionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getFromWallet() {
        return fromWallet;
    }

    public void setFromWallet(String fromWallet) {
        this.fromWallet = fromWallet;
    }

    public String getToWallet() {
        return toWallet;
    }

    public void setToWallet(String toWallet) {
        this.toWallet = toWallet;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

