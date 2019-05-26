package de.fraunhofer.iao.querimonia.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
public class ResponseSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;

    @ManyToOne
    @JoinColumn
    private Complaint complaint;

    @ManyToOne
    @JoinColumn
    private ResponseTemplate template;

    @Column(length = 1024)
    private String responseText;

    @JsonCreator
    public ResponseSuggestion(@JsonProperty Complaint complaint, @JsonProperty ResponseTemplate template,
                              @JsonProperty String responseText) {
        this.complaint = complaint;
        this.template = template;
        this.responseText = responseText;
    }

    public ResponseSuggestion() {
    }

    public int getID() {
        return ID;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public ResponseTemplate getTemplate() {
        return template;
    }

    public String getResponseText() {
        return responseText;
    }
}
