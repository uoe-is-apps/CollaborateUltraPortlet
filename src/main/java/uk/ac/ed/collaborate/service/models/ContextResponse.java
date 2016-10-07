package uk.ac.ed.collaborate.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.ac.ed.collaborate.data.Context;
import uk.ac.ed.collaborate.data.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgood on 03/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContextResponse {
    private List<Context> results = new ArrayList<>();

    public List<Context> getResults() {
        return results;
    }

    public void setResults(List<Context> results) {
        this.results = results;
    }
}
