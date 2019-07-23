package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.response.action.Action;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ResponseSuggestion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private long id;

  @OneToMany(cascade = CascadeType.ALL)
  @Column(nullable = false)
  @NonNull
  private List<CompletedResponseComponent> responseComponents = new ArrayList<>();

  @NonNull
  @Column(name = "action_id", nullable = false)
  @OneToMany(cascade = CascadeType.ALL)
  private List<Action> actions = new ArrayList<>();

  @JsonCreator
  public ResponseSuggestion(
      @JsonProperty("components") @NonNull List<CompletedResponseComponent> responseComponents,
      @JsonProperty("actions") @NonNull List<Action> actions) {
    this.actions = actions;
    this.responseComponents = responseComponents;
  }

  @SuppressWarnings("unused")
  public ResponseSuggestion() {
  }

  @NonNull
  public List<CompletedResponseComponent> getResponseComponents() {
    return responseComponents;
  }

  @NonNull
  public List<Action> getActions() {
    return actions;
  }

  public long getId() {
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
