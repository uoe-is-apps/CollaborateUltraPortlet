package uk.ac.ed.collaborate.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.ac.ed.collaborate.data.Enrollment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v1mburg3 on 02/06/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollmentsResponse {
    private List<Enrollment> results = new ArrayList<>();

    public List<Enrollment> getResults() {
        return results;
    }

    public void setResults(List<Enrollment> results) {
        this.results = results;
    }
}
