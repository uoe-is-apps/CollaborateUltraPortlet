package uk.ac.ed.collaborate.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.ac.ed.collaborate.data.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionsResponse {
    private List<Session> results = new ArrayList<>();

    public List<Session> getResults() {
        return results;
    }

    public void setResults(List<Session> results) {
        this.results = results;
    }
}