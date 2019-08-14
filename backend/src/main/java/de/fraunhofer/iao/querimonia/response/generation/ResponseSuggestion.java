package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.response.action.Action;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;
import tec.uom.lib.common.function.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a response to a complaint. It contains
 * {@link CompletedResponseComponent components} the are parts of the response text and actions,
 * that get executed on closing the complaints.
 */
@Entity
public class ResponseSuggestion implements Identifiable<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private long id;

  @OneToMany(cascade = CascadeType.ALL)
  @NonNull
  private List<CompletedResponseComponent> responseComponents = new ArrayList<>();

  @NonNull
  @Column(name = "action_id")
  @OneToMany(cascade = CascadeType.ALL)
  private List<Action> actions = new ArrayList<>();

  @JsonCreator
  public ResponseSuggestion(
      @JsonProperty("components")
      @NonNull
          List<CompletedResponseComponent> responseComponents,
      @JsonProperty("actions")
      @NonNull
          List<Action> actions) {
    this.actions = actions;
    this.responseComponents = responseComponents;
  }

  @SuppressWarnings("unused")
  private ResponseSuggestion() {
    // for hibernate
  }

  /**
   * Returns a response that has no actions and no components.
   *
   * @return a response that has no actions and no components.
   */
  public static ResponseSuggestion getEmptyResponse() {
    return new ResponseSuggestion();
  }

  @NonNull
  public List<CompletedResponseComponent> getResponseComponents() {
    return responseComponents;
  }

  @NonNull
  public List<Action> getActions() {
    return actions;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ResponseSuggestion that = (ResponseSuggestion) o;

    return new EqualsBuilder()
        .append(responseComponents, that.responseComponents)
        .append(actions, that.actions)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(responseComponents)
        .append(actions)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("responseComponents", responseComponents)
        .append("actions", actions)
        .toString();
  }
}
