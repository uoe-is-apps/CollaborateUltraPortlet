package uk.ac.ed.collaborate.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.ac.ed.collaborate.data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v1mburg3 on 03/06/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersResponse {
    private List<User> results = new ArrayList<>();

    public List<User> getResults() {
        return results;
    }

    public void setResults(List<User> results) {
        this.results = results;
    }
}