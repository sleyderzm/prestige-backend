package silicon.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import silicon.handler.Utils;
import java.util.Calendar;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="expiration_date", columnDefinition = "timestamptz")
    private Date expirationDate;

    @Column(name="token")
    @NotBlank
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name="created_at", columnDefinition = "timestamptz")
    private Date createdAt;


    @JsonManagedReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    @NotNull
    private User user;

    public Session(Integer id, User user, String token, Date createdAt) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.createdAt = createdAt;
    }

    public Session() {
    }

    public Session(User user, String token) {
        this.user = user;
        this.token = token;
        this.createdAt = new Date();
        this.expirationDate = Utils.addTime(this.createdAt, Calendar.HOUR, 1);;
    }
    public Boolean isValid(){
        Date now = new Date();
        if(this.expirationDate != null && this.expirationDate.before(now)){
            return false;
        }
        return true;
    }
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", user='" + user.toString() + '\'' +
                ", token='" + token + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

