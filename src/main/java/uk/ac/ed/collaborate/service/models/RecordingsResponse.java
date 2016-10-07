package uk.ac.ed.collaborate.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.ac.ed.collaborate.data.Recording;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v1mburg3 on 03/06/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordingsResponse {
    private List<Recording> results = new ArrayList<>();

    public List<Recording> getResults() {
        return results;
    }

    public void setResults(List<Recording> results) {
        this.results = results;
    }
}
