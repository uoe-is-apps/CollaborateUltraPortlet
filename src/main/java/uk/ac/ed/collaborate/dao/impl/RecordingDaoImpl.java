package uk.ac.ed.collaborate.dao.impl;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ed.collaborate.dao.RecordingDao;
import uk.ac.ed.collaborate.data.Recording;

import java.util.List;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class RecordingDaoImpl implements RecordingDao {
    @Autowired
    private SessionFactory sessionFactory;

    public Recording getRecording(Long recordingId) {
        return (Recording) this.getHibernateSession().get(Recording.class, recordingId);
    }

    public void saveRecording(Recording recording) {
        this.getHibernateSession().saveOrUpdate(recording);
    }

    public void deleteRecording(Long recordingId) {
        this.getHibernateSession().delete(this.getRecording(recordingId));
    }

    public void deleteAllSessionRecordings(Long sessionId) {
        List<Recording> recordingsToDelete = this.getAllSessionRecordings(sessionId);
        for (Recording recording : recordingsToDelete) {
            this.getHibernateSession().delete(recording);
        }
    }

    public List<Recording> getAllRecordings() {
        return this.getHibernateSession().createCriteria(Recording.class)
                .addOrder(Order.desc("createdDate"))
                .list();
    }

    public List<Recording> getRecordingsForUser(String uid) {
        return this.getHibernateSession().createCriteria(Recording.class)
                .add(Restrictions.eq("ownerId", uid))
                .addOrder(Order.desc("createdDate"))
                .list();
    }

    public List<Recording> getAllSessionRecordings(Long sessionId) {
        return this.getHibernateSession().createCriteria(Recording.class)
                .add(Restrictions.eq("sessionId", sessionId))
                .addOrder(Order.desc("createdDate"))
                .list();
    }

    private org.hibernate.Session getHibernateSession() {
        return this.sessionFactory.getCurrentSession();
    }
}
