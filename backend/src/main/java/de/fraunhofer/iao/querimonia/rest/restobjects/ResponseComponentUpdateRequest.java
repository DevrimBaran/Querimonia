package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public class ResponseComponentUpdateRequest {

  @Nullable
  private String subject;
  @Nullable
  private String templateText;
  @Nullable
  private String responsePart;
  @Nullable
  private List<String> successorParts;

  @JsonCreator
  public ResponseComponentUpdateRequest(@Nullable @JsonProperty String subject,
                                        @Nullable @JsonProperty String templateText,
                                        @Nullable @JsonProperty String responsePart,
                                        @Nullable @JsonProperty List<String> successorParts) {
    this.subject = subject;
    this.templateText = templateText;
    this.responsePart = responsePart;
    this.successorParts = successorParts;
  }

  public Optional<String> getSubject() {
    return Optional.ofNullable(subject);
  }

  public Optional<String> getTemplateText() {
    return Optional.ofNullable(templateText);
  }

  public Optional<String> getResponsePart() {
    return Optional.ofNullable(responsePart);
  }

  public Optional<List<String>> getSuccessorParts() {
    return Optional.ofNullable(successorParts);
  }
}
