package uk.ac.ed.collaborate.service;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import uk.ac.ed.collaborate.dao.EnrollmentDao;
import uk.ac.ed.collaborate.dao.SessionDao;
import uk.ac.ed.collaborate.dao.UserDao;
import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.Session;
import uk.ac.ed.collaborate.data.User;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by v1mburg3 on 10/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionServiceTest {
    private static final boolean USERS_DEFAULT_INTERNAL = true;

    @Mock
    private CollaborateUltraService mockCollaborateUltraService;

    @Mock
    private SessionDao mockSessionDao;

    @Mock
    private EnrollmentDao mockEnrollmentDao;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private MailTemplateService mailTemplateService;

    @InjectMocks
    private SessionService sessionService;

    @Test
    public void getSessionsForAdmin_sessionsExist_returnsAllSessions_setsAllAsEditable() {
        List<Session> allSessions = Arrays.asList(
                createMinimalSession(),
                createMinimalSession(),
                createMinimalSession(),
                createMinimalSession()
        );
        when(mockSessionDao.getAllSessions()).thenReturn(allSessions);

        List<Session> adminSessions = sessionService.getSessionsForAdmin();

        assertThat(adminSessions, notNullValue());
        assertThat(adminSessions.size(), equalTo(allSessions.size()));
        for (Session session : adminSessions) {
            assertThat(session.isCurrentUserCanEdit(), is(true));
        }
    }

    @Test
    public void getSessionsForUsers_returnsUserSessions() {
        List<Session> sessionsForUser = Arrays.asList(
                createMinimalSession(),
                createMinimalSession(),
                createMinimalSession()
        );
        when(mockSessionDao.getSessionsForInternalUser("user1")).thenReturn(sessionsForUser);

        List<Session> userSessions = sessionService.getSessionsForUser("user1");

        assertThat(userSessions, notNullValue());
        assertThat(userSessions.size(), equalTo(sessionsForUser.size()));
    }

    @Test
    public void saveSession_noSessionId_createsInCollaborate_savesApiResponseLocally_returnsApiResponse() {
        Session sessionToCreate = createMinimalSession();
        Session collaborateSession = createMinimalSession();
        collaborateSession.setId("12345");

        when(mockCollaborateUltraService.createSession(sessionToCreate))
                .thenReturn(collaborateSession);

        Session returnedSession = sessionService.saveSession(sessionToCreate);

        assertThat(returnedSession, equalTo(collaborateSession));
        verify(mockSessionDao).saveSession(collaborateSession);
    }

    @Test
    public void saveSession_hasSessionId_updatesInCollaborate_savesApiResponseLocally_returnsApiResponse() {
        Session sessionToCreate = createMinimalSession();
        sessionToCreate.setId("12345");
        Session collaborateSession = createMinimalSession();
        collaborateSession.setId(sessionToCreate.getId());

        when(mockCollaborateUltraService.updateSession(sessionToCreate))
                .thenReturn(collaborateSession);

        Session returnedSession = sessionService.saveSession(sessionToCreate);

        assertThat(returnedSession, equalTo(collaborateSession));
        verify(mockSessionDao).saveSession(collaborateSession);
    }

    @Test
    public void saveSession_nullReturnFromCollaborate_returnsNull_doesNotSaveLocally() {
        Session sessionToCreate = createMinimalSession();
        when(mockCollaborateUltraService.createSession(sessionToCreate))
                .thenReturn(null);

        Session returnedSession = sessionService.saveSession(sessionToCreate);

        assertThat(returnedSession, nullValue());
        verify(mockSessionDao, never()).saveSession(any(Session.class));
    }

    @Test
    public void saveSession_exceptionFromCollaborate_emitsError_doesNotSaveLocally() {
        Session sessionToCreate = createMinimalSession();
        RuntimeException collaborateException = new RuntimeException();

        when(mockCollaborateUltraService.createSession(sessionToCreate))
                .thenThrow(collaborateException);

        try {
            sessionService.saveSession(sessionToCreate);
        } catch (RuntimeException caughtException) {
            assertThat(caughtException, equalTo(collaborateException));
        }

        verify(mockSessionDao, never()).saveSession(any(Session.class));
    }

    @Test
    public void deleteSession_deletesInCollaborate_deletesLocally() {
        String sessionId = "12345";
        List<Enrollment> mockEnrollments = new ArrayList<Enrollment>();
        User mockUser = new User();
        mockUser.setId("1");
        mockUser.setUsernameInternal("user");
        mockUser.setDisplayName("Test User");
        mockUser.setEmail("user@address");
        Enrollment mockEnrollment = new Enrollment();
        mockEnrollment.setId("1");
        mockEnrollment.setEditingPermission("moderator");
        mockEnrollment.setSession(createMinimalSession());
        mockEnrollment.setUser(mockUser);
        mockEnrollment.setUserId("1");
        mockEnrollments.add(mockEnrollment);

        when(mockSessionDao.getSession(sessionId)).thenReturn(createMinimalSession());
        when(mockUserDao.getInternalUser("user")).thenReturn(mockUser);
        when(mockUserDao.getUser("1")).thenReturn(mockUser);
        when(mockEnrollmentDao.getSessionEnrollments(sessionId)).thenReturn(mockEnrollments);

        sessionService.deleteSession(sessionId);

        verify(mockEnrollmentDao).deleteEnrollment(mockEnrollment);
        verify(mockCollaborateUltraService).deleteSession(sessionId);
        verify(mockSessionDao).deleteSession(sessionId);
    }

    @Test
    public void deleteSession_exceptionFromCollaborate_emitsError_doesNotDeleteLocally() {
        String sessionId = "testSessionId";
        RuntimeException collaborateException = new RuntimeException();

        doThrow(collaborateException)
                .when(mockCollaborateUltraService).deleteSession(sessionId);

        try {
            sessionService.deleteSession(sessionId);
        } catch (RuntimeException caughtException) {
            assertThat(caughtException, equalTo(collaborateException));
        }

        verify(mockSessionDao, never()).deleteSession(sessionId);
    }

    @Test
    public void userIsSessionModerator_userNotEnrolled_returnsFalse() {
        String sessionId = "SessionId";
        String internalUserId = "intUser";
        when(mockEnrollmentDao.getSessionEnrollmentForInternalUser(sessionId, internalUserId))
                .thenReturn(null);

        boolean isUserModerator = sessionService.userIsSessionModeratorPresenter(sessionId, internalUserId,"moderator");

        assertThat(isUserModerator, is(false));
    }

    @Test
    public void userIsSessionModerator_userEnrollmentParticipant_returnsFalse() {
        String sessionId = "SessionId";
        String internalUserId = "intUser";
        Enrollment enrollment = new Enrollment(sessionId, "collabUserId", "participant", "reader");
        when(mockEnrollmentDao.getSessionEnrollmentForInternalUser(sessionId, internalUserId))
                .thenReturn(enrollment);

        boolean isUserModerator = sessionService.userIsSessionModeratorPresenter(sessionId, internalUserId,"moderator");

        assertThat(isUserModerator, is(false));
    }

    @Test
    public void userIsSessionModerator_userEnrollmentModerator_returnsTrue() {
        String sessionId = "SessionId";
        String internalUserId = "intUser";
        Enrollment enrollment = new Enrollment(sessionId, "collabUserId", "moderator", "writer");
        when(mockEnrollmentDao.getSessionEnrollmentForInternalUser(sessionId, internalUserId))
                .thenReturn(enrollment);

        boolean isUserModerator = sessionService.userIsSessionModeratorPresenter(sessionId, internalUserId,"moderator");

        assertThat(isUserModerator, is(true));
    }

    private static Session createMinimalSession() {
        Session session = new Session();
        session.setName("Test session");
        session.setStartTime(new Date());
        session.setEndTime(new Date());
        session.setOccurrenceType('S');
        session.setCreatorId("user");

        return session;
    }
}
