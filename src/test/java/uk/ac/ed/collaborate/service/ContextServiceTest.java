package uk.ac.ed.collaborate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ed.collaborate.dao.ContextDao;
import uk.ac.ed.collaborate.data.Context;
import uk.ac.ed.collaborate.service.models.ContextSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by rgood on 30/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContextServiceTest {

    @Mock
    CollaborateUltraService collaborateUltraService;

    @Mock
    ContextDao contextDao;

    @InjectMocks
    ContextService contextService;

    @Test
    public void testCreateAndSaveContext()
    {
        Context context = setupContextForCreate();
        context.setId("1234");

        when(collaborateUltraService.createContext(context)).thenReturn(context);
        contextService.createAndSaveContext(context);
        verify(collaborateUltraService).createContext(context);
        verify(contextDao).saveContext(context);
    }

    @Test
    public void testSaveContext()
    {
        Context context = setupContextForCreate();
        context.setId("1234");
        contextService.saveContext(context);
        verify(contextDao).saveContext(context);
    }

    @Test
    public void testSaveSessionContext()
    {
        ContextSession contextSession = new ContextSession();
        contextSession.setId("67890");

        contextService.saveSessionContext("12345","67890");

        verify(collaborateUltraService).saveContextSession(Matchers.refEq(contextSession),Matchers.eq("12345"));
    }

    @Test
    public void testGetContext()
    {
        String name = "uun";
        when(contextDao.getContextByName(name)).thenReturn(setupContextForCreate());
        Context context = contextService.getContextByName(name);

        assertEquals("uun",context.getLabel());

    }

    private static Context setupContextForCreate()
    {
        Context context = new Context();
        context.setExtId("uun");
        context.setLabel("uun");
        context.setName("uun");
        context.setTitle("uun");
        return context;
    }
}
