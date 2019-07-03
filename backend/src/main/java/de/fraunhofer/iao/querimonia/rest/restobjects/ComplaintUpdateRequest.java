package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.Nullable;

import java.util.Optional;

/**
 * Rest object for updating complaints.
 */
public class ComplaintUpdateRequest {

  @Nullable
  private String newSentiment;
  @Nullable
  private String newSubject;
  @Nullable
  private String newState;

  @JsonCreator
  public ComplaintUpdateRequest(@JsonProperty("sentiment") String newSentiment,
                                @JsonProperty("subject") String newSubject,
                                @JsonProperty("state") String newState) {
    this.newSentiment = newSentiment;
    this.newSubject = newSubject;
    this.newState = newState;
  }

  public Optional<String> getNewSentiment() {
    return Optional.ofNullable(newSentiment);
  }

  public Optional<String> getNewSubject() {
    return Optional.ofNullable(newSubject);
  }

  public Optional<String> getNewState() {
    return Optional.ofNullable(newState);
  }
}
