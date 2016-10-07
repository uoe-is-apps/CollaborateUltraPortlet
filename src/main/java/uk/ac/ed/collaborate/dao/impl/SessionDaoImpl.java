package uk.ac.ed.collaborate.dao.impl;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
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
import uk.ac.ed.collaborate.dao.SessionDao;
import uk.ac.ed.collaborate.data.Enrollment;
import uk.ac.ed.collaborate.data.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class SessionDaoImpl implements SessionDao {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private SessionFactory sessionFactory;

    public Session getSession(String sessionId) {
        return (Session) this.getHibernateSession().get(Session.class, sessionId);
    }

    public void saveSession(Session session) {
        this.getHibernateSession().saveOrUpdate(session);
    }

    public void deleteSession(String sessionId) {
        this.getHibernateSession().delete(this.getSession(sessionId));
    }

    public List<Session> getAllSessions() {
        return this.getHibernateSession().createCriteria(Session.class)
                .addOrder(Order.desc("startTime"))
                .list();
    }

    public List<Session> getSessionsForInternalUser(String uid) {
        List<Enrollment> enrollments = this.getHibernateSession().createCriteria(Enrollment.class, "e")
                .setFetchMode("e.session", FetchMode.JOIN)
                .createAlias("e.user", "u")
                .add(Restrictions.eq("u.usernameInternal", uid))
                .list();
        Map<String, Session> sessions = new HashMap<>();
        for (Enrollment enrollment : enrollments) {
            Session session = enrollment.getSession();
            session.setCurrentUserCanEdit(
                    session.getCreatorId().equals(uid)
                    || enrollment.getLaunchingRole().equals("moderator"));
            sessions.put(session.getId(), session);
        }

        Criteria userCreatedSessionsQuery = this.getHibernateSession().createCriteria(Session.class)
                .add(Restrictions.eq("creatorId", uid));
        if (sessions.keySet().size() > 0) {
            userCreatedSessionsQuery.add(Restrictions.not(Restrictions.in("id", sessions.keySet())));
        }
        List<Session> createdSessions = userCreatedSessionsQuery.list();
        for (Session createdSession : createdSessions) {
            createdSession.setCurrentUserCanEdit(true);
            sessions.put(createdSession.getId(), createdSession);
        }

        Ordering<Session> createdDateOrdering = Ordering.natural().onResultOf(new SessionStartTimeComparator());
        return createdDateOrdering.sortedCopy(sessions.values());
    }

    private org.hibernate.Session getHibernateSession() {
        return this.sessionFactory.getCurrentSession();
    }

    private static class SessionStartTimeComparator implements Function<Session, Comparable> {
        @Override
        public Comparable apply(Session session) {
            return session.getStartTime();
        }
    }
}
