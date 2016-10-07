package uk.ac.ed.collaborate.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ed.collaborate.dao.ContextDao;
import uk.ac.ed.collaborate.data.Context;

/**
 * Created by rgood on 30/06/2016.
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class ContextDaoImpl implements ContextDao {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Context getContext(String contextId) {
        return (Context) this.getHibernateSession().get(Context.class,contextId);
    }

    @Override
    public Context getContextByName(String name)
    {
        return (Context) this.getHibernateSession().createCriteria(Context.class)
                .add(Restrictions.eq("name", name))
                .uniqueResult();

    }

    @Override
    public void saveContext(Context context) {

        this.getHibernateSession().save(context);

    }

    private org.hibernate.Session getHibernateSession() {
        return this.sessionFactory.getCurrentSession();
    }

}
