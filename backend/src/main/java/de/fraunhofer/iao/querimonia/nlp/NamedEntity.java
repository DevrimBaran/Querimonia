package de.fraunhofer.iao.querimonia.nlp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a simple POJO that represents a named entity in a text. It has a label (for example
 * "Date", "Org")
 * and the indices where the entity starts and ends in the text.
 */
public class NamedEntity {

  private String label;
  private int start;
  private int end;

  /**
   * Simple constructor for creating a new named entity object.
   *
   * @param label the label of the entity, like name.
   * @param start the start index of the entity.
   * @param end   the end index of the entity.
   */
  @JsonCreator
  public NamedEntity(@JsonProperty String label, @JsonProperty("start") int start, @JsonProperty(
      "end") int end) {
    this.label = label;
    this.start = start;
    this.end = end;
  }

  public String getLabel() {
    return label;
  }

  @JsonProperty("start")
  public int getStartIndex() {
    return start;
  }

  @JsonProperty("end")
  public int getEndIndex() {
    return end;
  }
}
