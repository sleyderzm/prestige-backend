package silicon.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    @NotBlank
    private String name;

    @Column(name="aws_access_key_id")
    private String awsAccessKeyId;

    @Column(name="website")
    private String website;

    @Column(name="white_paper")
    private String whitePaper;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    @Column(name="api_token")
    @NotBlank
    private String apiToken;

    @JsonManagedReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="client_id")
    @NotNull
    private Client client;


    @JsonBackReference
    @OneToMany(mappedBy="project",fetch = FetchType.EAGER)
    private Set<Subscriber> subscribers;

    @JsonBackReference
    @OneToMany(mappedBy="project",fetch = FetchType.EAGER)
    private Set<Post> posts;

    public Project(String name, String description, String awsAccessKeyId, String website, String whitePaper,Client client) {
        this.name = name;
        this.client = client;
        this.description = description;
        this.awsAccessKeyId = awsAccessKeyId;
        this.apiToken = website;
        this.whitePaper = whitePaper;
        this.apiToken = UUID.randomUUID().toString();
    }

    public Project() {
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWhitePaper() {
        return whitePaper;
    }

    public void setWhitePaper(String whitePaper) {
        this.whitePaper = whitePaper;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Set<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Boolean havePermission(User user){
        if(!user.isAdminRole()){
            Client currentClient = user.getClient();
            Client projectClient = this.getClient();
            if(currentClient == null || projectClient == null || !currentClient.getId().equals(projectClient.getId())){
                return false;
            }
        }
        return true;
    }

}

