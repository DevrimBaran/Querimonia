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
  private final String newEmotion;
  @Nullable
  private final String newSubject;
  @Nullable
  private final ComplaintState newState;

  @JsonCreator
  public ComplaintUpdateRequest(@Nullable @JsonProperty("sentiment") String newEmotion,
                                @Nullable @JsonProperty("subject") String newSubject,
                                @Nullable @JsonProperty("state") ComplaintState newState) {
    this.newEmotion = newEmotion;
    this.newSubject = newSubject;
    this.newState = newState;
  }

  public Optional<String> getNewEmotion() {
    return Optional.ofNullable(newEmotion);
  }

  public Optional<String> getNewSubject() {
    return Optional.ofNullable(newSubject);
  }

  public Optional<ComplaintState> getNewState() {
    return Optional.ofNullable(newState);
  }
}
