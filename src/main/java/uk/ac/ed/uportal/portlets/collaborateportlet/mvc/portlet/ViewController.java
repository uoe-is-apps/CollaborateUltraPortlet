/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package uk.ac.ed.uportal.portlets.collaborateportlet.mvc.portlet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import uk.ac.ed.collaborate.data.Recording;
import uk.ac.ed.collaborate.data.Session;
import uk.ac.ed.collaborate.service.AuthorizationService;
import uk.ac.ed.collaborate.service.RecordingService;
import uk.ac.ed.collaborate.service.SessionService;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Main portlet view.
 */
@Controller
@RequestMapping("VIEW")
@SuppressWarnings("unchecked")
public class ViewController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RecordingService recordingService;

    @RequestMapping
    public ModelAndView view(RenderRequest request) {
        final ModelAndView modelAndView = new ModelAndView("CollaborateUltra_view");

        Map<String, String> userInfo = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        String internalUserId = userInfo.get("uid");
        boolean isAdmin = authorizationService.isAdminAccess(request);

        List<Session> sessions;
        if (isAdmin) {
            sessions = sessionService.getSessionsForAdmin();
        }
        else {
            sessions = sessionService.getSessionsForUser(internalUserId);
        }
        modelAndView.addObject("sessions", sessions);

        modelAndView.addObject("feedbackMessage",request.getParameter("feedbackMessage"));
        modelAndView.addObject("warningMessage",request.getParameter("warningMessage"));

        return modelAndView;
    }

    @RequestMapping(params = "action=viewSession")
    public ModelAndView viewSession(RenderRequest request) {
        final ModelAndView modelAndView = new ModelAndView("CollaborateUltra_viewSession");

        String sessionId = request.getParameter("sessionId");
        Session session = sessionService.getSession(sessionId);

        Map<String, String> userInfo = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);

        if (session.getEndTime().after(new Date())) {
            if (StringUtils.isNotBlank(session.getGuestUrl())) {
                modelAndView.addObject("guestUrl", session.getGuestUrl());
            }

            String launchUrl = sessionService.getSessionLaunchUrlForInternalUser(sessionId, userInfo.get("uid"));
            if (StringUtils.isNotBlank(launchUrl)) {
                modelAndView.addObject("launchSessionUrl", launchUrl);
            }
        }

        if (authorizationService.isAdminAccess(request)
                || sessionService.userIsSessionModeratorPresenter(sessionId, userInfo.get("uid"),"moderator")
                || sessionService.userIsSessionModeratorPresenter(sessionId, userInfo.get("uid"),"presenter")) {
            boolean isAdmin = authorizationService.isAdminAccess(request);
            final PortletSession portletSession = request.getPortletSession();
            portletSession.setAttribute("isAdminUser", isAdmin, PortletSession.APPLICATION_SCOPE);
            session.setCurrentUserCanEdit(true);

            modelAndView.addObject("showCSVDownload", "true");
            modelAndView.addObject("uid", userInfo.get("uid"));
        }

        modelAndView.addObject("session", session);

        return modelAndView;
    }

    @RequestMapping(params = "action=viewSessionRecordings")
    public ModelAndView viewRecordings(RenderRequest request) {
        final ModelAndView modelAndView = new ModelAndView("CollaborateUltra_viewSessionRecordings");

        Map<String, String> userInfo = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        String internalUserId = userInfo.get("uid");

        String sessionId = request.getParameter("sessionId");
        logger.debug("Getting recordings for session: " + sessionId + ", with internal user: " + internalUserId);

        List<Recording> recordings = recordingService.getRecordingsForSession(sessionId, internalUserId);
        modelAndView.addObject("recordings", recordings);

        return modelAndView;
    }
}
