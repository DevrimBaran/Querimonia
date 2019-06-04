package de.fraunhofer.iao.querimonia.nlp.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Embeddable
public class ResponseSuggestion {

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<CompletedResponseComponent> responseComponents;

  @JsonCreator
  public ResponseSuggestion(@JsonProperty List<CompletedResponseComponent> responseComponents) {
    this.responseComponents = responseComponents;
  }

  public List<CompletedResponseComponent> getResponseComponents() {
    return responseComponents;
  }

  public ResponseSuggestion() {
  }

}
