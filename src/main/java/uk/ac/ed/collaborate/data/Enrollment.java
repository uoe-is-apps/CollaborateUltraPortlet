package uk.ac.ed.collaborate.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@Entity
@Table(name="VC_ULTRA_SESSION_ENROLS")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Enrollment {
    @Id
    @Column(name="ENROLMENT_ID")
    private String id;

    @Column(name="EDITING_PERMISSION")
    private String editingPermission;

    @Column(name="LAUNCHING_ROLE")
    private String launchingRole;

    @Column(name="USER_ID")
    private String userId;

    @Column(name="SESSION_ID")
    @JsonIgnore
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SESSION_ID", insertable = false, updatable = false)
    private Session session;

    public Enrollment() {}

    public Enrollment(String sessionId, String userId, String launchingRole, String editingPermission) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.launchingRole = launchingRole;
        this.editingPermission = editingPermission;
    }

    public Enrollment(String id, String sessionId, String userId, String launchingRole, String editingPermission) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.launchingRole = launchingRole;
        this.editingPermission = editingPermission;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEditingPermission() {
        return this.editingPermission;
    }

    public void setEditingPermission(String editingPermission) {
        this.editingPermission = editingPermission;
    }

    public String getLaunchingRole() {
        return this.launchingRole;
    }

    public void setLaunchingRole(String launchingRole) {
        this.launchingRole = launchingRole;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String toString() {
        return "Enrollment["
                + "Id:" + this.id + ",SessionId:" + this.sessionId
                + ",UserId:" + this.userId + ",LaunchingRole:" + this.launchingRole
                + ",EditingPermission:" + this.editingPermission
                + "]";
    }
}
