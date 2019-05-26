package de.fraunhofer.iao.querimonia.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class represents a template for a response.
 */
@Entity
public class ResponseTemplate {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id; // Unique id for the template
  @Column(length = 1024)
  private String templateText; // The actual text of the template
  private String subject; // The subject/category of the template
  private String responsePart; // The role/position of this template in a response

  /**
   * Basic constructor for JSON deserialization.
   */
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

  public int getId() {
    return id;
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
