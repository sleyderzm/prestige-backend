package silicon.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "subscribers")
public class Subscriber {


    public static final int STATUS_PENDING = 1;
    public static final int STATUS_ACCEPTED = 2;
    public static final int STATUS_REJECTED = 3;

    public static final String SHORT_STATUS_PENDING = "R";
    public static final String SHORT_STATUS_ACCEPTED = "A";
    public static final String SHORT_STATUS_REJECT = "D";

    public static final String LARGE_STATUS_PENDING = "UNDECIDED";
    public static final String LARGE_STATUS_ACCEPTED = "ACCEPTED";
    public static final String LARGE_STATUS_REJECT = "REJECTED";

    public static final Integer BIG_CONTRIBUTION = 25000;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Integer id;

    @Column(name="first_name")
    @NotBlank
    private String firstName;

    @Column(name="last_name")
    @NotBlank
    private String lastName;

    @Column(name="email")
    @Email
    @NotBlank
    private String email;

    @Column(name="api_token")
    @NotBlank
    private String apiToken;

    @Column(name="validated", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @NotNull
    private Boolean validated;

    @Column(name="status")
    @NotNull
    private Integer status;

    @Column(name="identity_mind_status")
    private Integer statusIM;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name="birthdate", columnDefinition = "timestamptz")
    private Date birthdate;

    @Column(name="publicAddress")
    private String publicAddress;

    @Column(name="typeAddress")
    private String typeAddress;

    @Column(name="aws_access_key_id")
    private String awsAccessKeyId;


    @Column(name="document_type")
    private String documentType;

    @Column(name="country")
    private String country;

    @JsonIgnore
    @Column(name="fingerprint", columnDefinition="TEXT")
    private String fingerprint;

    @Column(name="state")
    private String state;

    @Column(name="extensionFile")
    private String extensionFile;

    @Column(name="ip")
    private String ip;

    @Column(name="referr")
    private String referr;

    @Column(name="billing_address")
    private String billingAddress;

    @Column(name="transaction_id")
    private String transactionId;

    @Column(name="contribution")
    private Double contribution;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name="created_at", columnDefinition = "timestamptz")
    private Date createdAt;

    @JsonManagedReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="project_id")
    @NotNull
    private Project project;


    @JsonManagedReference
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    @JsonBackReference
    @OneToMany(mappedBy="subscriber",fetch = FetchType.LAZY)
    private Set<IMResponse> imResponses;

    public Subscriber() {
    }

    public Subscriber(String firstName, String lastName, String email, Long birthdate, String publicAddress,
                      String typeAddress, String documentType, Project project, String awsAccessKeyId, String country,
                      String state, String fingerprint, Double contribution,User user,
                      String extensionFile, String ip,String billingAddress, String referr) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.state = state;
        this.extensionFile = extensionFile;
        this.fingerprint = fingerprint;
        this.contribution = contribution;
        if(birthdate != null){
            this.birthdate = new Date(birthdate);
        }else{
            this.birthdate = null;
        }
        this.publicAddress = publicAddress;
        this.typeAddress = typeAddress;
        this.documentType = documentType;
        this.project = project;
        this.apiToken = UUID.randomUUID().toString();
        this.validated = false;
        this.ip = ip;
        this.awsAccessKeyId = awsAccessKeyId;
        this.status = STATUS_PENDING;
        this.createdAt = new Date();
        this.user = user;
        this.billingAddress = billingAddress;
        this.referr = referr;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getReferr() {
        return referr;
    }

    public void setReferr(String referr) {
        this.referr = referr;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getExtensionFile() {
        return extensionFile;
    }

    public void setExtensionFile(String extensionFile) {
        this.extensionFile = extensionFile;
    }

    public Double getContribution() {
        return contribution;
    }

    public void setContribution(Double contribution) {
        this.contribution = contribution;
    }

    public Integer getStatusIM() {
        return statusIM;
    }

    public String getStatusIMName(){
        return getStatusName(this.statusIM);
    }

    public void setStatusIM(Integer statusIM) {
        this.statusIM = statusIM;
    }

    public void setStatusIM(String statusIM) {
        switch (statusIM) {
            case SHORT_STATUS_ACCEPTED:  this.statusIM = STATUS_ACCEPTED;
                break;
            case LARGE_STATUS_ACCEPTED:  this.statusIM = STATUS_ACCEPTED;
                break;
            case SHORT_STATUS_PENDING:  this.statusIM = STATUS_PENDING;
                break;
            case LARGE_STATUS_PENDING:  this.statusIM = STATUS_PENDING;
                break;
            case SHORT_STATUS_REJECT:  this.statusIM = STATUS_REJECTED;
                break;
            case LARGE_STATUS_REJECT:  this.statusIM = STATUS_REJECTED;
                break;
            default: this.statusIM = STATUS_PENDING;
                break;
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public void setBirthdate(Long birthdate) {
        if(birthdate != null){
            this.birthdate = new Date(birthdate);
        }else{
            this.birthdate = null;
        }
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public void setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
    }

    public String getTypeAddress() {
        return typeAddress;
    }

    public void setTypeAddress(String typeAddress) {
        this.typeAddress = typeAddress;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Project getProject() {
        return project;
    }

    public String getProjectName(){
        if(this.getProject() != null)
            return this.getProject().getName();

        return null;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName(){
        return getStatusName(this.status);
    }

    private static String getStatusName(Integer status){
        String statusName;

        if(status == null){
            return null;
        }

        switch (status){
            case STATUS_PENDING: statusName = "Pending";
                break;
            case STATUS_ACCEPTED: statusName = "Accepted";
                break;
            case STATUS_REJECTED: statusName = "Rejected";
                break;
            default: statusName = null;

        }

        return statusName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", documentType='" + documentType + '\'' +
                ", createdAt=" + createdAt +
                ", apiToken=" + apiToken +
                ", project_id=" + project.getId() +
                '}';
    }
}

