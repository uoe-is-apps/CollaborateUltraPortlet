package uk.ac.ed.uportal.portlets.collaborateportlet.mvc.callback;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ed.collaborate.dao.EnrollmentDao;
import uk.ac.ed.collaborate.data.User;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;

/**
 * Created by v1mburg3 on 20/06/2016.
 */
@Controller
public class CsvDownloadController {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private EnrollmentDao enrollmentDao;

    @RequestMapping(value="/csvDownload" , method = RequestMethod.POST)
    public void sessionUsersDownload(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam String sessionId, @RequestParam String uid) throws Exception {
        logger.debug("Reached the CSV download action");
        logger.debug("Session Id: " + sessionId);
        String userId = request.getRemoteUser();
        if (StringUtils.isBlank(userId)) {
            userId = uid;
        }
        logger.debug("User Id: " + userId);

        final HttpSession session = request.getSession(false);
        boolean isAdminUser = (boolean) session.getAttribute("isAdminUser");

        
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv");
        response.setHeader("content-disposition", "inline; filename=participant_list_"+userId+".csv");

        
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.println("UID,Display Name,Email address,Participant type");

            List<User> moderators = enrollmentDao.getEnrolledUsers(sessionId, "moderator", true);
            if (!isAdminUser && !userIsModerator(userId, moderators)) {
                logger.debug("Session CSV download was accessed by non-moderator user with Id: " + userId);
                outputStream.flush();
                outputStream.close();
                return;
            }

            List<User> internalParticipants = enrollmentDao.getEnrolledUsers(sessionId, "participant", true);
            List<User> externalParticipants = enrollmentDao.getEnrolledUsers(sessionId, "participant", false);

            for (User moderator : moderators) {
                outputStream.println(moderator.getUsernameInternal() + ","
                        + moderator.getDisplayName() + "," + moderator.getEmail() + ",Moderator");
            }
            for (User internalParticipant : internalParticipants) {
                outputStream.println(internalParticipant.getUsernameInternal() + ","
                        + internalParticipant.getDisplayName() + "," + internalParticipant.getEmail() + ",Internal Participant");
            }
            for (User externalParticipant : externalParticipants) {
                outputStream.println("" + ","
                        + externalParticipant.getDisplayName() + "," + externalParticipant.getEmail() + ",Internal Participant");
            }

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            logger.error("Exception caught while exporting session users to CSV", e);
        }
    }

    private static boolean userIsModerator(String userId, List<User> moderators) {
        Collection<User> userModerator = Collections2.filter(moderators, new UserByIdPredicate(userId));
        if (userModerator.size() == 1) {
            return true;
        } else {
            return false;
        }
    }

    private static class UserByIdPredicate implements Predicate<User> {
        private String userId;

        public UserByIdPredicate(String userId) {
            this.userId = userId;
        }

        @Override
        public boolean apply(User user) {
            return user.getUsernameInternal().equalsIgnoreCase(this.userId);
        }
    }
}
