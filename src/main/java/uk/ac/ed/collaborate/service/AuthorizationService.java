/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ed.collaborate.service;

import java.util.Map;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * Service Class which provides authorisation level methods
 * @author Richard Good
 */
@Service
public class AuthorizationService {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * Is the user in the admin group
     * @param request
     * @return boolean
     */
    public boolean isAdminAccess(RenderRequest request)
    {
        return request.isUserInRole(getAdminRole(request));
    }

    /**
     * Gets the admin group name
     * @param request
     * @return String
     */
    public String getAdminRole(RenderRequest request)
    {
        final PortletPreferences prefs=request.getPreferences();
        return prefs.getValue("adminRole", null);
    }

    /**
     * Is the user allowed full access to the portlet
     * @param request
     * @return boolean
     */
    public boolean isFullAccess(PortletRequest request)
    {
        logger.debug("isFullAccess called");
        String attributeValue = getUserInfo(request).get(this.getUserAttribute(request));
        logger.debug("attributeValue:"+attributeValue);
        logger.debug("full access requires:"+getFullAccessValues(request));
        if (getFullAccessValues(request).contains(attributeValue))
        {
            logger.debug("giving full access");
            return true;
        }
        else
        {
            logger.debug("giving basic acccess");
            return false;
        }
    }

    /**
     * Gets the user info Map from the request
     * @param request
     * @return
     */
    public Map<String,String> getUserInfo(PortletRequest request)
    {
        return (Map<String,String>) request.getAttribute(PortletRequest.USER_INFO);
    }

    /**
     * Gets the userTypeAttribute from the request
     * @param request
     * @return String
     */
    public String getUserAttribute(PortletRequest request)
    {
        final PortletPreferences prefs=request.getPreferences();
        return prefs.getValue("userTypeAttribute", null);
    }

    /**
     * Gets the values required for full access
     * @param request
     * @return String
     */
    public String getFullAccessValues(PortletRequest request)
    {
        final PortletPreferences prefs=request.getPreferences();
        return prefs.getValue("fullAccessValues", null);
    }
}
