package uk.ac.ed.collaborate.dao.impl;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ed.collaborate.dao.UserDao;
import uk.ac.ed.collaborate.data.User;

import java.util.List;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class UserDaoImpl implements UserDao {
    @Autowired
    private SessionFactory sessionFactory;

    public User getUser(String userId) {
        return (User) this.getHibernateSession().get(User.class, userId);
    }

    public User getInternalUser(String internalUserId) {
        return (User) this.getHibernateSession().createCriteria(User.class)
                .add(Restrictions.eq("usernameInternal", internalUserId))
                .uniqueResult();
    }

    public User getUserByEmail(String email) {
        return (User) this.getHibernateSession().createCriteria(User.class)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }

    public void saveUser(User user) {
        this.getHibernateSession().saveOrUpdate(user);
    }

    public void deleteUser(String userId) {
        User user = this.getUser(userId);
        if (user != null) {
            this.getHibernateSession().delete(user);
        }
    }

    public List<User> getAllUsers() {
        return this.getHibernateSession().createCriteria(User.class)
                .addOrder(Order.desc("created"))
                .list();
    }

    private org.hibernate.Session getHibernateSession() {
        return this.sessionFactory.getCurrentSession();
    }
}
