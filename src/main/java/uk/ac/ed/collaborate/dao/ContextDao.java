package uk.ac.ed.collaborate.dao;

import uk.ac.ed.collaborate.data.Context;

/**
 * Created by rgood on 30/06/2016.
 */
public interface ContextDao {

    /**
     * Get a context
     * @param contextId
     * @return
     */
    Context getContext(String contextId);

    /**
     * Save a context
     * @param context
     */
    void saveContext(Context context);

    /**
     * Gets a context by name
     * @param name
     * @return
     */
    Context getContextByName(String name);



}
