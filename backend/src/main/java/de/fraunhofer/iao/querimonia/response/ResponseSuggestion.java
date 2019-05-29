package de.fraunhofer.iao.querimonia.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.fraunhofer.iao.querimonia.db.Complaint;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@JsonPropertyOrder({
    "responseId",
    "responseComponents"
})
public class ResponseSuggestion {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int responseId;

  @OneToOne
  @JsonIgnore
  @JoinColumn(name = "complaint_id")
  private Complaint complaint;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<CompletedResponseComponent> responseComponents;

  @JsonCreator
  public ResponseSuggestion(@JsonProperty Complaint complaint,
                            @JsonProperty List<CompletedResponseComponent> responseComponents) {
    this.complaint = complaint;
    this.responseComponents = responseComponents;
  }

  public int getResponseId() {
    return responseId;
  }

  public Complaint getComplaint() {
    return complaint;
  }

  public List<CompletedResponseComponent> getResponseComponents() {
    return responseComponents;
  }

  public ResponseSuggestion() {
  }

}
