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

import javax.portlet.*;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ed.collaborate.data.Context;
import uk.ac.ed.collaborate.data.Session;
import uk.ac.ed.collaborate.data.User;
import uk.ac.ed.collaborate.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.portlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EditController renders the editing interface and persists user selections
 * to the portlet's preferences.
 */
@Controller
@RequestMapping("EDIT")
@SuppressWarnings("unchecked")
public class EditController {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContextService contextService;

    @RequestMapping
    public ModelAndView displaySessionEditForm(RenderRequest request, ModelAndView requestModel) {
        final ModelAndView responseModel = new ModelAndView("CollaborateUltra_edit");

        logger.debug("Displaying session edit form");

        if (requestModel.isEmpty()) {
            logger.debug("Creating new session");
            Session session = sessionService.setupSessionWithDefaultValues();
            responseModel.addObject("session", session);
        }
        else {
            logger.debug("Re-populating form model with existing session");
            transferSessionModelObjects(requestModel, responseModel);
        }

        if (responseModel.getModelMap().get("moderators") == null) {
            Map<String, String> userInfo = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
            User currentUser = extractCurrentUser(userInfo);
            
            
            if(currentUser.getEmail() == null || !currentUser.getEmail().contains("@")){
                logger.debug("Validation errors while displaying session");
                
                List<String> noEmailErrorMessage = new ArrayList<String>();
                noEmailErrorMessage.add("error.noemail");
                responseModel.addObject("errorMessage", noEmailErrorMessage);
                
                return responseModel;
            }
            
            
            User verifiedCollaborateUser = userService.ensureUserExistsForCollaborate(currentUser);

            List<User> defaultModerators = new ArrayList<>();
            defaultModerators.add(verifiedCollaborateUser);
            responseModel.addObject("moderators", defaultModerators);
        }

        if (authorizationService.isAdminAccess(request) || authorizationService.isFullAccess(request)) {
            responseModel.addObject("fullAccess", "true");
        }

        return responseModel;
    }

    @RequestMapping(params = "action=editSession")
    public ModelAndView editSession(RenderRequest request) {
        ModelAndView modelAndView = new ModelAndView("CollaborateUltra_edit");

        String sessionId = request.getParameter("sessionId");
        logger.debug("Editing session with Id: " + sessionId);
        try {
            modelAndView.addObject("session", sessionService.getSession(sessionId));
            List<User> moderatorParticipants = sessionService.getSessionEnrolledUsers(sessionId, "moderator", true);
            moderatorParticipants.addAll(sessionService.getSessionEnrolledUsers(sessionId,"presenter",true));
            modelAndView.addObject("moderators", moderatorParticipants);
            modelAndView.addObject("intParticipants", sessionService.getSessionEnrolledUsers(sessionId, "participant", true));
            modelAndView.addObject("extParticipants", sessionService.getSessionEnrolledUsers(sessionId, "participant", false));
            
        } catch (Exception e) {
            List<String> errorMessage = new ArrayList<String>();
            errorMessage.add("error.sessionretrievalproblem");
            modelAndView.addObject("errorMessage", errorMessage);
        }

        if (authorizationService.isAdminAccess(request) || authorizationService.isFullAccess(request))
        {
            modelAndView.addObject("fullAccess","true");
        }

        return modelAndView;
    }

    @RequestMapping(params = "action=Save Session", method = RequestMethod.POST)
    public void saveSession(ActionRequest request, ActionResponse response, ModelAndView modelAndView,
                            Session session) throws Exception {


        logger.debug("Reached the SaveSession action");

        final PortletPreferences prefs = request.getPreferences();

        List<String> errorMessage = addUnboundSessionDataToModel(request, session, modelAndView);
        if (errorMessage.size() > 0) {
            logger.debug("Validation errors while attempting to save a session");
            modelAndView.addObject("errorMessage", errorMessage);
            return;
        }

        Map<String, String> userInfo = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        User creatorUser = extractCurrentUser(userInfo);

        
        if(creatorUser.getEmail() == null || !creatorUser.getEmail().contains("@")){
            logger.debug("Validation errors while attempting to save a session");
            
            List<String> noEmailErrorMessage = new ArrayList<String>();
            noEmailErrorMessage.add("error.noemail");
            modelAndView.addObject("errorMessage", noEmailErrorMessage);

            return;
        }
        
        if (StringUtils.isBlank(session.getCreatorId())) {
            session.setCreatorId(creatorUser.getUsernameInternal());
        }

        Session savedSession = sessionService.saveSession(session);
        if (savedSession == null) {
            logger.debug("Data access error while attempting to save a session");
            modelAndView.addObject("errormessage", "Error when saving the session");
            return;
        }

        String launchUrl = getLaunchUrl(prefs,request);

        List<User> moderators = (List<User>)modelAndView.getModelMap().get("moderators");

        if (authorizationService.isFullAccess(request))
        {
            sessionService.processModeratorParticipantEnrollments(savedSession, creatorUser, moderators,launchUrl,"moderator");
        }
        else
        {
            sessionService.processModeratorParticipantEnrollments(savedSession, creatorUser, moderators,launchUrl,"presenter");
        }


        List<User> internalParticipants = (List<User>)modelAndView.getModelMap().get("intParticipants");
        sessionService.processInternalParticipantEnrollments(savedSession, creatorUser, internalParticipants,launchUrl);

        List<User> externalParticipants = (List<User>)modelAndView.getModelMap().get("extParticipants");
        sessionService.processExternalParticipantEnrollments(savedSession, creatorUser, externalParticipants);

        // Set up context if not already existing and save session into it
        Context context = contextService.getContextByName(creatorUser.getUsernameInternal());
        if (context==null)
        {
            context = contextService.getContextByExtId(creatorUser.getUsernameInternal());
            if (context==null)
            {
                context = new Context();
                context.setExtId(creatorUser.getUsernameInternal());
                context.setLabel(creatorUser.getUsernameInternal());
                context.setName(creatorUser.getUsernameInternal());
                context.setTitle(creatorUser.getUsernameInternal());
                context = contextService.createAndSaveContext(context);
            }
            else {
                contextService.saveContext(context);
            }


            contextService.saveSessionContext(context.getId(),savedSession.getId());

        }
        else
        {
            contextService.saveSessionContext(context.getId(),savedSession.getId());
            logger.info("update existing session - success");
        }
        
        //TEL032-17
        /*
        As discussed via email. 
        emails should only be sent to all participants when date/time changes. 
        When a new participant or moderator is added an email should only be sent to that individual, the other attendees do not receive an email. 
        There are two different links depending on if someone is a UoE member or not. 
        This one for internal participants & moderators which takes them to MyEd channel: 
        " To join the session, click on the following link to launch MyEd: https://www-test.myed.ed.ac.uk/uPortal/render.userLayoutRootNode.uP?uP_fname=blackboardvc-portlet" 
        For external participants it should be the one you quoted below so: 
        " ... To join the session, click on the following link: http://wcrxxstg01.bbcollab.com/guest/5037862D71CFF754B2ED72D9974181C6 ..."          
        */
        if(request.getParameter("update_mode") != null && request.getParameter("update_mode").equals("yes")){
            
            String originalStartTime = request.getParameter("original_startTime"); 
            String originalEndTime = request.getParameter("original_endTime");
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String newStartTime = simpleDateFormat.format(savedSession.getStartTime());
            String newEndTime = simpleDateFormat.format(savedSession.getEndTime());
            
            if(!newStartTime.equals(originalStartTime) || !newEndTime.equals(originalEndTime)){
                String internalLaunchUrl = launchUrl;
                sessionService.sendNotificationEmailForSessionUpdate(savedSession, internalLaunchUrl, originalStartTime, originalEndTime);    
            }            
        }
        
        response.setRenderParameter("feedbackMessage", "feedbackmessage.sessionsaved");
        response.setPortletMode(PortletMode.VIEW);
        response.setWindowState(WindowState.NORMAL);
    }

    @RequestMapping(params = "action=deleteSessions", method = RequestMethod.POST)
    public void deleteSessions(ActionRequest request, ActionResponse response, ModelAndView mView) throws Exception {
        logger.debug("Reached the Delete Sessions action");

        String[] sessionIds = request.getParameterValues("deleteSession");
        if (sessionIds == null){
            response.setRenderParameter("warningMessage", "feedbackmessage.nosessionsselected");
        } else {
            for (String sessionId : sessionIds) {
                try {
                    sessionService.deleteSession(sessionId);
                } catch (Exception e) {
                    List<String> errorMessage = new ArrayList<>();
                    errorMessage.add("error.deletesessionerror");
                    mView.addObject("errorMessage", errorMessage);
                }
            }
            response.setRenderParameter("feedbackMessage", "feedbackmessage.sessionsdeleted");
        }

        response.setPortletMode(PortletMode.VIEW);
    }

    @RequestMapping(params = "action=deleteRecordings", method = RequestMethod.POST)
    public void deleteSessionRecordings(ActionRequest request, ActionResponse response, ModelAndView mView) throws Exception {
        logger.debug("Reached the Delete Recordings action");

        String[] recordingsToDelete = request.getParameterValues("deleteRecording");
        if (recordingsToDelete == null) {
            response.setRenderParameter("warningMessage", "feedbackmessage.norecordingsselected");
        } else {
            logger.debug("Deleting " + recordingsToDelete.length + " recordings");
            recordingService.deleteRecordings(recordingsToDelete);
            response.setRenderParameter("feedbackMessage", "feedbackmessage.recordingsdeleted");
        }

        response.setPortletMode(PortletMode.VIEW);
    }

    @RequestMapping(params = "action=Add Moderator(s)", method = RequestMethod.POST)
    public void addModerator(ActionRequest request, ModelAndView mView, Session session) {
        logger.debug("Reached the Add Moderator action");
        this.addUnboundSessionDataToModel(request, session, mView);

        String moderatorsString = request.getParameter("moderatorUid");
        List<String> errorMessage = new ArrayList<>();

        if (StringUtils.isBlank(moderatorsString)) {
            errorMessage.add("error.nomoderatorstoadd");
            mView.addObject("errorMessage", errorMessage);
            return;
        }

        List<User> moderatorList;
        if (mView.getModelMap().get("moderators") != null) {
            moderatorList = (List<User>) mView.getModelMap().get("moderators");
        } else {
            moderatorList = new ArrayList<>();
        }

        logger.debug("Adding moderator to existing list of " + moderatorList.size());

        String[] users = moderatorsString.split(",");
        for (String userToLookup : users) {
            User user = userService.getInternalUserDetails(userToLookup);
            if (user == null) {
                errorMessage.add("User '" + userToLookup + " not found");
            } else {
                user.setInternal(true);
                User verifiedCollaborateUser = userService.ensureUserExistsForCollaborate(user);
                if (verifiedCollaborateUser == null) {
                    errorMessage.add("User '" + userToLookup + "' could not be added");
                } else {
                    moderatorList.add(verifiedCollaborateUser);
                }
            }
        }

        if (errorMessage.size() > 0) {
            if (users.length > errorMessage.size()) {
                mView.addObject("warningMessage", "warningmessage.somemoderatorsadded");
            }
            mView.addObject("errorMessage", errorMessage);
        } else {
            mView.addObject("feedbackMessage", "feedbackmessage.moderatorsadded");
        }

        mView.addObject("moderators", moderatorList);
    }

    @RequestMapping(params = "action=Delete Moderator(s)")
    public void deleteModerator(ActionRequest request, ModelAndView mView, Session session) {
        logger.debug("Reached the Delete Moderator action");
        this.addUnboundSessionDataToModel(request, session, mView);

        String[] moderatorsToDelete = request.getParameterValues("deleteModerator");
        if (moderatorsToDelete == null || moderatorsToDelete.length == 0)
        {
            List<String> errorMessage = new ArrayList();
            errorMessage.add("error.nomoderatorsselected");
            mView.addObject("errorMessage",errorMessage);
        }
        else
        {
            int delRow;
            List<User> moderatorList = (List<User>)mView.getModelMap().get("moderators");

            for (int i=(moderatorsToDelete.length-1);i>-1;i--)
            {
                delRow = new Integer(moderatorsToDelete[i]);
                moderatorList.remove(delRow);
            }
            mView.addObject("moderators",moderatorList);
            mView.addObject("feedbackMessage","feedbackmessage.moderatorsremoved");
        }
    }

    @RequestMapping(params = "action=Add Participant(s)", method = RequestMethod.POST)
    public void addInternalParticipant(ActionRequest request, ModelAndView mView, Session session) {
        logger.debug("Reached the Add Internal Participant action");
        this.addUnboundSessionDataToModel(request, session, mView);

        String participantsString = request.getParameter("intParticipantUid");
        List<String> errorMessage = new ArrayList<>();

        if (StringUtils.isBlank(participantsString)) {
            errorMessage.add("error.noparticipantstoadd");
            mView.addObject("errorMessage", errorMessage);
            return;
        }

        List<User> participantList;
        if (mView.getModelMap().get("intParticipants") != null) {
            participantList = (List<User>) mView.getModelMap().get("intParticipants");
        } else {
            participantList = new ArrayList<>();
        }

        String[] users = participantsString.split(",");
        for (String userToLookup : users) {
            User user = userService.getInternalUserDetails(userToLookup);
            if (user == null) {
                errorMessage.add("User '" + userToLookup + " not found");
            } else {
                user.setInternal(true);
                User verifiedCollaborateUser = userService.ensureUserExistsForCollaborate(user);
                if (verifiedCollaborateUser == null) {
                    errorMessage.add("User '" + userToLookup + "' could not be added");
                } else {
                    participantList.add(verifiedCollaborateUser);
                }
            }
        }

        if (errorMessage.size() > 0) {
            if (users.length > errorMessage.size()) {
                mView.addObject("warningMessage", "warningmessage.someinternalparticipantsadded");
            }
            mView.addObject("errorMessage", errorMessage);
        } else{
            mView.addObject("feedbackMessage", "feedbackmessage.internalparticipantsadded");
        }

        mView.addObject("intParticipants", participantList);
    }

    @RequestMapping(params = "action=Delete Internal Participant(s)", method = RequestMethod.POST)
    public void deleteInternalParticipant(ActionRequest request, ModelAndView mView, Session session) {
        logger.debug("Reached the Delete Internal Participant action");
        this.addUnboundSessionDataToModel(request, session, mView);

        String[] participantsToDelete = request.getParameterValues("deleteIntParticipant");
        if (participantsToDelete == null || participantsToDelete.length == 0)
        {
            List<String> errorMessage = new ArrayList();
            errorMessage.add("error.nointernalparticipantselected");
            mView.addObject("errorMessage",errorMessage);
        }
        else
        {
            int delRow;
            List<User> participantList = (List<User>)mView.getModelMap().get("intParticipants");

            for (int i=(participantsToDelete.length-1);i>-1;i--)
            {
                delRow = new Integer(participantsToDelete[i]);
                participantList.remove(delRow);
            }
            mView.addObject("intParticipants",participantList);
            mView.addObject("feedbackMessage","feedbackmessage.internalparticipantsremoved");
        }
    }

    @RequestMapping(params = "action=Add External Participant", method = RequestMethod.POST)
    public void addExternalParticipant(ActionRequest request, ModelAndView mView, Session session) {
        logger.debug("Reached the Add External Participant action");
        this.addUnboundSessionDataToModel(request, session, mView);

        List<String> errorMessage = new ArrayList<>();
        String displayName = request.getParameter("extParticipantDisplayName");
        String email = request.getParameter("extParticipantEmail");

        if (StringUtils.isBlank(displayName) || StringUtils.isBlank(email)) {
            errorMessage.add("error.extparticipantdisplayandemailmustbeprovided");
            mView.addObject("errorMessage", errorMessage);
            return;
        }

        List<User> participantList;
        if (mView.getModelMap().get("extParticipants") != null) {
            participantList = (List<User>) mView.getModelMap().get("extParticipants");
        } else {
            participantList = new ArrayList<>();
        }

        User user = new User();
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setInternal(false);

        User verifiedCollaborateUser = userService.ensureUserExistsForCollaborate(user);
        if (verifiedCollaborateUser == null) {
            errorMessage.add("User '" + displayName + "' could not be added");
        } else {
            participantList.add(verifiedCollaborateUser);
        }

        if (errorMessage.size() > 0) {
            mView.addObject("warningMessage", "warningmessage.someexternalparticipantsadded");
        } else {
            mView.addObject("feedbackMessage", "feedbackmessage.externalparticipantsadded");
        }

        mView.addObject("extParticipants", participantList);
    }

    @RequestMapping(params = "action=Delete External Participant(s)", method = RequestMethod.POST)
    public void deleteExternalParticipant(ActionRequest request, ModelAndView mView, Session session) {
        logger.debug("Reached the Delete External Participant action");
        this.addUnboundSessionDataToModel(request, session, mView);

        String[] participantsToDelete = request.getParameterValues("deleteExtParticipant");
        if (participantsToDelete == null || participantsToDelete.length == 0)
        {
            List<String> errorMessage = new ArrayList();
            errorMessage.add("error.noexternalparticipantselected");
            mView.addObject("errorMessage",errorMessage);
        }
        else
        {
            int delRow;
            List<User> participantList = (List<User>)mView.getModelMap().get("extParticipants");

            for (int i=(participantsToDelete.length-1);i>-1;i--)
            {
                delRow = new Integer(participantsToDelete[i]);
                participantList.remove(delRow);
            }
            mView.addObject("extParticipants",participantList);
            mView.addObject("feedbackMessage","feedbackmessage.externalparticipantsremoved");
        }
    }

    /**
     * Transfers objects which are relevant to the edit screen of a Collaborate Session entity between request
     * and response models.
     *
     * @param requestModel The model received from the request
     * @param responseModel The model being prepared for the response
     */
    private void transferSessionModelObjects(ModelAndView requestModel, ModelAndView responseModel) {
        for(Map.Entry pairs : requestModel.getModelMap().entrySet()) {
            if (pairs.getKey().equals("errorMessage"))
            {
                responseModel.addObject("errorMessage", pairs.getValue());
            }
            else if (pairs.getKey().equals("feedbackMessage"))
            {
                responseModel.addObject("feedbackMessage", pairs.getValue());
            }
            else if (pairs.getKey().equals("warningMessage"))
            {
                responseModel.addObject("warningMessage", pairs.getValue());
            }
            else if (pairs.getKey().equals("session"))
            {
                Session session = (Session) pairs.getValue();
                responseModel.addObject("session", session);
            }
            else if (pairs.getKey().equals("extParticipants"))
            {
                List<User> extParticipantList = (List<User>) pairs.getValue();
                for (User user : extParticipantList) {
                    logger.debug("list debug:(" +user.getDisplayName() + "," + user.getEmail() + ")");
                }
                responseModel.addObject("extParticipants", extParticipantList);

            }
            else if (pairs.getKey().equals("moderators"))
            {
                List<User> moderatorList = (List<User>) pairs.getValue();
                for (User user : moderatorList) {
                    logger.debug("list debug:(" + user.getId() + "," + user.getDisplayName() + "," + user.getEmail() + ")");
                }
                responseModel.addObject("moderators", moderatorList);
            }
            else if (pairs.getKey().equals("intParticipants"))
            {
                List<User> intParticipantList = (List<User>) pairs.getValue();
                for (User user : intParticipantList) {
                    logger.debug("list debug:(" + user.getId() + "," + user.getDisplayName() + "," + user.getEmail() + ")");
                }
                responseModel.addObject("intParticipants", intParticipantList);
            }
        }
    }

    /**
     * Perform manual model binding for Session start and end dates
     *
     * @param request The request to extract start and end dates from
     * @param session The session to add start and end dates to
     * @return Validation error messages generated by this binding. The list will be empty if binding was successful
     */
    private List<String> addValidatedRequestDatesToSession(ActionRequest request, Session session) {
        List<String> errorMessage = new ArrayList<>();
        Date startTime = null;
        Date endTime = null;
        try {
            String startTimeString = request.getParameter("startdate") + " "
                    + request.getParameter("startHour") + ":"
                    + request.getParameter("startMinute");

            startTime = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)
                    .parse(startTimeString);
            session.setStartTime(startTime);
        } catch (Exception e) {
            errorMessage.add("error.startdateinvalid");
        }
        try {
            String endTimeString = request.getParameter("enddate") + " "
                    + request.getParameter("endHour") + ":"
                    + request.getParameter("endMinute");

            endTime = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)
                    .parse(endTimeString);
            session.setEndTime(endTime);
        } catch (Exception e) {
            errorMessage.add("error.enddateinvalid");
        }
        if (startTime != null && endTime != null) {
            if (endTime.before(startTime)) {
                errorMessage.add("error.startdatebeforeenddate");
            }
            Date now = new Date();
            if (startTime.before(now)) {
                errorMessage.add("error.startdatebeforenow");
            }
            Period sessionPeriod = new Period(new DateTime(startTime), new DateTime(endTime));
            if (sessionPeriod.getYears() > 0) {
                errorMessage.add("error.dateyearapart");
            }
        }

        return errorMessage;
    }

    /**
     * Perform manual model binding to extract all relevant Session data and add it to the model. This process
     * includes the Session object itself (retrieved from automatic model binding) and the lists of users with
     * different roles.
     *
     * @param request The request to extract session data from
     * @param session The session which was automatically bound from the request
     * @param modelAndView The model to bind
     * @return A list of error messages generated during binding. The list will be empty if binding was successful
     */
    private List<String> addUnboundSessionDataToModel(ActionRequest request, Session session, ModelAndView modelAndView) {
        List<String> errorMessage = addValidatedRequestDatesToSession(request, session);
        if (StringUtils.isBlank(session.getName())) {
            errorMessage.add("error.sessionnamemissing");
        }
        modelAndView.addObject("session", session);

        List<User> moderatorList = new ArrayList<>();
        String[] moderatorIds = request.getParameterValues("moderatorIds");
        String[] moderatorUids = request.getParameterValues("moderatorUids");
        String[] moderatorEmails = request.getParameterValues("moderatorEmails");
        String[] moderatorDisplayNames = request.getParameterValues("moderatorDisplayNames");
        if (moderatorIds != null) {
            User user;
            for (int i = 0; i < moderatorIds.length; i++) {
                user = new User();
                user.setId(moderatorIds[i]);
                user.setUsernameInternal(moderatorUids[i]);
                user.setEmail(moderatorEmails[i]);
                user.setDisplayName(moderatorDisplayNames[i]);
                user.setInternal(true);
                moderatorList.add(user);
            }
        }
        modelAndView.addObject("moderators", moderatorList);

        List<User> intParticipantList = new ArrayList<>();
        String[] intParticipantIds = request.getParameterValues("intParticipantIds");
        String[] intParticipantUids = request.getParameterValues("intParticipantUids");
        String[] intParticipantEmails = request.getParameterValues("intParticipantEmails");
        String[] intParticipantDisplayNames = request.getParameterValues("intParticipantDisplayNames");
        if (intParticipantIds != null) {
            User user;
            for (int i = 0; i < intParticipantIds.length; i++) {
                user = new User();
                user.setId(intParticipantIds[i]);
                user.setUsernameInternal(intParticipantUids[i]);
                user.setEmail(intParticipantEmails[i]);
                user.setDisplayName(intParticipantDisplayNames[i]);
                user.setInternal(true);
                intParticipantList.add(user);
            }
        }
        modelAndView.addObject("intParticipants", intParticipantList);

        List<User> extParticipantList = new ArrayList<>();
        String[] extParticipantIds = request.getParameterValues("extParticipantIds");
        String[] extParticipantEmails = request.getParameterValues("extParticipantEmails");
        String[] extParticipantDisplayNames = request.getParameterValues("extParticipantDisplayNames");
        if (extParticipantEmails != null) {
            User user;
            for (int i = 0; i < extParticipantIds.length; i++) {
                user = new User();
                user.setId(extParticipantIds[i]);
                user.setEmail(extParticipantEmails[i]);
                user.setDisplayName(extParticipantDisplayNames[i]);
                user.setInternal(false);
                extParticipantList.add(user);
            }
        }
        modelAndView.addObject("extParticipants", extParticipantList);

        return errorMessage;
    }

    /**
     * Gets a user object representing the internally-held attributes of the user making the current request.
     *
     * @param userInfo A map of user-info attributes extracted from the current request
     * @return A user object representing the current user
     */
    private User extractCurrentUser(Map<String, String> userInfo) {
        User creatorUser = new User();
        if (StringUtils.isNotBlank(userInfo.get("mail")))
        {
            logger.info("extractCurrentUser found uid - " + userInfo.get("uid"));
            logger.info("extractCurrentUser found mail - " + userInfo.get("mail"));            
            if(userInfo.get("mail") != null){
                creatorUser.setEmail(userInfo.get("mail"));
            }
            creatorUser.setUsernameInternal(userInfo.get("uid"));
            creatorUser.setDisplayName(userInfo.get("displayName"));
        }
        else
        {
            logger.info("extractCurrentUser mail not found - call getInternalUserDetails");
            
            creatorUser = userService.getInternalUserDetails(userInfo.get("uid"));            
            logger.info("getInternalUserDetails returns (creatorUser == null) - " + (creatorUser == null));   
            
            //if creatorUser is not in ldap, this will return null user, which cause error in portlet, ref: TEL032-28       
            if(creatorUser == null){
                creatorUser = new User();
                
                if(userInfo.get("mail") != null){
                    creatorUser.setEmail(userInfo.get("mail"));
                    logger.info("creatorUser.mail is not null");
                }else{
                    logger.info("creatorUser.mail is null");
                }
                
                if(userInfo.get("uid") != null){
                    creatorUser.setUsernameInternal(userInfo.get("uid"));
                    logger.info("creatorUser.uid is not null");
                }else{
                    logger.info("creatorUser.uid is null");
                }
                
                if(userInfo.get("displayName") != null){
                    creatorUser.setDisplayName(userInfo.get("displayName"));
                    logger.info("creatorUser.displayName is not null");      
                }else{
                    logger.info("creatorUser.displayName is null");          
                }
            } 
           
        }
        
        creatorUser.setInternal(true);
        return creatorUser;
    }

    /**
     * Helper class to create the url to join the session
     * getLaunchUrl
     * @param preference
     * @param request
     * @return string
     * @throws Exception
     */
    public String getLaunchUrl(PortletPreferences preference, ActionRequest request) throws Exception {
        String launchUrl;

        launchUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getScheme().equals("http")&&request.getServerPort()!=80) {
            launchUrl+=":" + request.getServerPort();
        }
        launchUrl+= "/uPortal/render.userLayoutRootNode.uP?uP_fname=";
        launchUrl +=preference.getValue("fname","blackboardvc-portlet");

        return launchUrl;
    }
}
