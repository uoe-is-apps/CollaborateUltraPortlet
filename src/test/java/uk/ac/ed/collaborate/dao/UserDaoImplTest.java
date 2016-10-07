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
import uk.ac.ed.collaborate.data.User;

import java.util.List;

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
public class UserDaoImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;

    @Test
    public void getUser_userDoesNotExist_returnsNull() {
        User notCreatedUser = setupUserForSave();

        User returnedUser = userDao.getUser(notCreatedUser.getId());

        assertThat(returnedUser, nullValue());
    }

    @Test
    public void getUser_userExists_returnsUser() {
        User createdUser = setupUserForSave();
        try {
            userDao.saveUser(createdUser);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        User returnedUser = userDao.getUser(createdUser.getId());

        assertThat(returnedUser, notNullValue());
        assertThat(returnedUser.getId(), equalTo(createdUser.getId()));
        assertThat(returnedUser.getDisplayName(), equalTo(createdUser.getDisplayName()));
    }

    @Test
    public void getInternalUser_userDoesNotExist_returnsNull() {
        User notCreatedUser = setupInternalUserForSave();

        User returnedUser = userDao.getInternalUser(notCreatedUser.getUsernameInternal());

        assertThat(returnedUser, nullValue());
    }

    @Test
    public void getInternalUser_userExists_returnsUser() {
        User createdUser = setupInternalUserForSave();
        try {
            userDao.saveUser(createdUser);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        User returnedUser = userDao.getInternalUser(createdUser.getUsernameInternal());

        assertThat(returnedUser, notNullValue());
        assertThat(returnedUser.getId(), equalTo(createdUser.getId()));
        assertThat(returnedUser.getDisplayName(), equalTo(createdUser.getDisplayName()));
    }

    @Test
    public void getUserByEmail_userDoesNotExist_returnsNull() {
        User notCreatedUser = setupUserForSave();

        User returnedUser = userDao.getUserByEmail(notCreatedUser.getEmail());

        assertThat(returnedUser, nullValue());
    }

    @Test
    public void getUserByEmail_userExists_returnsUser() {
        User createdUser = setupUserForSave();
        try {
            userDao.saveUser(createdUser);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        }

        User returnedUser = userDao.getUserByEmail(createdUser.getEmail());

        assertThat(returnedUser, notNullValue());
        assertThat(returnedUser.getId(), equalTo(createdUser.getId()));
        assertThat(returnedUser.getDisplayName(), equalTo(createdUser.getDisplayName()));
    }

    @Test
    public void saveUser_requiredValuesSet_addsUser() {
        userDao.saveUser(setupUserForSave());
        assertThat(this.countUsers(), equalTo(1));
    }

    @Test
    public void deleteUser_noUserFound_doesNotCauseError() {
        User notCreatedUser = setupUserForSave();

        userDao.deleteUser(notCreatedUser.getId());
    }

    @Test
    public void deleteUser_userExists_deletesUser() {
        User createdUser = setupUserForSave();
        try {
            userDao.saveUser(createdUser);
        } catch (Exception e) {
            Assume.assumeNoException(e);
        } finally {
            Assume.assumeThat(this.countUsers(), equalTo(1));
        }

        userDao.deleteUser(createdUser.getId());

        assertThat(this.countUsers(), equalTo(0));
    }

    @Test
    public void getAllUsers_noExistingUsers_returnsEmptyList() {
        List<User> users = userDao.getAllUsers();

        assertThat(users, notNullValue());
        assertThat(users.size(), equalTo(0));
    }

    @Test
    public void getAllUsers_usersExist_returnsAllUsers() {
        try {
            userDao.saveUser(setupUniqueUserForSave("User 1"));
            userDao.saveUser(setupUniqueUserForSave("User 2"));
            userDao.saveUser(setupUniqueUserForSave("User 3"));
        } catch (Exception e) {
            Assume.assumeNoException(e);
        } finally {
            Assume.assumeThat(this.countUsers(), equalTo(3));
        }

        List<User> users = userDao.getAllUsers();

        assertThat(users, notNullValue());
        assertThat(users.size(), equalTo(3));
    }

    private int countUsers() {
        return JdbcTestUtils.countRowsInTable(this.jdbcTemplate, "VC_ULTRA_USERS");
    }

    private static User setupUserForSave() {
        User user = new User();
        user.setEmail("user@test.com");
        user.setDisplayName("A User");
        user.setId("vTXwDDzoMuIlBMr7wHQgrSgj");

        return user;
    }

    private static User setupUniqueUserForSave(String userId) {
        User user = setupUserForSave();
        user.setId(userId);
        return user;
    }

    private static User setupInternalUserForSave() {
        User user = setupUserForSave();
        user.setUsernameInternal("Edinburgh LDAP User");
        return user;
    }
}
