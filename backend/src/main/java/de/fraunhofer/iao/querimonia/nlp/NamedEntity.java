package de.fraunhofer.iao.querimonia.nlp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This is a simple POJO that represents a named entity in a text. It has a label (for example
 * "Date", "Org") and the indices where the entity starts and ends in the text.
 */
@Entity
public class NamedEntity implements Comparable<NamedEntity> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @NonNull
  @Column(nullable = false)
  private String label = "";
  @NonNull
  @Column(nullable = false)
  private String value = "";
  private int start;
  private int end;
  private boolean setByUser = false;
  @NonNull
  @Column(nullable = false)
  private String extractor = "";

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
  NamedEntity(@NonNull @JsonProperty String label,
                     @JsonProperty("start") int start,
                     @JsonProperty("end") int end,
                     @JsonProperty boolean setByUser,
                     @NonNull @JsonProperty String extractor,
                     @NonNull @JsonProperty String value) {
    this.label = label;
    this.start = start;
    this.end = end;
    this.value = value;
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
   * @param value     the text of the entity.
   * @param extractor the name of the extractor that was used to find this entity.
   */
  public NamedEntity(String label, int start, int end, String extractor, String value) {
    this(label, start, end, false, extractor, value);
  }

  public NamedEntity() {
    // constructor for hibernate
  }

  @NonNull
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

  @NonNull
  public String getExtractor() {
    return extractor;
  }

  public long getId() {
    return id;
  }

  @NonNull
  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NamedEntity that = (NamedEntity) o;

    return new EqualsBuilder()
        .append(start, that.start)
        .append(end, that.end)
        .append(label, that.label)
        .append(value, that.value)
        .append(extractor, that.extractor)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 37)
        .append(label)
        .append(start)
        .append(end)
        .append(value)
        .append(extractor)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("label", label)
        .append("start", start)
        .append("end", end)
        .append("value", value)
        .append("setByUser", setByUser)
        .append("extractor", extractor)
        .toString();
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
