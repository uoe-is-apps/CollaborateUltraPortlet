package uk.ac.ed.collaborate.service;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.collaborate.data.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * Created by v1mburg3 on 08/06/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CollaborateUltraServiceTest.RestTemplateConfiguration.class)
public class CollaborateUltraServiceTest {
    @Autowired
    private CollaborateUltraService collaborateUltraService;

    private List<String> sessionIdsCreatedByTest = new ArrayList<>();
    private List<String> userIdsCreatedByTest = new ArrayList<>();
    private List<Enrollment> enrollmentsCreatedByTest = new ArrayList<>();

    /**
     * Ensure that the service has an API token. This acts as a basic test of connectivity to the remote dev API.
     * If an access token cannot be retrieved, ignore the test and move on.
     */
    @Before
    public void setupApiAccessToken() {
        try {
            collaborateUltraService.retrieveAccessToken();

        } finally {
            Assume.assumeTrue(collaborateUltraService.hasValidAccessToken());
        }
    }

    @After
    public void attemptTeardownCreatedObjects() {
        try {
            for (String sessionId : this.sessionIdsCreatedByTest) {
                collaborateUltraService.deleteSession(sessionId);
            }
            for (String userId : this.userIdsCreatedByTest) {
                collaborateUltraService.deleteUser(userId);
            }
            for (Enrollment enrollment : this.enrollmentsCreatedByTest) {
                collaborateUltraService.deleteEnrollment(enrollment.getId(), enrollment.getSessionId());
            }
        } catch (Exception e) {
            // if deletions fail the relevant test should pick it up
        }
    }

    @Test
    public void getSessions_noPriorInteractions_returnsNonNullList(){
        List<Session> sessions = collaborateUltraService.getSessions();
        // don't know current state of remote system, so cannot assume anything about number of existing sessions
        assertThat(sessions, notNullValue());
    }

    @Test
    public void createSession_requiredValuesOnly_returnsCreatedSession() throws Exception {
        Session sessionToCreate = setupMinimalSessionForCreate();

        Session createdSession = collaborateUltraService.createSession(sessionToCreate);

        assertThat(createdSession, notNullValue());
        assertThat(createdSession.getId(), notNullValue());

        this.sessionIdsCreatedByTest.add(createdSession.getId());
    }

    @Test
    public void createSession_nonApiValuesSet_returnsCreatedSession_includesNonApiValues() throws Exception {
        Session sessionToCreate = setupSessionWithNonApiValuesForCreate();

        Session createdSession = collaborateUltraService.createSession(sessionToCreate);

        assertThat(createdSession, notNullValue());
        assertThat(createdSession.getCreatorId(), equalTo(sessionToCreate.getCreatorId()));
        assertThat(createdSession.getAccessType(), equalTo(sessionToCreate.getAccessType()));
        assertThat(createdSession.isCurrentUserCanEdit(), equalTo(sessionToCreate.isCurrentUserCanEdit()));

        this.sessionIdsCreatedByTest.add(createdSession.getId());
    }

    @Test
    public void updateSession_nonApiValuesSet_returnsUpdatedSession_includesNonApiValues() throws Exception {
        Session createdSession = null;
        try {
            createdSession = collaborateUltraService.createSession(setupSessionWithNonApiValuesForCreate());
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        Session updatedSession = collaborateUltraService.updateSession(createdSession);

        assertThat(updatedSession, notNullValue());
        assertThat(updatedSession.getCreatorId(), equalTo(createdSession.getCreatorId()));
        assertThat(updatedSession.getAccessType(), equalTo(createdSession.getAccessType()));
        assertThat(updatedSession.isCurrentUserCanEdit(), equalTo(createdSession.isCurrentUserCanEdit()));

        this.sessionIdsCreatedByTest.add(createdSession.getId());
    }

    @Test
    public void deleteSession_basicSessionExists_returnsWithNoErrors() throws Exception {
        Session createdSession = null;
        try {
            Session sessionToCreate = setupMinimalSessionForCreate();
            createdSession = collaborateUltraService.createSession(sessionToCreate);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }
        Assume.assumeNotNull(createdSession);

        collaborateUltraService.deleteSession(createdSession.getId());

        // this method doesn't currently test for a successful return
        // if no error, assume everything went smoothly
    }

    @Test
    public void getUsers_noPriorInteractions_returnsNonNullList() {
        List<User> users = collaborateUltraService.getUsers();
        // don't know current state of remote system, so cannot assume anything about number of existing users
        assertThat(users, notNullValue());
    }

    @Test
    public void createUser_minimalValuesOnly_returnsCreatedUser() throws Exception{
        User userToCreate = setupMinimalUserForCreate();

        User createdUser  = collaborateUltraService.createUser(userToCreate);

        assertThat(createdUser, notNullValue());
        assertThat(createdUser.getId(), notNullValue());

        this.userIdsCreatedByTest.add(createdUser.getId());
    }

    @Test
    public void createUser_nonApiValuesSet_returnsCreatedUser_includesNonApiValues() throws Exception {
        User userToCreate = setupUserWithNonApiValuesForCreate();

        User createdUser  = collaborateUltraService.createUser(userToCreate);

        assertThat(createdUser, notNullValue());
        assertThat(createdUser.isInternal(), equalTo(userToCreate.isInternal()));
        assertThat(createdUser.getUsernameInternal(), equalTo(userToCreate.getUsernameInternal()));

        this.userIdsCreatedByTest.add(createdUser.getId());
    }

    @Test
    public void deleteUser_userExists_returnsWithNoErrors() throws Exception {
        User user = null;
        try {
            User userToCreate = setupMinimalUserForCreate();
            user  = collaborateUltraService.createUser(userToCreate);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        collaborateUltraService.deleteUser(user.getId());
        // this method doesn't currently test for a successful return
        // if no error, assume everything went smoothly
    }

    @Test
    public void createEnrollmentForSession_userIsModerator_returnsCreatedEnrollment() throws Exception {
        Session session = null;
        User user = null;
        try {
            session = collaborateUltraService.createSession(setupMinimalSessionForCreate());
            user = collaborateUltraService.createUser(setupMinimalUserForCreate());
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        Enrollment enrollmentToCreate = setupSessionEnrollmentForUser(session.getId(), user.getId(), "moderator");

        Enrollment createdEnrollment = collaborateUltraService.createEnrollmentForSession(enrollmentToCreate);

        assertThat(createdEnrollment, notNullValue());
        assertThat(createdEnrollment.getId(), notNullValue());
        assertThat(createdEnrollment.getLaunchingRole(), equalTo(enrollmentToCreate.getLaunchingRole()));

        this.sessionIdsCreatedByTest.add(session.getId());
        this.userIdsCreatedByTest.add(user.getId());
    }

    @Test
    public void getEnrollmentLaunchUrl_returnsUrl() throws Exception {
        Session session = null;
        User user = null;
        Enrollment enrollment = null;
        try {
            session = collaborateUltraService.createSession(setupMinimalSessionForCreate());
            user = collaborateUltraService.createUser(setupMinimalUserForCreate());
            enrollment = collaborateUltraService.createEnrollmentForSession(
                    setupSessionEnrollmentForUser(session.getId(), user.getId(), "moderator"));
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        String url = collaborateUltraService.getEnrollmentLaunchUrl(enrollment.getId(), enrollment.getSessionId());

        assertThat(url, notNullValue());

        this.sessionIdsCreatedByTest.add(session.getId());
        this.userIdsCreatedByTest.add(user.getId());
        this.enrollmentsCreatedByTest.add(enrollment);
    }

    /**
     * Recordings can't be programmatically created. They have to be created by launching a session.
     * The best we can do for a test is have a go at getting and deserializing any Recording values which may exist.
     */
    @Test
    public void getRecordings_noPriorInteractions_returnsNonNullList() {
        List<Recording> recordings = collaborateUltraService.getRecordings();
        assertThat(recordings, notNullValue());
    }

    @Test
    public void getRecordingDownloadUrl()
    {
        List<Recording> recordings = collaborateUltraService.getRecordings();
        for (Recording recording : recordings)
        {
            if (recording.isCanDownload())
            {
                String recordingDownloadUrl = collaborateUltraService.getRecordingDownloadUrl(recording.getId());
                assertNotNull(recordingDownloadUrl);
                break;
            }
        }
    }

    /** cannot use this for now as there is no way to delete a context after testing creation
    @Test
    public void createContext()
    {
        Context context = collaborateUltraService.createContext(setupContextForCreate());
        assertNotNull(context);
        assertNotNull(context.getId());
    }
     // Also removing this as it's not necessarily going to return results without being able to create/remove
     @Test
     public void testGetContext() throws Exception{
     Context context = collaborateUltraService.getContextByExtId("rgood");
     if (context!=null)
     {
     System.out.println("Context found");
     System.out.println(context.getName());
     }
     }

     **/

    private static Context setupContextForCreate()
    {
        Context context = new Context();
        context.setExtId("uun");
        context.setLabel("uun");
        context.setName("uun");
        context.setTitle("uun");
        return context;
    }
    private static Session setupMinimalSessionForCreate() throws Exception {
        Session toCreate = new Session();
        toCreate.setName("Sample session");
        toCreate.setStartTime(new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).parse("10-06-2016 13:00"));
        toCreate.setEndTime(new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).parse("10-06-2016 14:00"));
        toCreate.setOccurrenceType('S');

        return toCreate;
    }

    private static Session setupSessionWithNonApiValuesForCreate() throws Exception {
        Session toCreate = setupMinimalSessionForCreate();
        toCreate.setCreatorId("testuser");
        toCreate.setAccessType(12345);
        toCreate.setCurrentUserCanEdit(true);

        return toCreate;
    }

    private static User setupMinimalUserForCreate() {
        User user = new User();

        user.setEmail("user@test.com");
        user.setDisplayName("A User");

        return user;
    }

    private static User setupUserWithNonApiValuesForCreate() {
        User user = setupMinimalUserForCreate();

        user.setInternal(true);
        user.setUsernameInternal("InternalUsername");

        return user;
    }

    private static Enrollment setupSessionEnrollmentForUser(String sessionId, String userId, String role) {
        Enrollment enrollment = new Enrollment();
        enrollment.setSessionId(sessionId);
        enrollment.setUserId(userId);
        enrollment.setLaunchingRole(role);
        enrollment.setEditingPermission("reader");

        return enrollment;
    }

    @Configuration
    @PropertySource("classpath:testCollaborateApi.properties")
    public static class RestTemplateConfiguration {
        /**
         * Manually enable this setting when using the tests alongside Fiddler to debug API requests
         * WARNING: while this is true, Fiddler must be open on port 8888 or all requests will fail
         */
        private boolean setFiddlerProxy = false;

        @Bean
        public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        public RestTemplate restTemplate() throws Exception {
            if (setFiddlerProxy) {
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{
                        new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) { }
                            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) { }
                            public X509Certificate[] getAcceptedIssuers() { return null; }
                        }
                }, null);
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
                factory.setProxy(proxy);
                return new RestTemplate(factory);
            }

            return new RestTemplate();
        }

        @Bean
        public CollaborateUltraService collaborateUltraService() {
            return new CollaborateUltraService();
        }
    }

}
