package de.fraunhofer.iao.querimonia.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ResponseTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;
    private String templateText;
    private String subject;
    private String responsePart;

    @JsonCreator
    public ResponseTemplate(@JsonProperty String templateText,
                            @JsonProperty String subject,
                            @JsonProperty String responsePart) {
        this.templateText = templateText;
        this.subject = subject;
        this.responsePart = responsePart;
    }

    public ResponseTemplate() {

    }

    public int getID() {
        return ID;
    }

    public String getTemplateText() {
        return templateText;
    }

    public String getSubject() {
        return subject;
    }

    public String getResponsePart() {
        return responsePart;
    }
}
