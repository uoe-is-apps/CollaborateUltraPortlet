package uk.ac.ed.collaborate.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.collaborate.dao.EnrollmentDao;
import uk.ac.ed.collaborate.dao.SessionDao;
import uk.ac.ed.collaborate.dao.UserDao;
import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.Session;
import uk.ac.ed.collaborate.data.User;
import uk.ac.ed.collaborate.service.utils.UserEnrollmentDiff;

import javax.mail.MessagingException;
import java.util.*;

/**
 * Created by v1mburg3 on 07/06/2016.
 */
@Service
public class SessionService {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private CollaborateUltraService collaborateUltraService;

    @Autowired
    private MailTemplateService mailTemplateService;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private EnrollmentDao enrollmentDao;

    @Autowired
    private UserDao userDao;

    public Session setupSessionWithDefaultValues() {
        Session session = new Session();

        // Set default on values
        session.setAllowInSessionInvitees(true);
        session.setAllowGuest(true);
        session.setShowProfile(true);
        session.setCanShareAudio(true);
        session.setCanShareVideo(true);
        session.setCanPostMessage(true);
        session.setCanAnnotateWhiteboard(true);
        session.setNoEndDate(false);


        //Set explicit off values
        session.setRaiseHandOnEnter(false);


        session.setBoundaryTime(15);
        session.setOccurrenceType('S');
        session.setCanDownloadRecording(true);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 15;
        int time = 15 - mod;

        calendar.add(Calendar.MINUTE, time);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        session.setStartTime(calendar.getTime());

        unroundedMinutes = calendar.get(Calendar.MINUTE);
        mod = unroundedMinutes % 15;
        time = 15 - mod;

        calendar.add(Calendar.MINUTE,time );
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        session.setEndTime(calendar.getTime());

        return session;
    }

    public List<Session> getSessionsForAdmin() {
        List<Session> sessions = sessionDao.getAllSessions();
        for (Session session : sessions) {
            session.setCurrentUserCanEdit(true);
        }
        return sessions;
    }

    public List<Session> getSessionsForUser(String uid) {
        return sessionDao.getSessionsForInternalUser(uid);
    }

    public Session getSession(String sessionId) {
        return sessionDao.getSession(sessionId);
    }

    public Session saveSession(Session session) {
        Session savedSession;
        if (session.getId() == null) {
            savedSession = collaborateUltraService.createSession(session);
        } else {
            savedSession = collaborateUltraService.updateSession(session);
        }

        if (savedSession == null) {
            return null;
        }

        sessionDao.saveSession(savedSession);
        return savedSession;
    }

    public void deleteSession(String sessionId) {
        List<Enrollment> sessionEnrollments = enrollmentDao.getSessionEnrollments(sessionId);
        logger.info("delete session enrolments found:"+sessionEnrollments.size());
        Session session = sessionDao.getSession(sessionId);
        logger.info("prepare to delete - found session - " + (session != null));
        
        if(session != null){
        Date endTime = session.getEndTime();                
        Date currentTime = new Date();
        boolean ifPastSession = endTime.before(currentTime);        
        
        logger.info("currentTime:" + currentTime + " endTime:" + endTime + " ifPastSession - " + ifPastSession);            
        
        if (sessionEnrollments.size()>0) {
            logger.info("sessionEnrollments.size():" + sessionEnrollments.size());               
            
            logger.info("session.getCreatorId():" + session.getCreatorId()); 

            User sessionCreator = userDao.getInternalUser(session.getCreatorId());
            
            logger.info("sessionCreator:" + sessionCreator);            
            
            String fromEmail = sessionCreator.getEmail();
            
            logger.info("fromEmail:" + fromEmail);       

            String creatorDetails = sessionCreator.getDisplayName() + " (" + sessionCreator.getEmail() + ")";
            
            logger.info("creatorDetails:" + creatorDetails);       

            String[] substitutions;
            User user;
            List<String> toEmail;

            logger.info("set email initial entries:"+fromEmail+" "+creatorDetails);

            for (Enrollment enrollment : sessionEnrollments) {
                logger.info("in loop");
                user = userDao.getUser(enrollment.getUserId());
                logger.info("got user:"+user.getDisplayName());
                substitutions = new String[]{
                        user.getDisplayName(),
                        creatorDetails,
                        session.getName(),
                        session.getStartTimeForInternalDisplay(),
                        session.getEndTimeForInternalDisplay(),
                        creatorDetails
                };
               
                logger.info("deleting enrollment");
                enrollmentDao.deleteEnrollment(enrollment);

                if(user.getEmail() != null && user.getEmail().contains("@")){
                    logger.info("set substitutions");
                    toEmail = Collections.singletonList(user.getEmail());

                    //TEL032-18
                    //When a moderator deletes a past session no cancellation email should be sent. 
                    //These emails should only be sent for sessions in the future. 
                    //Currently when a moderator deletes a past session the email is sent to all participants and moderators.
                    if(!ifPastSession){
                        try {
                            logger.info("this session is not past session, sending email");
                            mailTemplateService.sendEmailUsingTemplate(
                                    fromEmail, toEmail, null, substitutions, "sessionDeletionMessage");
                        } catch (MessagingException e) {
                            logger.error("Failed to send a notification email to: " + user.getEmail(), e);
                        }
                    }else{
                        logger.error("this session is past session, ignore delete session email");
                    }
                }
            }
        }
        logger.info("deleting session");
        collaborateUltraService.deleteSession(sessionId);
        sessionDao.deleteSession(sessionId);
        }else{
            logger.info("session not found - (session == null) - " + (session == null));
        }
    }

    
    //TEL032-17
    //When a moderator edits a session and makes changes to the date/time 
    //emails should be sent to all participants and moderators showing updated details. 
    //Currently no updated emails are sent.
    public void sendNotificationEmailForSessionUpdate(Session session, String internalLaunchUrl, String originalStartTime, String originalEndTime) { 
        List<Enrollment> sessionEnrollments = enrollmentDao.getSessionEnrollments(session.getId());
        logger.info("updated session enrolments found:"+sessionEnrollments.size());
   
        if(session != null){
        Date endTime = session.getEndTime();                
        Date currentTime = new Date();
        boolean ifPastSession = endTime.before(currentTime);        
        
        if(ifPastSession) return;
        
        if (sessionEnrollments.size()>0) {
            User sessionCreator = userDao.getInternalUser(session.getCreatorId());
            String externalLaunchUrl = session.getGuestUrl();
            String fromEmail = sessionCreator.getEmail();
            String creatorDetails = sessionCreator.getDisplayName() + " (" + sessionCreator.getEmail() + ")";

            String[] substitutions;
            User user;
            List<String> toEmail;

            logger.info("set email initial entries:"+fromEmail+" "+creatorDetails);

            for (Enrollment enrollment : sessionEnrollments) {
                logger.info("in loop");
                user = userDao.getUser(enrollment.getUserId());
                
                String url = user.isInternal()?internalLaunchUrl:externalLaunchUrl;
                
                logger.info("got user:"+user.getDisplayName());
                substitutions = new String[]{
                        user.getDisplayName(),
                        creatorDetails,
                        session.getName(),
                        originalStartTime,
                        originalEndTime,
                        session.getStartTimeForInternalDisplay(),
                        session.getEndTimeForInternalDisplay(),
                        url,
                        creatorDetails
                };
                logger.info("set substitutions");
                
                if(user.getEmail() != null && user.getEmail().contains("@")){
                    toEmail = Collections.singletonList(user.getEmail());

                    try {
                        logger.info("sending email for session update");
                        mailTemplateService.sendEmailUsingTemplate(
                                fromEmail, toEmail, null, substitutions, "sessionUpdateMessage");
                    } catch (MessagingException e) {
                        logger.error("Failed to send a notification email to: " + user.getEmail(), e);
                    }                
                }
            }
        }
        logger.info("finish sending email for updating session");
        }
    }    
    
    
    public String getSessionLaunchUrlForInternalUser(String sessionId, String internalUserId) {
        Enrollment userEnrollment = enrollmentDao.getSessionEnrollmentForInternalUser(sessionId, internalUserId);
        if (userEnrollment == null) {
            return null;
        } else {
            return collaborateUltraService.getEnrollmentLaunchUrl(userEnrollment.getId(), sessionId);
        }
    }

    public String getSessionLaunchUrlForExternalUser(String sessionId, String userId) {
        Enrollment userEnrollment = enrollmentDao.getSessionEnrollmentForExternalUser(sessionId, userId);
        if (userEnrollment == null) {
            return null;
        } else {
            return collaborateUltraService.getEnrollmentLaunchUrl(userEnrollment.getId(), sessionId);
        }
    }

    public boolean userIsSessionModeratorPresenter(String sessionId, String internalUserId, String role) {
        Enrollment userEnrollment = enrollmentDao.getSessionEnrollmentForInternalUser(sessionId, internalUserId);
        return userEnrollment != null
                && userEnrollment.getLaunchingRole().equals(role);
    }

    public List<User> getSessionEnrolledUsers(String sessionId, String role, boolean internal) {
        return enrollmentDao.getEnrolledUsers(sessionId, role, internal);
    }

    public void processModeratorParticipantEnrollments(Session session, User sessionCreator, List<User> moderators,String launchUrl, String role) {
        UserEnrollmentDiff diff = UserEnrollmentDiff.processDiff(
                moderators, enrollmentDao.getEnrollments(session.getId(), role, true));

        saveUserEnrollments(diff, session.getId(), role);

        notifyNewAttendees(session, sessionCreator, diff.getUsersToEnroll(), "moderatorMailMessage",launchUrl);
    }

    public void processInternalParticipantEnrollments(Session session, User sessionCreator, List<User> internalParticipants,String launchUrl) {
        UserEnrollmentDiff diff = UserEnrollmentDiff.processDiff(
                internalParticipants, enrollmentDao.getEnrollments(session.getId(), "participant", true));

        saveUserEnrollments(diff, session.getId(), "participant");

        notifyNewAttendees(session, sessionCreator, diff.getUsersToEnroll(), "intParticipantMailMessage",launchUrl);
    }

    public void processExternalParticipantEnrollments(Session session, User sessionCreator, List<User> externalParticipants) {
        UserEnrollmentDiff diff = UserEnrollmentDiff.processDiff(
                externalParticipants, enrollmentDao.getEnrollments(session.getId(), "participant", false));

        saveUserEnrollments(diff, session.getId(), "participant");

        notifyNewAttendees(session, sessionCreator, diff.getUsersToEnroll(), "extParticipantMailMessage",session.getGuestUrl());
    }

    private void saveUserEnrollments(UserEnrollmentDiff diff, String sessionId, String role) {

        for (Enrollment enrollment : diff.getEnrollmentsToRemove()) {
            deleteEnrollment(enrollment);
        }

        for (User user : diff.getUsersToEnroll()) {
            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setUserId(user.getId());
            newEnrollment.setSessionId(sessionId);
            newEnrollment.setLaunchingRole(role);
            newEnrollment.setEditingPermission(getEnrollmentEditPermissionForRole(role));
            this.saveEnrollment(newEnrollment);
        }
    }

    private void notifyNewAttendees(Session session, User sessionCreator, List<User> newAttendees, String notificationTemplateName,String launchUrl) {
        String fromEmail = sessionCreator.getEmail();
        String creatorDetails = sessionCreator.getDisplayName() + " (" + sessionCreator.getEmail() + ")";

        String[] substitutions;

        for (User user : newAttendees) {

            substitutions = new String[] {
                    user.getDisplayName(),
                    creatorDetails,
                    session.getName(),
                    session.getStartTimeForInternalDisplay(),
                    session.getEndTimeForInternalDisplay(),
                    launchUrl,
                    creatorDetails
            };
            
            if(user.getEmail() != null && user.getEmail().contains("@")){
                List<String> toEmail = Collections.singletonList(user.getEmail());

                try {
                    mailTemplateService.sendEmailUsingTemplate(
                            fromEmail, toEmail, null, substitutions, notificationTemplateName);
                } catch(MessagingException e) {
                    logger.error("Failed to send a notification email to: " + user.getEmail(), e);
                }
            }
            
        }
    }
 
    private Enrollment saveEnrollment(Enrollment enrollment) {
        Enrollment createdEnrollment = collaborateUltraService.createEnrollmentForSession(enrollment);
        if (createdEnrollment != null) {
            enrollmentDao.saveEnrollment(createdEnrollment);
        }
        return createdEnrollment;
    }

    private void deleteEnrollment(Enrollment enrollment) {
        collaborateUltraService.deleteEnrollment(enrollment.getId(), enrollment.getSessionId());
        enrollmentDao.deleteEnrollment(enrollment.getId());
    }

    private static String getEnrollmentEditPermissionForRole(String role) {
        if (role.equals("moderator")) {
            return "writer";
        } else {
            return "reader";
        }
    }
}
