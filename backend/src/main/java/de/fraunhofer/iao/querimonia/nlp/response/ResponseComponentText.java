package de.fraunhofer.iao.querimonia.nlp.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a template for a part of the response. A response consists of a list of
 * these response components.
 */
@Embeddable
public class ResponseComponentText {

  /**
   * The template text that may have placeholders.
   */
  @Column(length = 5000)
  private String templateText;

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
  public ResponseComponentText(@JsonProperty("templateText") String templateText) {
    this.templateText = templateText;
  }

  @SuppressWarnings("unused")
  public ResponseComponentText() {

  }

  // getters and setters

  public String getTemplateText() {
    return templateText;
  }

  /**
   * Sets the template text of the response component.
   */
  public ResponseComponentText setTemplateText(String templateText) {
    this.templateText = templateText;
    // update slices
    slices = ResponseSlice.createSlices(templateText);
    return this;
  }

  @JsonIgnore
  @Transient
  List<ResponseSlice> getSlices() {
    if (slices == null) {
      slices = ResponseSlice.createSlices(templateText);
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

}
