package uk.ac.ed.collaborate.dao;

import uk.ac.ed.collaborate.data.Recording;

import java.util.List;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
public interface RecordingDao {
    Recording getRecording(Long recordingId);

    void saveRecording(Recording recording);

    void deleteRecording(Long recordingId);

    void deleteAllSessionRecordings(Long sessionId);

    List<Recording> getAllRecordings();

    List<Recording> getRecordingsForUser(String uid);

    List<Recording> getAllSessionRecordings(Long sessionId);
}
