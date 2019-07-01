package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.util.List;

@Embeddable
public class ResponseSuggestion {

  @ElementCollection
  @CollectionTable(name = "completed_response_components",
                   joinColumns = @JoinColumn(name = "component_id"))
  private List<CompletedResponseComponent> responseComponents;

  @JsonCreator
  public ResponseSuggestion(@JsonProperty List<CompletedResponseComponent> responseComponents) {
    this.responseComponents = responseComponents;
  }

  @SuppressWarnings("unused")
  public ResponseSuggestion() {
  }

  public List<CompletedResponseComponent> getResponseComponents() {
    return responseComponents;
  }

}
