package uk.ac.ed.collaborate.dao;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.User;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by v1mburg3 on 13/06/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:testDataAccessContext.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EnrollmentDaoImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EnrollmentDao enrollmentDao;

    @Test
    public void getEnrollment_enrollmentDoesNotExist_returnsNull() {
        Enrollment notCreatedEnrollment = setupEnrollmentForSave("user1");

        Enrollment returnedEnrollment = enrollmentDao.getEnrollment(notCreatedEnrollment.getId());

        assertThat(returnedEnrollment, nullValue());
    }

    @Test
    public void getEnrollment_enrollmentExists_returnsEnrollment() {
        Enrollment createdEnrollment = setupEnrollmentForSave("user1");
        try {
            enrollmentDao.saveEnrollment(createdEnrollment);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        Enrollment returnedEnrollment = enrollmentDao.getEnrollment(createdEnrollment.getId());

        assertThat(returnedEnrollment, notNullValue());
        assertThat(returnedEnrollment.getId(), equalTo(createdEnrollment.getId()));
        assertThat(returnedEnrollment.getUserId(), equalTo(createdEnrollment.getUserId()));
    }

    @Test
    public void saveEnrollment_allValuesSet_addsEnrollment() {
        enrollmentDao.saveEnrollment(setupEnrollmentForSave("userId"));
        assertThat(this.countEnrollments(), equalTo(1));
    }

    @Test
    public void deleteEnrollment_noEnrollmentFound_doesNotCauseError() {
        Enrollment notCreatedEnrollment = setupEnrollmentForSave("user1");

        enrollmentDao.deleteEnrollment(notCreatedEnrollment.getId());
    }

    @Test
    public void deleteEnrollment_enrollmentExists_deletesEnrollment() {
        Enrollment createdEnrollment = setupEnrollmentForSave("user1");
        try {
            enrollmentDao.saveEnrollment(createdEnrollment);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        } finally {
            Assume.assumeThat(this.countEnrollments(), equalTo(1));
        }

        enrollmentDao.deleteEnrollment(createdEnrollment.getId());

        assertThat(this.countEnrollments(), equalTo(0));
    }

    @Test
    public void getAllEnrollments_noExistingEnrollments_returnsEmptyList() {
        List<Enrollment> enrollments = enrollmentDao.getAllEnrollments();

        assertThat(enrollments, notNullValue());
        assertThat(enrollments.size(), equalTo(0));
    }

    @Test
    public void getAllEnrollments_enrollmentsExist_returnsAllEnrollments() {
        try {
            enrollmentDao.saveEnrollment(setupEnrollmentForSave("User 1"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSave("User 2"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSave("User 3"));
        } catch (Exception e) {
            Assume.assumeNoException(e);
        } finally {
            Assume.assumeThat(this.countEnrollments(), equalTo(3));
        }

        List<Enrollment> enrollments = enrollmentDao.getAllEnrollments();

        assertThat(enrollments, notNullValue());
        assertThat(enrollments.size(), equalTo(3));
    }

    @Test
    public void getEnrollments_noExistingEnrollments_returnsEmptyList() {
        List<Enrollment> returnedEnrollments = enrollmentDao.getEnrollments("Session1", "moderator", null);

        assertThat(returnedEnrollments, notNullValue());
        assertThat(returnedEnrollments.size(), equalTo(0));
    }

    @Test
    public void getEnrollments_noEnrollmentsForSessionRole_returnsEmptyList() {
        try {
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session1", "participant"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session2", "moderator"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session1", "participant"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session2", "participant"));
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        List<Enrollment> returnedEnrollments = enrollmentDao.getEnrollments("Session1", "moderator", null);

        assertThat(returnedEnrollments, notNullValue());
        assertThat(returnedEnrollments.size(), equalTo(0));
    }

    @Test
    public void getEnrollments_enrollmentsForSessionsAndRoles_returnsSessionRoleEnrollmentsOnly() {
        try {
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session1", "moderator"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session1", "participant"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session1", "moderator"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session2", "moderator"));
            enrollmentDao.saveEnrollment(setupEnrollmentForSessionAndRole("Session2", "participant"));
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        List<Enrollment> returnedEnrollments = enrollmentDao.getEnrollments("Session1", "moderator", null);

        assertThat(returnedEnrollments, notNullValue());
        assertThat("Items 1 and 3 from the setup list are returned", returnedEnrollments.size(), equalTo(2));
    }

    @Test
    public void getEnrollments_externalParticipantsRequested_returnsParticipantEnrollmentsForExternalUsersOnly() {
        String sessionId = "5F89A28E40E1C8BC68FF9F580B55BF87";
        String role = "participant";

        try {
            userDao.saveUser(new User("User1", "Internal User", "user1@test.com", "UsernameInt"));
            userDao.saveUser(new User("User2", "External User", "user2@test.com", null));
            enrollmentDao.saveEnrollment(new Enrollment("IntUserEnroll", sessionId, "User1", role, "writer"));
            enrollmentDao.saveEnrollment(new Enrollment("ExtUserEnroll", sessionId, "User2", role, "writer"));
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        boolean getInternalUser = false;
        List<Enrollment> returnedEnrollments = enrollmentDao.getEnrollments(sessionId, role, getInternalUser);

        assertThat(returnedEnrollments, notNullValue());
        assertThat(returnedEnrollments.size(), equalTo(1));
        assertThat(returnedEnrollments.get(0).getId(), equalTo("ExtUserEnroll"));
    }

    @Test
    public void getEnrolledUsers_noEnrollments_returnsEmptyList() {
        List<User> users = enrollmentDao.getEnrolledUsers("Session1", "moderator", null);

        assertThat(users, notNullValue());
        assertThat(users.size(), equalTo(0));
    }

    @Test
    public void getEnrolledUsers_userAndEnrollment_returnsUser() {
        String sessionId = "5F89A28E40E1C8BC68FF9F580B55BF87";
        String role = "moderator";
        String userId = "133C86F19427C0791783EF504B35954B";
        try {
            userDao.saveUser(new User(userId, "User 1", "user1@test.com", "UsernameInt"));
            enrollmentDao.saveEnrollment(new Enrollment("Enroll1", sessionId, userId, role, "writer"));
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        List<User> users = enrollmentDao.getEnrolledUsers(sessionId, role, null);

        assertThat(users, notNullValue());
        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getId(), equalTo(userId));
    }

    @Test
    public void getEnrolledUsers_internalExternalUsersEnrolled_returnsSpecifiedUsers() {
        String sessionId = "5F89A28E40E1C8BC68FF9F580B55BF87";
        String role = "participant";

        try {
            userDao.saveUser(new User("User1", "Internal User", "user1@test.com", "UsernameInt"));
            userDao.saveUser(new User("User2", "External User", "user2@test.com", null));
            enrollmentDao.saveEnrollment(new Enrollment("Enroll1", sessionId, "User1", role, "writer"));
            enrollmentDao.saveEnrollment(new Enrollment("Enroll2", sessionId, "User2", role, "writer"));
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        boolean getInternalUser = false;
        List<User> users = enrollmentDao.getEnrolledUsers(sessionId, role, getInternalUser);

        assertThat(users, notNullValue());
        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).isInternal(), equalTo(getInternalUser));
    }

    @Test
    public void getSessionEnrollmentForInternalUser_enrollmentDoesNotExist_returnsNull() {
        Enrollment expectedEnrollment = setupEnrollmentForSave("userId");

        Enrollment returnedEnrollment = enrollmentDao.getSessionEnrollmentForInternalUser(
                expectedEnrollment.getSessionId(), expectedEnrollment.getUserId());

        assertThat(returnedEnrollment, nullValue());
    }

    @Test
    public void getSessionEnrollmentForInternalUser_enrollmentExists_returnsEnrollment() {
        User user = new User("133C86F19427C0791783EF504B35954B", "User 1", "user1@test.com", "UsernameInt");
        Enrollment createdEnrollment = setupEnrollmentForSave(user.getId());
        try {
            userDao.saveUser(user);
            enrollmentDao.saveEnrollment(createdEnrollment);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        Enrollment returnedEnrollment = enrollmentDao.getSessionEnrollmentForInternalUser(
                createdEnrollment.getSessionId(), user.getUsernameInternal());

        assertThat(returnedEnrollment, notNullValue());
        assertThat(returnedEnrollment.getId(), equalTo(createdEnrollment.getId()));
    }

    private int countEnrollments() {
        return JdbcTestUtils.countRowsInTable(this.jdbcTemplate, "VC_ULTRA_SESSION_ENROLS");
    }

    private static Enrollment setupEnrollmentForSave(String userId) {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(userId + "sampleEnrollment");
        enrollment.setUserId(userId);
        enrollment.setLaunchingRole("moderator");
        enrollment.setEditingPermission("writer");
        enrollment.setSessionId("5F89A28E40E1C8BC68FF9F580B55BF87");

        return enrollment;
    }

    private static Enrollment setupEnrollmentForSessionAndRole(String sessionId, String role) {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(UUID.randomUUID().toString());
        enrollment.setUserId("133C86F19427C0791783EF504B35954B");
        enrollment.setLaunchingRole(role);
        enrollment.setEditingPermission("writer");
        enrollment.setSessionId(sessionId);

        return enrollment;
    }
}
