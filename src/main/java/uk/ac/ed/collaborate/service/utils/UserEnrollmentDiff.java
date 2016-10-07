package uk.ac.ed.collaborate.service.utils;

import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v1mburg3 on 15/06/2016.
 */
public class UserEnrollmentDiff {
    private List<User> allUsers;
    private List<User> usersToEnroll;
    private List<Enrollment> allEnrollments;
    private List<Enrollment> enrollmentsToRemove;

    private UserEnrollmentDiff(List<User> usersFromUi, List<Enrollment> enrollmentsFromDb) {
        this.allUsers = usersFromUi;
        this.allEnrollments = enrollmentsFromDb;
        this.usersToEnroll = new ArrayList<>();
        this.enrollmentsToRemove = new ArrayList<>(enrollmentsFromDb);
    }

    public static UserEnrollmentDiff processDiff(List<User> usersFromUi, List<Enrollment> enrollmentsFromDb) {
        UserEnrollmentDiff diff = new UserEnrollmentDiff(usersFromUi, enrollmentsFromDb);
        diff.processDiffs();
        return diff;
    }

    private void processDiffs() {
        for (User user : allUsers) {
            Enrollment userEnrollment = getUserEnrollment(allEnrollments, user);
            if (userEnrollment != null) {
                enrollmentsToRemove.remove(userEnrollment);
            } else {
                usersToEnroll.add(user);
            }
        }
    }

    private static Enrollment getUserEnrollment(List<Enrollment> enrollments, User user) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getUserId().equals(user.getId())) {
                return enrollment;
            }
        }
        return null;
    }

    public List<User> getUsersToEnroll() {
        return usersToEnroll;
    }

    public List<Enrollment> getEnrollmentsToRemove() {
        return enrollmentsToRemove;
    }
}