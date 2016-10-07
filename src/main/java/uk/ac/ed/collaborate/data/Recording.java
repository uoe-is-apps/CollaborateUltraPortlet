package uk.ac.ed.collaborate.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@Entity
@Table(name="VC_ULTRA_RECORDINGS")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recording {
    @Id
    @Column(name="RECORDING_ID")
    private String id;

    @Column(name="SESSION_START_TIME")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date sessionStartTime;

    @Column(name = "CAN_DOWNLOAD")
    private boolean canDownload;

    @Column(name="DURATION")
    private String duration;

    @Column(name="SESSION_NAME")
    private String sessionName;

    @Column(name = "OWNER_ID")
    private String ownerId;

    @Column(name="NAME")
    private String name;

    @Column(name="RESTRICTED")
    private boolean restricted;

    @Column(name="MEDIA_NAME")
    private String mediaName;

    @Column(name = "EDITING_PERMISSION")
    private String editingPermission;

    @Column(name="CREATED_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

    @Column(name="MODIFIED_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date modified;

    @Transient
    private boolean currentUserCanDelete;

    @Transient
    private String playUrl;

    @Transient
    private String downloadUrl;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getSessionStartTime() {
        return this.sessionStartTime;
    }

    public void setSessionStartTime(Date sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public boolean isCanDownload() {
        return this.canDownload;
    }

    public void setCanDownload(boolean canDownload) {
        this.canDownload = canDownload;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSessionName() {
        return this.sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRestricted() {
        return this.restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public String getMediaName() {
        return this.mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getEditingPermission() {
        return this.editingPermission;
    }

    public void setEditingPermission(String editingPermission) {
        this.editingPermission = editingPermission;
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

    public boolean isCurrentUserCanDelete() {
        return this.currentUserCanDelete;
    }

    public void setCurrentUserCanDelete(boolean currentUserCanDelete) {
        this.currentUserCanDelete = currentUserCanDelete;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
