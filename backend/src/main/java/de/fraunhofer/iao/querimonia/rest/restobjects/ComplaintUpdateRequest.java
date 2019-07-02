package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Rest object for updating complaints.
 */
public class ComplaintUpdateRequest {

  @Nullable
  private final String newSentiment;
  @Nullable
  private final String newSubject;
  @Nullable
  private final ComplaintState newState;

  @JsonCreator
  public ComplaintUpdateRequest(@JsonProperty("sentiment") String newSentiment,
                                @JsonProperty("subject") String newSubject,
                                @JsonProperty("state") ComplaintState newState) {
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

  public Optional<ComplaintState> getNewState() {
    return Optional.ofNullable(newState);
  }
}
