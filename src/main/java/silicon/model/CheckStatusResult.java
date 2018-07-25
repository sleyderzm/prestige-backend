package silicon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

@Entity
@Table(name = "echeck_status_result")
public class CheckStatusResult implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name="created_at", columnDefinition = "timestamptz")
    private Date createdAt;

    @Column(name="result")
    private String result ;

    @Column(name="result_description")
    private String resultDescription ;

    @Column(name="verify_result")
    private String verifyResult ;

    @Column(name="verify_result_description")
    private String verifyResultDescription ;

    @Column(name="verify_overriden")
    private String verifyOverriden ;

    @Column(name="deleted")
    private String deleted ;

    @Column(name="deleted_date")
    private String deletedDate ;

    @Column(name="processed")
    private String processed ;

    @Column(name="processed_date")
    private String processedDate ;

    @Column(name="rejected")
    private String rejected ;

    @Column(name="rejected_date")
    private String rejectedDate ;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="echeck_response_id")
    private EcheckResponse echeckResponse;

    public CheckStatusResult() {
        this.createdAt = new Date();
    }

    public CheckStatusResult(String result, String resultDescription, String verifyResult, String verifyResultDescription, String verifyOverriden, String deleted, String deletedDate, String processed, String processedDate, String rejected, String rejectedDate, EcheckResponse echeckResponse) {
        this.result = result;
        this.resultDescription = resultDescription;
        this.verifyResult = verifyResult;
        this.verifyResultDescription = verifyResultDescription;
        this.verifyOverriden = verifyOverriden;
        this.deleted = deleted;
        this.deletedDate = deletedDate;
        this.processed = processed;
        this.processedDate = processedDate;
        this.rejected = rejected;
        this.rejectedDate = rejectedDate;
        this.echeckResponse = echeckResponse;
        this.createdAt = new Date();
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(String verifyResult) {
        this.verifyResult = verifyResult;
    }

    public String getVerifyResultDescription() {
        return verifyResultDescription;
    }

    public void setVerifyResultDescription(String verifyResultDescription) {
        this.verifyResultDescription = verifyResultDescription;
    }

    public String getVerifyOverriden() {
        return verifyOverriden;
    }

    public void setVerifyOverriden(String verifyOverriden) {
        this.verifyOverriden = verifyOverriden;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(String deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public String getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(String processedDate) {
        this.processedDate = processedDate;
    }

    public String getRejected() {
        return rejected;
    }

    public void setRejected(String rejected) {
        this.rejected = rejected;
    }

    public String getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public EcheckResponse getEcheckResponse() {
        return echeckResponse;
    }

    public void setEcheckResponse(EcheckResponse echeckResponse) {
        this.echeckResponse = echeckResponse;
    }

    public void setAttribute(String attribute, String value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(attribute);
        field.set(this, value);
    }
}

