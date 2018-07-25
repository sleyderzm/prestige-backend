package silicon.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import silicon.handler.ErrorResponse;
import silicon.service.ClientService;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    @NotBlank
    private String name;

    @Column(name="background_color")
    private String backgroundColor;

    @Column(name="font_color")
    private String fontColor;

    @Column(name="accepted_color")
    private String acceptedColor;

    @Column(name="rejected_color")
    private String rejectedColor;

    @Column(name="pending_color")
    private String pendingColor;

    @Column(name="form_title_color")
    private String formTitleColor;

    @JsonBackReference
    @OneToMany(mappedBy="client",fetch = FetchType.LAZY)
    private Set<User> users;

    @JsonBackReference
    @OneToMany(mappedBy="client",fetch = FetchType.LAZY)
    private Set<Project> projects;


    public Client(String name) {
        this.name = name;
    }

    public Client() {
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getAcceptedColor() {
        return acceptedColor;
    }

    public void setAcceptedColor(String acceptedColor) {
        this.acceptedColor = acceptedColor;
    }

    public String getRejectedColor() {
        return rejectedColor;
    }

    public void setRejectedColor(String rejectedColor) {
        this.rejectedColor = rejectedColor;
    }

    public String getPendingColor() {
        return pendingColor;
    }

    public void setPendingColor(String pendingColor) {
        this.pendingColor = pendingColor;
    }

    public String getFormTitleColor() {
        return formTitleColor;
    }

    public void setFormTitleColor(String formTitleColor) {
        this.formTitleColor = formTitleColor;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", fontColor='" + fontColor + '\'' +
                ", acceptedColor='" + acceptedColor + '\'' +
                ", rejectedColor='" + rejectedColor + '\'' +
                ", pendingColor='" + pendingColor + '\'' +
                ", formTitleColor='" + formTitleColor + '\'' +
                '}';
    }

    public static Client getClientFromRequest(Integer clientId, User currentUser, ClientService clientService){
        Client client;
        if(currentUser.isAdminRole()){
            if (clientId == null) {
                return null;
            }
            client = clientService.findById(clientId);
        }else{
            client = currentUser.getClient();
        }
        return client;
    }

    public boolean havePermission(User user){

        if(!user.isAdminRole()){
            Integer clientId = user.getClient().getId();
            return clientId.equals(this.getId());
        }

        return true;
    }

}

