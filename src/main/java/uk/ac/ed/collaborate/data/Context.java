package uk.ac.ed.collaborate.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by rgood on 30/06/2016.
 */
@Entity
@Table(name="VC_ULTRA_CONTEXTS")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Context {

    @Id
    @Column(name="CONTEXT_ID")
    String id;

    @Column(name="CONTEXT_NAME")
    String name;

    @Column(name="CONTEXT_LABEL")
    String label;

    @Column(name="CONTEXT_TITLE")
    String title;

    @Column(name="CONTEXT_EXTID")
    String extId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }
}
