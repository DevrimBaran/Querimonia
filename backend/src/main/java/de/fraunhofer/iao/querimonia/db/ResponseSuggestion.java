package de.fraunhofer.iao.querimonia.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ResponseSuggestion {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @ManyToOne
  @JoinColumn
  private Complaint complaint;

  @ManyToOne
  @JoinColumn
  private ResponseTemplate template;

  @Column(length = 4048)
  private String responseText;

  /**
   * Basic constructor for json deserialization.
   */
  @JsonCreator
  public ResponseSuggestion(@JsonProperty Complaint complaint,
                            @JsonProperty ResponseTemplate template,
                            @JsonProperty String responseText) {
    this.complaint = complaint;
    this.template = template;
    this.responseText = responseText;
  }

  public ResponseSuggestion() {
  }

  public int getId() {
    return id;
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
