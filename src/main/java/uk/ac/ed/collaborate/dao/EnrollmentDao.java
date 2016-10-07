package uk.ac.ed.collaborate.dao;

import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.User;

import java.util.List;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
public interface EnrollmentDao {
    Enrollment getEnrollment(String enrollmentId);

    void saveEnrollment(Enrollment enrollment);

    void deleteEnrollment(String enrollmentId);

    List<Enrollment> getAllEnrollments();

    /**
     * Gets enrollments in a specified session which have a particular role. The user role can include whether or
     * not they are internal to Edinburgh systems (exist in LDAP).
     *
     * @param sessionId The Collaborate Id of the session
     * @param role The Collaborate user role of enrollments to get
     * @param internal Whether to get enrollments only for internal users. Pass null to omit this check.
     * @return All enrollments for the session with the given role.
     */
    List<Enrollment> getEnrollments(String sessionId, String role, Boolean internal);

    /**
     * Gets users who are enrolled in a session with a particular role. The user role can include whether or
     * not they are internal to Edinburgh systems (exist in LDAP).
     *
     * @param sessionId The Collaborate Id of the session
     * @param role The Collaborate user role of users to get
     * @param internal Whether to get records only for internal users. Pass null to omit this check.
     * @return All users who are enrolled in the session with the given role.
     */
    List<User> getEnrolledUsers(String sessionId, String role, Boolean internal);

    /**
     * Gets the enrollment of the given internal user for the session. Expected use is to check the
     * user's role and permissions within the session.
     *
     * @param sessionId The Collaborate Id of the session
     * @param internalUserId The Edinburgh LDAP Id of the user
     * @return The enrollment of the user in the session, or null if not found
     */
    Enrollment getSessionEnrollmentForInternalUser(String sessionId, String internalUserId);

    /**
     * Gets the enrollment of the given internal user for the session. Expected use is to check the
     * user's role and permissions within the session.
     *
     * @param sessionId The Collaborate Id of the session
     * @param userId The Edinburgh LDAP Id of the user
     * @return The enrollment of the user in the session, or null if not found
     */
    Enrollment getSessionEnrollmentForExternalUser(String sessionId, String userId);

    /**
     * Gets all enrollments for a particular session
     * @param sessionId
     * @return List of Enrollment objects
     */
    List<Enrollment> getSessionEnrollments(String sessionId);

    /**
     * Delete an enrollment
     * @param enrollment Enrolment object
     */
    void deleteEnrollment(Enrollment enrollment);
}
