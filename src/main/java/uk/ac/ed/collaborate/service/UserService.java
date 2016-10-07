package uk.ac.ed.collaborate.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ed.collaborate.dao.UserDao;
import uk.ac.ed.collaborate.data.User;

/**
 * Created by v1mburg3 on 13/06/2016.
 */
@Service
public class UserService {
    @Value("${user.getdummyuser:false}")
    private boolean getDummyUser;

    @Autowired
    private CollaborateUltraService collaborateUltraService;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private UserDao userDao;

    public User getInternalUserDetails(String userSearchTerm) {
        if (getDummyUser) {
            return getDummyUser(userSearchTerm);
        }

        return ldapService.getUserDetails(userSearchTerm);
    }

    public User ensureUserExistsForCollaborate(User user) {
        if (StringUtils.isNotBlank(user.getId())) {
            return user;
        }

        User collaborateUser = userDao.getUserByEmail(user.getEmail());
        if (collaborateUser != null) {
            return collaborateUser;
        }

        User createdUser = collaborateUltraService.createUser(user);
        if (createdUser != null) {
            userDao.saveUser(createdUser);
        }
        return createdUser;
    }

    private static User getDummyUser(String userSearchTerm) {
        User dummyUser = new User();
        dummyUser.setDisplayName(userSearchTerm);
        dummyUser.setEmail(userSearchTerm.replaceAll("\\s+",".") + "1@test.com");
        dummyUser.setUsernameInternal(userSearchTerm.replaceAll("\\s+","."));
        dummyUser.setInternal(true);
        return dummyUser;
    }
}
