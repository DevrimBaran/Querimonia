package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.response.action.Action;

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

  // TODO add join column!
  @ElementCollection
  @CollectionTable(name = "response_actions",
                   joinColumns = @JoinColumn(name = "action_id"))
  private List<Action> actions;

  @JsonCreator
  public ResponseSuggestion(
      @JsonProperty("components") List<CompletedResponseComponent> responseComponents,
      @JsonProperty("actions") List<Action> actions) {
    this.actions = actions;
    this.responseComponents = responseComponents;
  }

  @SuppressWarnings("unused")
  public ResponseSuggestion() {
  }

  public List<CompletedResponseComponent> getResponseComponents() {
    return responseComponents;
  }

  public List<Action> getActions() {
    return actions;
  }
}
