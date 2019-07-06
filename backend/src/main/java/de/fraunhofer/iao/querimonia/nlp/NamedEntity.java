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
  private boolean setByUser = false;
  private String extractor;

  /**
   * Simple constructor for creating a new named entity object.
   *
   * @param label     the label of the entity, like name.
   * @param start     the start index of the entity.
   * @param end       the end index of the entity.
   * @param setByUser if the named entity was set by the user.
   * @param extractor the name of the extractor that was used to find this entity.
   */
  @JsonCreator
  public NamedEntity(@JsonProperty String label, @JsonProperty("start") int start, @JsonProperty(
      "end") int end, @JsonProperty boolean setByUser, @JsonProperty String extractor) {
    this.label = label;
    this.start = start;
    this.end = end;
    this.setByUser = setByUser;
    this.extractor = extractor;
  }

  /**
   * Simple constructor for creating a new named entity object which setByUser property is set to
   * false.
   *
   * @param label     the label of the entity, like name.
   * @param start     the start index of the entity.
   * @param end       the end index of the entity.
   * @param extractor the name of the extractor that was used to find this entity.
   */
  public NamedEntity(String label, int start, int end, String extractor) {
    this(label, start, end, false, extractor);
  }

  public NamedEntity() {
    // constructor for hibernate
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

  @JsonProperty("setByUser")
  public boolean isSetByUser() {
    return setByUser;
  }

  public String getExtractor() {
    return extractor;
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
      return other.start == start
          && other.end == end
          && other.label.equals(label)
          && other.extractor.equals(extractor);
    }
    return false;
  }

  @Override
  public int compareTo(@NotNull NamedEntity o) {
    int compare = Integer.compare(this.start, o.start);
    if (compare == 0) {
      compare = Integer.compare(this.end, o.end);
    }
    if (compare == 0) {
      compare = label.compareTo(o.label);
    }
    return compare;
  }
}
