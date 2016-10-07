package uk.ac.ed.collaborate.data;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@Entity
@Table(name="VC_ULTRA_SESSIONS")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session implements Serializable {
    private static final String ISO_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String DISPLAY_DATE_FORMAT_PATTERN = "dd-MMM-yyyy HH:mm";

    @Id
    @Column(name="SESSION_ID")
    private String id;

    @Column(name="SESSION_NAME")
    private String name;

    @Column(name="START_TIME")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(pattern = ISO_DATE_FORMAT_PATTERN)
    private Date startTime;

    @Column(name="END_TIME")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(pattern = ISO_DATE_FORMAT_PATTERN)
    private Date endTime;

    @Column(name="BOUNDARY_TIME")
    private int boundaryTime;

    @Column(name="CREATOR_ID")
    @JsonIgnore
    private String creatorId;

    @Column(name="ALLOW_GUEST")
    private boolean allowGuest;

    @Column(name="GUEST_URL")
    private String guestUrl;

    @Column(name="NO_END_DATE")
    private boolean noEndDate;

    @Column(name="SHOW_PROFILE")
    private boolean showProfile;

    @Column(name="PARTICIPANT_CAN_USE_TOOLS")
    private boolean participantCanUseTools;

    @Column(name="CAN_SHARE_VIDEO")
    private boolean canShareVideo;

    @Column(name="CAN_SHARE_AUDIO")
    private boolean canShareAudio;

    @Column(name="CAN_POST_MESSAGE")
    private boolean canPostMessage;

    @Column(name="CAN_ANNOTATE_WHITEBOARD")
    private boolean canAnnotateWhiteboard;

    @Column(name="MUST_BE_SUPERVISED")
    private boolean mustBeSupervised;

    @Column(name="OPEN_CHAIR")
    private boolean openChair;

    @Column(name="RAISE_HAND_ON_ENTER")
    private boolean raiseHandOnEnter;

    @Column(name="ALLOW_IN_SESSION_INVITES")
    private boolean allowInSessionInvitees;

    @Column(name="CAN_DOWNLOAD_RECORDING")
    private boolean canDownloadRecording;

    @Column(name="OCCURRENCE_TYPE")
    private char occurrenceType;

    @Column(name="CREATED_DATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    @JsonFormat(pattern = ISO_DATE_FORMAT_PATTERN)
    private Date created;

    @Column(name="MODIFIED_DATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    @JsonFormat(pattern = ISO_DATE_FORMAT_PATTERN)
    private Date modified;

    @Column(name="GUEST_ROLE")
    private String guestRole;

    @Column(name="EDITING_PERMISSION")
    private String editingPermission;

    @Column(name="ACCESS_TYPE")
    @JsonIgnore
    private long accessType;

    @Transient
    @JsonIgnore
    private boolean currentUserCanEdit;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getBoundaryTime() {
        return this.boundaryTime;
    }

    public void setBoundaryTime(int boundaryTime) {
        this.boundaryTime = boundaryTime;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public boolean isAllowGuest() {
        return this.allowGuest;
    }

    public void setAllowGuest(boolean allowGuest) {
        this.allowGuest = allowGuest;
    }

    public String getGuestUrl() {
        return this.guestUrl;
    }

    public void setGuestUrl(String guestUrl) {
        this.guestUrl = guestUrl;
    }

    public boolean isNoEndDate() {
        return this.noEndDate;
    }

    public void setNoEndDate(boolean noEndDate) {
        this.noEndDate = noEndDate;
    }

    public boolean isShowProfile() {
        return this.showProfile;
    }

    public void setShowProfile(boolean showProfile) {
        this.showProfile = showProfile;
    }

    public boolean isParticipantCanUseTools() {
        return this.participantCanUseTools;
    }

    public void setParticipantCanUseTools(boolean participantCanUseTools) {
        this.participantCanUseTools = participantCanUseTools;
    }

    public boolean isCanShareVideo() {
        return this.canShareVideo;
    }

    public void setCanShareVideo(boolean canShareVideo) {
        this.canShareVideo = canShareVideo;
    }

    public boolean isCanShareAudio() {
        return this.canShareAudio;
    }

    public void setCanShareAudio(boolean canShareAudio) {
        this.canShareAudio = canShareAudio;
    }

    public boolean isCanPostMessage() {
        return this.canPostMessage;
    }

    public void setCanPostMessage(boolean canPostMessage) {
        this.canPostMessage = canPostMessage;
    }

    public boolean isCanAnnotateWhiteboard() {
        return this.canAnnotateWhiteboard;
    }

    public void setCanAnnotateWhiteboard(boolean canAnnotateWhiteboard) {
        this.canAnnotateWhiteboard = canAnnotateWhiteboard;
    }

    public boolean isMustBeSupervised() {
        return this.mustBeSupervised;
    }

    public void setMustBeSupervised(boolean mustBeSupervised) {
        this.mustBeSupervised = mustBeSupervised;
    }

    public boolean isOpenChair() {
        return this.openChair;
    }

    public void setOpenChair(boolean openChair) {
        this.openChair = openChair;
    }

    public boolean isRaiseHandOnEnter() {
        return this.raiseHandOnEnter;
    }

    public void setRaiseHandOnEnter(boolean raiseHandOnEnter) {
        this.raiseHandOnEnter = raiseHandOnEnter;
    }

    public boolean isAllowInSessionInvitees() {
        return this.allowInSessionInvitees;
    }

    public void setAllowInSessionInvitees(boolean allowInSessionInvitees) {
        this.allowInSessionInvitees = allowInSessionInvitees;
    }

    public boolean isCanDownloadRecording() {
        return this.canDownloadRecording;
    }

    public void setCanDownloadRecording(boolean canDownloadRecording) {
        this.canDownloadRecording = canDownloadRecording;
    }

    public char getOccurrenceType() {
        return this.occurrenceType;
    }

    public void setOccurrenceType(char occurrenceType) {
        this.occurrenceType = occurrenceType;
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

    public String getGuestRole() {
        return this.guestRole;
    }

    public void setGuestRole(String guestRole) {
        this.guestRole = guestRole;
    }

    public String getEditingPermission() {
        return this.editingPermission;
    }

    public void setEditingPermission(String editingPermission) {
        this.editingPermission = editingPermission;
    }

    public long getAccessType() {
        return this.accessType;
    }

    public void setAccessType(long accessType) {
        this.accessType = accessType;
    }

    public boolean isCurrentUserCanEdit() {
        return currentUserCanEdit;
    }

    public void setCurrentUserCanEdit(boolean currentUserCanEdit) {
        this.currentUserCanEdit = currentUserCanEdit;
    }

    @JsonIgnore
    public String getStartTimeForInternalDisplay() {
        return new SimpleDateFormat(DISPLAY_DATE_FORMAT_PATTERN).format(this.startTime);
    }

    @JsonIgnore
    public String getEndTimeForInternalDisplay() {
        return new SimpleDateFormat(DISPLAY_DATE_FORMAT_PATTERN).format(this.endTime);
    }

    @Override
    public String toString() {
        DateFormat fmt = new SimpleDateFormat(ISO_DATE_FORMAT_PATTERN);
        return "Session["
                + "Id:" + this.id + ",Name:" + this.name
                + ",StartTime:" + fmt.format(this.startTime) + ",EndTime:" + fmt.format(this.endTime)
                + ",OccurrenceType:" + this.occurrenceType + ",BoundaryTime:" + this.boundaryTime
                + ",guestUrl:" + this.guestUrl + ",guestRole:" + this.guestRole
                + ",editingPermission:" + this.editingPermission
                + "]";
    }
}
