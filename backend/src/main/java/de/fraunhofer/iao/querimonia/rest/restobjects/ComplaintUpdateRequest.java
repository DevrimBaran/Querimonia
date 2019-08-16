package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Rest object that represents a request for updating complaints. It consists of optional
 * attributes that the complaint should have.
 */
public class ComplaintUpdateRequest {

  @Nullable
  private final String newEmotion;
  @Nullable
  private final Double newTendency;
  @Nullable
  private final String newSubject;
  @Nullable
  private final ComplaintState newState;

  /**
   * Creates a new complaint update request.
   *
   * @param newEmotion  the emotion that the complaint should have.
   * @param newTendency the tendency that the complaint should have.
   * @param newState    the new state that the complaint should have.
   * @param newSubject  the subject that the complaint should have.
   */
  @JsonCreator
  public ComplaintUpdateRequest(@Nullable
                                @JsonProperty("sentiment")
                                    String newEmotion,
                                @Nullable
                                @JsonProperty("tendency")
                                    Double newTendency,
                                @Nullable
                                @JsonProperty("subject")
                                    String newSubject,
                                @Nullable
                                @JsonProperty("state")
                                    ComplaintState newState) {
    this.newEmotion = newEmotion;
    this.newTendency = newTendency;
    this.newSubject = newSubject;
    this.newState = newState;
  }

  public Optional<String> getNewEmotion() {
    return Optional.ofNullable(newEmotion);
  }

  public Optional<Double> getNewTendency() {
    return Optional.ofNullable(newTendency);
  }

  public Optional<String> getNewSubject() {
    return Optional.ofNullable(newSubject);
  }

  public Optional<ComplaintState> getNewState() {
    return Optional.ofNullable(newState);
  }
}
