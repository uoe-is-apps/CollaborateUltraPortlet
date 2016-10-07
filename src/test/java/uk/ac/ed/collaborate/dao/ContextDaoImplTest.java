package uk.ac.ed.collaborate.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.collaborate.data.Context;

import static org.junit.Assert.assertNotNull;

/**
 * Created by rgood on 30/06/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:testDataAccessContext.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ContextDaoImplTest {

    @Autowired
    ContextDao contextDao;

    private Context setupTestContext()
    {
        Context context = new Context();
        context.setId("12345");
        context.setExtId("uun");
        context.setLabel("uun");
        context.setName("uun");
        context.setTitle("uun");
        return context;
    }

    @Test
    public void testSaveAndGetContext()
    {
        contextDao.saveContext(setupTestContext());

        assertNotNull(contextDao.getContext(setupTestContext().getId()));

        assertNotNull(contextDao.getContextByName(setupTestContext().getName()));
    }


}
