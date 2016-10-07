package uk.ac.ed.collaborate.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ed.collaborate.dao.EnrollmentDao;
import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class EnrollmentDaoImpl implements EnrollmentDao {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private SessionFactory sessionFactory;

    public Enrollment getEnrollment(String enrollmentId) {
        return (Enrollment) this.getHibernateSession().get(Enrollment.class, enrollmentId);
    }

    public void saveEnrollment(Enrollment enrollment) {
        this.getHibernateSession().saveOrUpdate(enrollment);
    }

    public void deleteEnrollment(String enrollmentId) {
        Enrollment enrollment = this.getEnrollment(enrollmentId);
        if (enrollment != null) {
            this.getHibernateSession().delete(enrollment);
        }
    }

    public void deleteEnrollment(Enrollment enrollment)
    {
        this.getHibernateSession().delete(enrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return this.getHibernateSession().createCriteria(Enrollment.class)
                .list();
    }

    public List<Enrollment> getSessionEnrollments(String sessionId){

        Criteria enrollmentCriteria = this.getHibernateSession().createCriteria(Enrollment.class)
                .add(Restrictions.eq("sessionId", sessionId));

        return enrollmentCriteria.list();

    }

    /**
     * {@inheritDoc}
     */
    public List<Enrollment> getEnrollments(String sessionId, String role, Boolean internal) {
        Criteria enrollmentCriteria = this.getHibernateSession().createCriteria(Enrollment.class)
                .add(Restrictions.eq("sessionId", sessionId))
                .add(Restrictions.eq("launchingRole", role));

        if (internal != null) {
            enrollmentCriteria
                    .createCriteria("user", "u")
                    .add(Restrictions.eq("u.internal", internal));
        }

        return enrollmentCriteria.list();
    }

    /**
     * {@inheritDoc}
     */
    public List<User> getEnrolledUsers(String sessionId, String role, Boolean internal) {
        logger.debug("Getting enrolled users for SessionId:" + sessionId + ",Role=" + role + ",Internal=" + internal);
        Criteria enrollmentCriteria = this.getHibernateSession().createCriteria(Enrollment.class)
                .setFetchMode("user", FetchMode.JOIN)
                .add(Restrictions.eq("sessionId", sessionId))
                .add(Restrictions.eq("launchingRole", role))
                .createAlias("user", "u")
                .addOrder(Order.desc("u.created"));

        if (internal != null) {
            enrollmentCriteria
                    .add(Restrictions.eq("u.internal", internal));
        }
        List<Enrollment> enrollments = enrollmentCriteria.list();

        logger.debug("Found session enrollments, count:" + enrollments.size());

        List<User> users = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            users.add(enrollment.getUser());
        }
        logger.debug("Returning " + users.size() + " users in session " + sessionId);
        return users;
    }

    /**
     * {@inheritDoc}
     */
    public Enrollment getSessionEnrollmentForInternalUser(String sessionId, String internalUserId) {
        return (Enrollment) this.getHibernateSession().createCriteria(Enrollment.class)
                .createAlias("user", "u")
                .add(Restrictions.eq("sessionId", sessionId))
                .add(Restrictions.eq("u.usernameInternal", internalUserId))
                .uniqueResult();
    }

    @Override
    public Enrollment getSessionEnrollmentForExternalUser(String sessionId, String userId) {
        return (Enrollment) this.getHibernateSession().createCriteria(Enrollment.class)
                .createAlias("user", "u")
                .add(Restrictions.eq("sessionId", sessionId))
                .add(Restrictions.eq("u.id", userId))
                .uniqueResult();
    }

    private org.hibernate.Session getHibernateSession() {
        return this.sessionFactory.getCurrentSession();
    }
}
