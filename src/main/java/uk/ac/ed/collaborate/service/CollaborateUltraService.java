package uk.ac.ed.collaborate.service;

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.JWTSigner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.collaborate.data.*;
import uk.ac.ed.collaborate.service.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

@Service
public class CollaborateUltraService {

    private final Log logger = LogFactory.getLog(getClass());

    @Value("${collaborate.key}")
    private String collaborateKey;

    @Value("${collaborate.secret}")
    private String collaborateSecret;

    @Value("${collaborate.host}")
    private String collaborateHost;

    @Value("${collaborate.tokenttl}")
    private int tokenTtl;

    private AccessToken accessToken;

    @Autowired
    private RestTemplate restTemplate;

    public void retrieveAccessToken() {
        String url = collaborateHost + "/token";
        url += "?grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion="
                + this.createJwtToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> entity = new HttpEntity<String>("", headers);

        ResponseEntity<AccessToken> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, AccessToken.class);
        this.accessToken = response.getBody();
    }

    public boolean hasValidAccessToken() {
        return this.accessToken != null
                && this.accessToken.isNotExpired();
    }

    public List<Session> getSessions() {
        String url = collaborateHost + "/sessions";
        return this.getResource(url, SessionsResponse.class)
                .getResults();
    }

    public Session createSession(Session session) {
        String url = collaborateHost + "/sessions";
        return copyNonApiFields(
                this.postResource(url, session, Session.class),
                session);
    }

    public Session updateSession(Session session) {
        String url = collaborateHost + "/sessions/" + session.getId();
        return copyNonApiFields(
                this.putResource(url, session, Session.class),
                session);
    }

    public void deleteSession(String sessionId) {
        String url = collaborateHost + "/sessions/" + sessionId;
        this.deleteResource(url);
    }

    public List<Enrollment> getEnrollmentsForSession(String sessionId) {
        String url = collaborateHost + "/sessions/" + sessionId + "/enrollments";
        return this.getResource(url, EnrollmentsResponse.class).getResults();
    }

    public Enrollment createEnrollmentForSession(Enrollment enrollment) {
        String url = collaborateHost + "/sessions/" + enrollment.getSessionId() + "/enrollments";
        return copyNonApiFields(this.postResource(url, enrollment, Enrollment.class), enrollment);
    }

    public Enrollment updateEnrollmentForSession(Enrollment enrollment, String sessionId) {
        String url = collaborateHost + "/sessions/" + sessionId + "/enrollments/" + enrollment.getId();
        return copyNonApiFields(this.putResource(url, enrollment, Enrollment.class), enrollment);
    }

    public void deleteEnrollment(String enrollmentId, String sessionId) {
        String url = collaborateHost + "/sessions/" + sessionId + "/enrollments/" + enrollmentId;
        this.deleteResource(url);
    }

    public String getEnrollmentLaunchUrl(String enrollmentId, String sessionId) {
        String url = collaborateHost + "/sessions/" + sessionId + "/enrollments/" + enrollmentId + "/url";
        return this.getResource(url, LaunchUrlResponse.class).getUrl();
    }

    public List<User> getUsers() {
        String url = collaborateHost + "/users";
        return this.getResource(url, UsersResponse.class).getResults();
    }

    public User createUser(User user) {
        String url = collaborateHost + "/users";
        return copyNonApiFields(this.postResource(url, user, User.class), user);
    }

    public User updateUser(User user) {
        String url = collaborateHost + "/users/" + user.getId();
        return copyNonApiFields(this.putResource(url, user, User.class), user);
    }

    public void deleteUser(String userId) {
        String url = collaborateHost + "/users/" + userId;
        this.deleteResource(url);
    }

    public List<Recording> getRecordings() {
        String url = collaborateHost + "/recordings";
        return this.getResource(url, RecordingsResponse.class).getResults();
    }

    public List<Recording> getRecordingsByContext(String contextId)
    {
        String url = collaborateHost + "/recordings?contextId="+contextId;
        return this.getResource(url, RecordingsResponse.class).getResults();
    }

    public Context createContext(Context context)
    {
        String url = collaborateHost + "/contexts";
        return this.postResource(url,context,Context.class);
    }

    public Context getContextByExtId(String exitd)
    {
        String url = collaborateHost + "/contexts?extId="+exitd;
        ContextResponse contextResponse = this.getResource(url,ContextResponse.class);
        if (contextResponse.getResults().size()==1)
        {
            return contextResponse.getResults().get(0);
        }
        else
            return null;

    }

    public void saveContextSession(ContextSession contextSession,String contextId)
    {
        String url = collaborateHost + "/contexts/"+contextId+"/sessions";

        this.postResource(url,contextSession,null);
    }

    public String getRecordingPlayUrl() {
        return null;
    }

    public String getRecordingDownloadUrl(String recordingId) {
        String url = collaborateHost + "/recordings/"+recordingId+"/url?disposition=download";
        return this.getResource(url,UrlResponse.class).getUrl();
    }

    public void deleteRecording(String recordingId) {
        String url = collaborateHost + "/recordings/" + recordingId;
        this.deleteResource(url);
    }

    private String createJwtToken() {
        JWTSigner jwtSigner = new JWTSigner(collaborateSecret);

        long nowMillis = System.currentTimeMillis();
        JWTSigner.Options options = new JWTSigner.Options();
        options.setAlgorithm(Algorithm.HS256);

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("iss",collaborateKey);
        claims.put("sub","richard.good@ed.ac.uk");

        if (tokenTtl >= 0) {
            long expMillis = nowMillis + tokenTtl;
            claims.put("exp",expMillis);
        }

        return jwtSigner.sign(claims);
    }

    private static Session copyNonApiFields(Session responseSession, Session requestSession) {
        responseSession.setCreatorId(requestSession.getCreatorId());
        responseSession.setAccessType(requestSession.getAccessType());
        responseSession.setCurrentUserCanEdit(requestSession.isCurrentUserCanEdit());

        return responseSession;
    }

    private static Enrollment copyNonApiFields(Enrollment responseEnrollment, Enrollment requestEnrollment) {
        responseEnrollment.setSessionId(requestEnrollment.getSessionId());
        return responseEnrollment;
    }

    private static User copyNonApiFields(User responseUser, User requestUser) {
        responseUser.setInternal(requestUser.isInternal());
        responseUser.setUsernameInternal(requestUser.getUsernameInternal());
        return responseUser;
    }

    private <T> T getResource(String url, Class<T> responseType) {
        HttpEntity<String> entity = new HttpEntity<String>("", this.createCommonRequestHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType).getBody();
    }

    private <T> T postResource(String url, T resource, Class<T> responseType) {
        HttpEntity<T> entity = new HttpEntity<T>(resource, this.createCommonRequestHeaders());
        return restTemplate.postForEntity(url, entity, responseType).getBody();
    }

    private <T> T putResource(String url, T resource, Class<T> responseType) {
        HttpEntity<T> entity = new HttpEntity<T>(resource, this.createCommonRequestHeaders());
        return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType).getBody();
    }

    private void deleteResource(String url) {
        HttpEntity<String> entity = new HttpEntity<String>("", this.createCommonRequestHeaders());
        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    private HttpHeaders createCommonRequestHeaders() {
        if (!this.hasValidAccessToken()) {
            this.retrieveAccessToken();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + this.accessToken.getAccessToken());

        return headers;
    }
}