package uk.ac.ed.collaborate.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@Entity
@Table(name="VC_ULTRA_USERS")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {
    @Id
    @Column(name="USER_ID")
    private String id;

    @Column(name="FIRST_NAME")
    private String firstName;

    @Column(name="LAST_NAME")
    private String lastName;

    @Column(name="USER_NAME")
    private String userName;

    @Column(name="DISPLAY_NAME")
    private String displayName;

    @Column(name="EMAIL")
    private String email;

    @Column(name="USER_ROLE")
    private String role;

    @Column(name="AVATAR_URL")
    private String avatarUrl;

    @Column(name="CREATED_DATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date created;

    @Column(name="MODIFIED_DATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date modified;

    @Column(name="IS_INTERNAL")
    @JsonIgnore
    private boolean internal;

    @Column(name="USER_UID")
    @JsonIgnore
    private String usernameInternal;

    public User() {}

    public User(String displayName, String email, String usernameInternal) {
        this.displayName = displayName;
        this.email = email;
        if (StringUtils.isNotBlank(usernameInternal)) {
            this.usernameInternal = usernameInternal;
            this.internal = true;
        }
    }

    public User(String id, String displayName, String email, String usernameInternal) {
        this(displayName, email, usernameInternal);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return this.modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public String getUsernameInternal() {
        return usernameInternal;
    }

    public void setUsernameInternal(String usernameInternal) {
        this.usernameInternal = usernameInternal;
    }

    public String toString() {
        return "User:["
                + "Id: " + this.id + ",Display Name=" + this.displayName + ",Email=" + this.email
                + ",IsInternal=" + this.internal + ",UID=" + this.usernameInternal
                + "]";
    }
}
