package uk.ac.ed.collaborate.dao;

import uk.ac.ed.collaborate.data.Session;

import java.util.List;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
public interface SessionDao {
    Session getSession(String sessionId);

    void saveSession(Session session);

    void deleteSession(String sessionId);

    List<Session> getAllSessions();

    List<Session> getSessionsForInternalUser(String uid);
}
