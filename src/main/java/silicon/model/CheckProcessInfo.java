package silicon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

@Entity
@Table(name = "echeck_status_result")
public class CheckProcessInfo implements Serializable{

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

    @Column(name="process_type")
    private String processType ;

    @Column(name="process_id")
    private String process_ID ;

    @Column(name="name_first")
    private String nameFirst ;

    @Column(name="name_last")
    private String nameLast ;

    @Column(name="company_name")
    private String companyName ;

    @Column(name="email_address")
    private String emailAddress ;

    @Column(name="phone")
    private String phone ;

    @Column(name="phone_extension")
    private String phoneExtension ;

    @Column(name="address1")
    private String address1 ;

    @Column(name="address2")
    private String address2 ;

    @Column(name="city")
    private String city ;

    @Column(name="state")
    private String state ;

    @Column(name="zip")
    private String zip ;

    @Column(name="country")
    private String country ;

    @Column(name="check_memo")
    private String checkMemo ;

    @Column(name="check_amount")
    private String checkAmount ;

    @Column(name="check_date")
    private String checkDate ;

    @Column(name="check_number")
    private String checkNumber ;

    @Column(name="recurring_type")
    private String recurringType ;

    @Column(name="recurring_offset")
    private String recurringOffset ;

    @Column(name="recurring_payments")
    private String recurringPayments ;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="echeck_response_id")
    private EcheckResponse echeckResponse;

    public CheckProcessInfo() {
        this.createdAt = new Date();
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

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getProcess_ID() {
        return process_ID;
    }

    public void setProcess_ID(String process_ID) {
        this.process_ID = process_ID;
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneExtension() {
        return phoneExtension;
    }

    public void setPhoneExtension(String phoneExtension) {
        this.phoneExtension = phoneExtension;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCheckMemo() {
        return checkMemo;
    }

    public void setCheckMemo(String checkMemo) {
        this.checkMemo = checkMemo;
    }

    public String getCheckAmount() {
        return checkAmount;
    }

    public void setCheckAmount(String checkAmount) {
        this.checkAmount = checkAmount;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getRecurringType() {
        return recurringType;
    }

    public void setRecurringType(String recurringType) {
        this.recurringType = recurringType;
    }

    public String getRecurringOffset() {
        return recurringOffset;
    }

    public void setRecurringOffset(String recurringOffset) {
        this.recurringOffset = recurringOffset;
    }

    public String getRecurringPayments() {
        return recurringPayments;
    }

    public void setRecurringPayments(String recurringPayments) {
        this.recurringPayments = recurringPayments;
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

