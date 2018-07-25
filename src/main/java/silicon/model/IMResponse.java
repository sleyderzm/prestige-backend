package silicon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "im_responses")
public class IMResponse implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name="created_at", columnDefinition = "timestamptz")
    private Date createdAt;

    @Column(name="response", columnDefinition="TEXT")
    private String response;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="subscriber_id")
    @NotNull
    private Subscriber subscriber;

    public IMResponse(String response, Subscriber subscriber) {
        this.response = response;
        this.subscriber = subscriber;
        this.createdAt = new Date();
    }

    public IMResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public String toString() {
        return "IMResponse{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", response='" + response + '\'' +
                ", subscriber=" + subscriber +
                '}';
    }
}

