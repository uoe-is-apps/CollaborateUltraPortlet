package uk.ac.ed.collaborate.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.collaborate.data.Context;
import uk.ac.ed.collaborate.data.Recording;
import uk.ac.ed.collaborate.data.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v1mburg3 on 07/06/2016.
 */
@Service
public class RecordingService {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private CollaborateUltraService collaborateUltraService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ContextService contextService;

    public List<Recording> getRecordingsForSession(String sessionId, String internalUserId) {
        logger.debug("Finding recordings for session with Id: " + sessionId);

        Session session = sessionService.getSession(sessionId);
        boolean isModerator=false;
        if (sessionService.userIsSessionModeratorPresenter(sessionId, internalUserId,"moderator")||sessionService.userIsSessionModeratorPresenter(sessionId, internalUserId,"presenter"))
        {
            isModerator =true;
        }


        Context context = contextService.getContextByName(session.getCreatorId());
        if (context==null)
        {
            // If no context found, no point asking for recordings.
            return new ArrayList<Recording>();
        }

        List<Recording> foundRecordings = collaborateUltraService.getRecordingsByContext(context.getId());
        logger.debug("Found " + foundRecordings.size() + " recordings to search");

        List<Recording> sessionRecordings = new ArrayList<>();
        for (Recording recording : foundRecordings) {
            if (isRecordingOfSession(recording, session)) {
                recording.setPlayUrl(collaborateUltraService.getRecordingPlayUrl());
                if (recording.isCanDownload()) {
                    recording.setDownloadUrl(collaborateUltraService.getRecordingDownloadUrl(recording.getId()));
                }
                recording.setCurrentUserCanDelete(isModerator);

                sessionRecordings.add(recording);
            }
        }

        logger.debug("Found " + sessionRecordings.size() + " recordings for the session");
        return sessionRecordings;
    }

    public void deleteRecordings(String[] recordingIds) {
        for (String recordingId : recordingIds) {
            collaborateUltraService.deleteRecording(recordingId);
        }
    }

    private static boolean isRecordingOfSession(Recording recording, Session session) {
        return recording.getSessionName().equals(session.getName())
                && recording.getSessionStartTime().equals(session.getStartTime());
    }
}
