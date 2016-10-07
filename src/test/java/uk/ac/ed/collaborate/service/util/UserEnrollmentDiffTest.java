package uk.ac.ed.collaborate.service.util;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.User;
import uk.ac.ed.collaborate.service.utils.UserEnrollmentDiff;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by v1mburg3 on 15/06/2016.
 */
public class UserEnrollmentDiffTest {
    @Test
    public void processDiff_noNewUsers_noExistingEnrollments_returnsNoNewUsers_returnsNoRemovedEnrollments() {
        List<User> newUsers = new ArrayList<>();
        List<Enrollment> existingEnrollments = new ArrayList<>();

        UserEnrollmentDiff diff = UserEnrollmentDiff.processDiff(newUsers, existingEnrollments);

        assertThat(diff.getUsersToEnroll().size(), equalTo(0));
        assertThat(diff.getEnrollmentsToRemove().size(), equalTo(0));
    }

    @Test
    public void processDiff_usersMatchEnrollments_returnsNoNewUsers_returnsNoRemovedEnrollments() {
        List<User> newUsers = Arrays.asList(
                createUserWithId(),
                createUserWithId(),
                createUserWithId()
        );
        List<Enrollment> existingEnrollments = Arrays.asList(
                createEnrollmentForUser(newUsers.get(0).getId()),
                createEnrollmentForUser(newUsers.get(1).getId()),
                createEnrollmentForUser(newUsers.get(2).getId())
        );

        UserEnrollmentDiff diff = UserEnrollmentDiff.processDiff(newUsers, existingEnrollments);

        assertThat(diff.getUsersToEnroll().size(), equalTo(0));
        assertThat(diff.getEnrollmentsToRemove().size(), equalTo(0));
    }

    @Test
    public void processDiff_noExistingEnrollments_givenNewUser_returnsUserToAdd() {
        List<User> newUsers = Collections.singletonList(createUserWithId());
        List<Enrollment> existingEnrollments = new ArrayList<>();

        UserEnrollmentDiff diff = UserEnrollmentDiff.processDiff(newUsers, existingEnrollments);

        assertThat(diff.getUsersToEnroll().size(), equalTo(1));
        assertThat(diff.getUsersToEnroll().get(0).getId(), equalTo(newUsers.get(0).getId()));
    }

    @Test
    public void processDiff_existingEnrollment_noUsers_returnsEnrollmentToDelete() {
        List<User> newUsers = new ArrayList<>();
        List<Enrollment> existingEnrollments = Collections.singletonList(
                createEnrollmentForUser(createUserWithId().getId()));

        UserEnrollmentDiff diff = UserEnrollmentDiff.processDiff(newUsers, existingEnrollments);

        assertThat(diff.getEnrollmentsToRemove().size(), equalTo(1));
        assertThat(diff.getEnrollmentsToRemove().get(0).getId(), equalTo(existingEnrollments.get(0).getId()));
    }

    private static User createUserWithId() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        return user;
    }

    private static Enrollment createEnrollmentForUser(String userId) {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(StringUtils.reverse(userId));
        enrollment.setUserId(userId);
        return enrollment;
    }
}
