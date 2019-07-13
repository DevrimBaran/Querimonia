package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.response.action.Action;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class ResponseSuggestion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private long id;

  @OneToMany(cascade = CascadeType.ALL)
  private List<CompletedResponseComponent> responseComponents;

  @OneToMany(cascade = CascadeType.ALL)
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

  public long getId() {
    return id;
  }
}
