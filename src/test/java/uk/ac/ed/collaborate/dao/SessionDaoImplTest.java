package uk.ac.ed.collaborate.dao;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.Session;
import uk.ac.ed.collaborate.data.User;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by v1mburg3 on 06/06/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:testDataAccessContext.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SessionDaoImplTest {
    @Autowired
    private UserDao userDao;

    @Autowired
    private EnrollmentDao enrollmentDao;

    @Autowired
    private SessionDao sessionDao;

    @Test
    public void getSession_sessionExists_returnsSession() throws Exception {
        Session createdSession = createSessionForSave();
        try {
            sessionDao.saveSession(createdSession);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        Session returnSession = sessionDao.getSession(createdSession.getId());

        assertThat(returnSession, notNullValue());
        assertThat(returnSession.getId(), equalTo(createdSession.getId()));
        assertThat(returnSession.getName(), equalTo(createdSession.getName()));
    }

    @Test
    public void getAllSessions_noSessionsSaved_returnsEmptyList() {
        List<Session> sessions = sessionDao.getAllSessions();
        assertThat(sessions.size(), equalTo(0));
    }

    @Test
    public void getAllSessions_sessionIsSaved_returnsSession() throws Exception {
        Session createdSession = createSessionForSave();
        try {
            sessionDao.saveSession(createdSession);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        List<Session> sessions = sessionDao.getAllSessions();
        assertThat(sessions.size(), equalTo(1));
        assertThat(sessions.get(0).getId(), equalTo(createdSession.getId()));
    }

    @Test
    public void saveSession_allValuesSet_doesNotRaiseError() throws Exception {
        sessionDao.saveSession(createSessionForSave());
    }

    @Test
    public void getSessionsForInternalUser_noUserEnrollments_returnsEmptyList() {
        List<Session> sessions = sessionDao.getSessionsForInternalUser("intUserId");

        assertThat(sessions, notNullValue());
        assertThat(sessions.size(), equalTo(0));
    }

    @Test
    public void getSessionsForInternalUser_userHasEnrollment_returnsEnrolledSession() throws Exception {
        // arrange
        User createdUser = createInternalUserForSave();
        Session createdSession = createSessionForSave();
        Enrollment enrollment = new Enrollment(
                "CollabEnrollId", createdSession.getId(), createdUser.getId(), "participant", "reader");
        try {
            userDao.saveUser(createdUser);
            sessionDao.saveSession(createdSession);
            enrollmentDao.saveEnrollment(enrollment);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        // act
        List<Session> sessions = sessionDao.getSessionsForInternalUser(createdUser.getUsernameInternal());

        // assert
        assertThat(sessions, notNullValue());
        assertThat(sessions.size(), equalTo(1));
        assertThat(sessions.get(0).getId(), equalTo(createdSession.getId()));
    }

    @Test
    public void getSessionsForInternalUser_userHasEnrollment_userIsSessionCreator_returnsSession_setsUserCanEditTrue() throws Exception {
        // arrange
        User createdUser = createInternalUserForSave();
        Session createdSession = createSessionForSave();
        createdSession.setCreatorId(createdUser.getUsernameInternal());
        Enrollment enrollment = new Enrollment(
                "CollabEnrollId", createdSession.getId(), createdUser.getId(), "participant", "reader");
        try {
            userDao.saveUser(createdUser);
            sessionDao.saveSession(createdSession);
            enrollmentDao.saveEnrollment(enrollment);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        // act
        List<Session> sessions = sessionDao.getSessionsForInternalUser(createdUser.getUsernameInternal());

        assertThat(sessions, notNullValue());
        assertThat(sessions.size(), equalTo(1));
        assertThat(sessions.get(0).isCurrentUserCanEdit(), is(true));
    }

    @Test
    public void getSessionsForInternalUser_userHasEnrollment_userIsModerator_returnsSession_setsUserCanEditTrue() throws Exception {
        // arrange
        User createdUser = createInternalUserForSave();
        Session createdSession = createSessionForSave();
        Enrollment enrollment = new Enrollment(
                "CollabEnrollId", createdSession.getId(), createdUser.getId(), "moderator", "writer");
        try {
            userDao.saveUser(createdUser);
            sessionDao.saveSession(createdSession);
            enrollmentDao.saveEnrollment(enrollment);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        // act
        List<Session> sessions = sessionDao.getSessionsForInternalUser(createdUser.getUsernameInternal());

        assertThat(sessions, notNullValue());
        assertThat(sessions.size(), equalTo(1));
        assertThat(sessions.get(0).isCurrentUserCanEdit(), is(true));
    }

    @Test
    public void getSessionsForInternalUser_userHasEnrollment_userIsParticipant_returnsSession_setsUserCanEditFalse() throws Exception {
        // arrange
        User createdUser = createInternalUserForSave();
        Session createdSession = createSessionForSave();
        Enrollment enrollment = new Enrollment(
                "CollabEnrollId", createdSession.getId(), createdUser.getId(), "participant", "reader");
        try {
            userDao.saveUser(createdUser);
            sessionDao.saveSession(createdSession);
            enrollmentDao.saveEnrollment(enrollment);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        // act
        List<Session> sessions = sessionDao.getSessionsForInternalUser(createdUser.getUsernameInternal());

        assertThat(sessions, notNullValue());
        assertThat(sessions.size(), equalTo(1));
        assertThat(sessions.get(0).isCurrentUserCanEdit(), is(false));
    }

    @Test
    public void getSessionsForInternalUser_userNotEnrolled_userIsCreator_returnsSession_setsUserCanEditTrue() throws Exception {
        // arrange
        User createdUser = createInternalUserForSave();
        Session createdSession = createSessionForSave();
        createdSession.setCreatorId(createdUser.getUsernameInternal());
        try {
            userDao.saveUser(createdUser);
            sessionDao.saveSession(createdSession);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        // act
        List<Session> sessions = sessionDao.getSessionsForInternalUser(createdUser.getUsernameInternal());

        // assert
        assertThat(sessions, notNullValue());
        assertThat(sessions.size(), equalTo(1));
        assertThat(sessions.get(0).isCurrentUserCanEdit(), is(true));
    }

    /*
     * Values taken from Collaborate API documentation
     */
    private static Session createSessionForSave() throws Exception {
        Session session = new Session();

        session.setId("5F89A28E40E1C8BC68FF9F580B55BF87");
        session.setName("New Session");
        session.setStartTime(parseIsoDateTime("2015-12-01T21:32:00.937Z"));
        session.setEndTime(parseIsoDateTime("2016-12-01T22:32:00.937Z"));
        session.setBoundaryTime(15);
        session.setCreatorId("ACreatorUser");
        session.setGuestUrl("https://api.bbcollab.com/assets/eyJhbGciOiJIUzI1NiJ9.eyJyZXNvdXJjZUFjY2Vzc1RpY2tldCI6eyJyZXNvdXJjZUlkIjoiNUY4OUEyOEU0MEUxQzhCQzY4RkY5RjU4MEI1NUJGODciLCJjb25zdW1lcklkIjoiQURNSU4tVVNFUi1DT05TVU1FUiIsInR5cGUiOiJTRVNTSU9OIiwicmVzdHJpY3Rpb24iOnsidHlwZSI6IlRJTUUiLCJleHBpcmF0aW9uSG91cnMiOi0xLCJtYXhSZXF1ZXN0cyI6MH0sImRpc3Bvc2l0aW9uIjoiTEFVTkNIIiwibGF1bmNoVHlwZSI6IkdVRVNUIn0sInN1YiI6ImJiQ29sbGFiQXBpIiwiaXNzIjoiYmJDb2xsYWJBcGkiLCJpYXQiOjE0NTQyNTE2Mzd9.QYPRsqEvRutJqy9s1nyzHCOr4QaHTYniI8gdOta-Cb4");
        session.setAllowGuest(true);
        session.setNoEndDate(false);
        session.setShowProfile(true);
        session.setParticipantCanUseTools(true);
        session.setCanShareVideo(false);
        session.setCanShareAudio(true);
        session.setCanPostMessage(true);
        session.setCanAnnotateWhiteboard(true);
        session.setMustBeSupervised(true);
        session.setOpenChair(false);
        session.setRaiseHandOnEnter(false);
        session.setAllowInSessionInvitees(true);
        session.setCanDownloadRecording(false);
        session.setOccurrenceType('S');
        session.setCreated(parseIsoDateTime("2016-01-31T14:47:17.145Z"));
        session.setModified(parseIsoDateTime("2016-01-31T14:47:17.145Z"));
        session.setGuestRole("participant");
        session.setEditingPermission("writer");

        return session;
    }

    private static User createInternalUserForSave() {
        User user = new User("CollabUserId", "UserName", "user@test.com", "UsernameInt");
        user.setUsernameInternal("UsernameInternal");
        user.setInternal(true);
        return user;
    }

    private static Date parseIsoDateTime(String date) {
        return ISODateTimeFormat.dateTimeParser().parseDateTime(date).toDate();
    }
}
