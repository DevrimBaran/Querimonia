package de.fraunhofer.iao.querimonia.nlp.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class represents a template for a part of the response. A response consists of a list of
 * these response components.
 */
@Entity
public class ResponseComponent {

  /**
   * Unique componentId for the template to store in the database.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int componentId;

  /**
   * The template text that may have placeholders.
   */
  @Column(length = 5000)
  private String templateText;

  /**
   * The complaint category to this component fits.
   */
  @Nullable
  private String subject;

  /**
   * A unique identifier that represents where to component should be set in the full response, for
   * example "begin", "introduction", "end"...
   */
  private String responsePart;

  /**
   * This list contains all the possible response parts that may follow up this component. This is
   * used for response generation.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  private List<String> successorParts;

  /**
   * The template text gets sliced into text parts and placeholder parts.
   */
  @JsonIgnore
  @Transient
  private List<ResponseSlice> slices;

  /**
   * Basic constructor for JSON deserialization.
   */
  @JsonCreator
  public ResponseComponent(@JsonProperty("templateText") String templateText,
                           @JsonProperty("subject") String subject,
                           @JsonProperty("responsePart") String responsePart,
                           @JsonProperty("successorParts") ArrayList<String> successorParts) {
    this.templateText = templateText;
    this.subject = subject;
    this.responsePart = responsePart;
    this.successorParts = successorParts;
  }

  @SuppressWarnings("unused")
  public ResponseComponent() {

  }

  /**
   * Slices the template text into text and placeholder parts.
   */
  private void createSlices() {
    slices = new ArrayList<>();
    // the current position in the text
    int templatePosition = 0;

    // check if template text has correct placeholder formatting
    if (!templateText.matches("[^${}]+(\\$\\{\\w*}[^${}]+)*")) {
      throw new IllegalArgumentException("Illegal template format");
    }

    // split on every placeholder
    String[] parts = templateText.split("\\$\\{\\w*}");

    for (String currentPart : parts) {
      templatePosition += currentPart.length();
      // add raw text slice
      slices.add(new ResponseSlice(false, currentPart));

      String remainingText = templateText.substring(templatePosition);
      if (remainingText.length() >= 2) {
        // find closing bracket
        int endPosition = remainingText.indexOf('}');
        // find entity for placeholder
        String entityLabel = remainingText.substring(2, endPosition);
        // add label slice
        slices.add(new ResponseSlice(true, entityLabel));

        templatePosition = templatePosition + 3 + entityLabel.length();

      } else {
        break;
      }
    }
  }

  // getters and setters

  public List<String> getSuccessorParts() {
    return successorParts;
  }

  public ResponseComponent setSuccessorParts(List<String> successorParts) {
    this.successorParts = successorParts;
    return this;
  }

  public int getComponentId() {
    return componentId;
  }

  public String getTemplateText() {
    return templateText;
  }

  /**
   * Sets the template text of the response component.
   */
  public ResponseComponent setTemplateText(String templateText) {
    this.templateText = templateText;
    // update slices
    createSlices();
    return this;
  }

  public Optional<String> getSubject() {
    return Optional.ofNullable(subject);
  }

  public ResponseComponent setSubject(@Nullable String subject) {
    this.subject = subject;
    return this;
  }

  public String getResponsePart() {
    return responsePart;
  }

  public ResponseComponent setResponsePart(String responsePart) {
    this.responsePart = responsePart;
    return this;
  }

  @JsonIgnore
  @Transient
  List<ResponseSlice> getSlices() {
    if (slices == null) {
      createSlices();
    }
    return slices;
  }

  @Transient
  @JsonProperty("requiredEntities")
  public List<String> getRequiredEntities() {
    return getSlices()
        .stream()
        .filter(ResponseSlice::isPlaceholder)
        .map(ResponseSlice::getContent)
        .collect(Collectors.toList());
  }

  /**
   * Represents a slice of the response component that is either raw text or a placeholder. The
   * complaint text gets sliced in text and placeholders.
   */
  static class ResponseSlice {

    private boolean isPlaceholder;
    // this is either the placeholder name or the raw text when the slice is no placeholder.
    private String content;

    ResponseSlice(boolean isPlaceholder, String content) {
      this.isPlaceholder = isPlaceholder;
      this.content = content;
    }

    boolean isPlaceholder() {
      return isPlaceholder;
    }

    String getContent() {
      return content;
    }
  }
}
