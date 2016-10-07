package uk.ac.ed.collaborate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.collaborate.dao.ContextDao;
import uk.ac.ed.collaborate.data.Context;
import uk.ac.ed.collaborate.service.models.ContextSession;

/**
 * Created by rgood on 30/06/2016.
 */
@Service
public class ContextService {

    @Autowired
    CollaborateUltraService collaborateUltraService;

    @Autowired
    ContextDao contextDao;

    public Context createAndSaveContext(Context context)
    {
        Context contextWithId = collaborateUltraService.createContext(context);
        contextDao.saveContext(contextWithId);
        return contextWithId;
    }

    public void saveContext(Context context)
    {
        contextDao.saveContext(context);
    }

    public Context getContextByName(String name)
    {
        Context context = contextDao.getContextByName(name);
        return context;
    }

    public Context getContextByExtId(String extid)
    {
        try {
            return collaborateUltraService.getContextByExtId(extid);
        }
        catch (Exception e)
        {
            return null;
        }

    }

    public void saveSessionContext(String contextId,String sessionId)
    {

        ContextSession contextSession = new ContextSession();
        contextSession.setId(sessionId);
        collaborateUltraService.saveContextSession(contextSession,contextId);
    }

}
