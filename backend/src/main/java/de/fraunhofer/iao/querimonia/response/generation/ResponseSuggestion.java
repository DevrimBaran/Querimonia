package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  @Column(length = 10000)
  private String response = "";

  @JsonCreator
  public ResponseSuggestion(
      @JsonProperty("components")
      @NonNull
          List<CompletedResponseComponent> responseComponents,
      @JsonProperty("response")
      @NonNull
          String response) {
    this.response = response;
    this.responseComponents = responseComponents;
  }

  private ResponseSuggestion(
      long id,
      @NonNull List<CompletedResponseComponent> responseComponents,
      @NonNull String response) {
    this.response = response;
    this.responseComponents = responseComponents;
    this.id = id;
  }

  @SuppressWarnings("unused")
  public ResponseSuggestion() {
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
  public String getResponse() {
    return response;
  }

  @Override
  public Long getId() {
    return id;
  }

  public ResponseSuggestion withResponseComponents(
      @NonNull List<CompletedResponseComponent> responseComponents) {
    return new ResponseSuggestion(this.id, responseComponents, this.response);
  }

  public ResponseSuggestion withResponse(@NonNull String response) {
    return new ResponseSuggestion(this.id, this.responseComponents, response);
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
        .append(response, that.response)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(responseComponents)
        .append(response)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("responseComponents", responseComponents)
        .append("response", response)
        .toString();
  }
}
