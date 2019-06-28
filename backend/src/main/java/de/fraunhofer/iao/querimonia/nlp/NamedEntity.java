package de.fraunhofer.iao.querimonia.nlp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Embeddable;

/**
 * This is a simple POJO that represents a named entity in a text. It has a label (for example
 * "Date", "Org") and the indices where the entity starts and ends in the text.
 */
@Embeddable
public class NamedEntity implements Comparable<NamedEntity> {

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

  public NamedEntity() {

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

  @Override
  public String toString() {
    return "NamedEntity{"
        + "label='" + label + '\''
        + ", start=" + start
        + ", end=" + end
        + '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NamedEntity) {
      NamedEntity other = (NamedEntity) obj;
      return other.start == start && other.end == end && other.label.equals(label);
    }
    return false;
  }

  @Override
  public int compareTo(@NotNull NamedEntity o) {
    if (start < o.start) {
      return -1;
    }
    if (end < o.end) {
      return -1;
    }
    return label.compareTo(o.label);
  }
}
