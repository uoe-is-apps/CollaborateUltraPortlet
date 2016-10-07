package uk.ac.ed.collaborate.dao;

import uk.ac.ed.collaborate.data.User;

import java.util.List;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
public interface UserDao {
    User getUser(String userId);

    User getInternalUser(String internalUserId);

    User getUserByEmail(String email);

    void saveUser(User user);

    void deleteUser(String userId);

    List<User> getAllUsers();
}
