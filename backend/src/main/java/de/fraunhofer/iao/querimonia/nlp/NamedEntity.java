package de.fraunhofer.iao.querimonia.nlp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.config.Configuration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * This is a simple POJO that represents a named entity in a text. A named entity is a part of a
 * text that belongs to a certain category, for example organisation or location. This class
 * contains the indices of the named entity in the text, the text of it and their label.<p>
 * An instance can be created using a {@link NamedEntityBuilder}</p>
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@JsonPropertyOrder(value = {
    "id", "label", "start", "end", "value", "setByUser", "preferred", "extractor", "color"
})
public class NamedEntity implements Comparable<NamedEntity> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @NonNull
  @Column(nullable = false)
  @XmlPath("@type")
  private String label = "";

  @XmlPath("position/@startIndex")
  private int start;
  @XmlPath("position/@endIndex")
  private int end;

  @NonNull
  @Column(nullable = false)
  @XmlPath("originalValue/text()")
  private String value = "";

  @XmlPath("results/result[0][@type='setByUser']/text()")
  private boolean setByUser = false;

  @JsonProperty("preferred")
  @XmlPath("results/result[1][@type='preferred']/text()")
  private boolean preferred = false;

  @NonNull
  @Column(nullable = false)
  private String extractor = "";

  @NotNull
  @Column
  @JsonProperty("color")
  @XmlPath("results/result[2][@type='color']/text()")
  private String color = "#22222";

  /**
   * JSON constructor.
   */
  @JsonCreator
  @SuppressWarnings("unused")
  NamedEntity(
      @NonNull
      @JsonProperty(value = "label", required = true)
          String label,
      @JsonProperty(value = "start", required = true)
          int start,
      @JsonProperty(value = "end", required = true)
          int end,
      @JsonProperty(value = "setByUser", required = true)
          boolean setByUser,
      @JsonProperty(value = "preferred", defaultValue = "false")
          boolean preferred,
      @NonNull
      @JsonProperty(value = "extractor", required = true)
          String extractor,
      @NonNull
      @JsonProperty(value = "value")
          String value,
      @JsonProperty(value = "color")
      @NonNull
          String color,
      @JsonProperty("id")
          long id
  ) {
    this.label = label;
    this.start = start;
    this.end = end;
    this.value = value;
    this.setByUser = setByUser;
    this.extractor = extractor;
    this.preferred = preferred;
    this.color = color;
  }

  // constructor for builder.
  NamedEntity(long id, @NonNull String label, @NonNull String value, int start, int end,
                     boolean setByUser, boolean preferred, @NonNull String extractor,
                     @NotNull String color) {
    this.id = id;
    this.label = label;
    this.value = value;
    this.start = start;
    this.end = end;
    this.setByUser = setByUser;
    this.preferred = preferred;
    this.extractor = extractor;
    this.color = color;
  }

  @SuppressWarnings("unused")
  private NamedEntity() {
    // constructor for hibernate
  }

  /**
   * Returns the label of the named entity. The label defines the category of the named entity,
   * like "Name".
   *
   * @return the label of the named entity.
   */
  @NonNull
  public String getLabel() {
    return label;
  }

  /**
   * Returns the start index of the named entity. This is the index in the original text where
   * the named entity starts.
   *
   * @return the start index of the named entity.
   */
  @JsonProperty("start")
  public int getStartIndex() {
    return start;
  }

  /**
   * Returns the end index of the named entity. This is the index in the original text where
   * the named entity ends.
   *
   * @return the end index of the named entity.
   */
  @JsonProperty("end")
  public int getEndIndex() {
    return end;
  }

  /**
   * Returns true if the entity was set by the user and false if the entity was found using an
   * entity extractor.
   *
   * @return true if the entity was set by the user, else false.
   */
  @JsonProperty("setByUser")
  public boolean isSetByUser() {
    return setByUser;
  }

  /**
   * Returns the name of the extractor that was used to find this entity. The extractor name is
   * one of the {@link Configuration#getExtractors() extractors} of the configuration of a
   * complaint.
   *
   * @return the name of the extractor that was used to find this entity.
   */
  @NonNull
  public String getExtractor() {
    return extractor;
  }

  /**
   * Returns the unique id of a named entity.
   *
   * @return the unique id of a named entity.
   */
  public long getId() {
    return id;
  }

  /**
   * Returns the value of the named entity. The value is the actual text of the entity that
   * starts with the start index and ends with the end index in the original complaint text.
   *
   * @return the value of the named entity.
   */
  @NonNull
  public String getValue() {
    return value;
  }

  /**
   * Returns the preferred flag of the entity. This flag is manually set and defines, if the
   * entity should be preferred in the response generation, when there are multiple entities of
   * the same type are available.
   *
   * @return the preferred flag of the entity.
   */
  public boolean isPreferred() {
    return preferred;
  }

  /**
   * Returns the color of the named entity. This color should be used to highlight the entity in
   * the text.
   *
   * @return the color of the named entity.
   */
  @NotNull
  public String getColor() {
    return color;
  }

  public NamedEntity withId(long id) {
    return new NamedEntityBuilder(this).setId(id)
        .createNamedEntity();
  }

  public NamedEntity withLabel(@NonNull String label) {
    return new NamedEntityBuilder(this).setLabel(label)
        .createNamedEntity();
  }

  public NamedEntity withStart(int start) {
    return new NamedEntityBuilder(this).setStart(start)
        .createNamedEntity();
  }

  public NamedEntity withEnd(int end) {
    return new NamedEntityBuilder(this).setEnd(end)
        .createNamedEntity();
  }

  public NamedEntity withValue(@NonNull String value) {
    return new NamedEntityBuilder(this).setValue(value)
        .createNamedEntity();
  }

  public NamedEntity withSetByUser(boolean setByUser) {
    return new NamedEntityBuilder(this).setSetByUser(setByUser)
        .createNamedEntity();
  }

  public NamedEntity withPreferred(boolean preferred) {
    return new NamedEntityBuilder(this).setPreferred(preferred)
        .createNamedEntity();
  }

  public NamedEntity withExtractor(@NonNull String extractor) {
    return new NamedEntityBuilder(this).setExtractor(extractor)
        .createNamedEntity();
  }

  public NamedEntity withColor(@NotNull String color) {
    return new NamedEntityBuilder(this).setColor(color)
        .createNamedEntity();
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
        .append(preferred, that.preferred)
        .append(color, that.color)
        .append(setByUser, that.setByUser)
        .append(extractor, that.extractor)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 59)
        .append(label)
        .append(start)
        .append(end)
        .append(value)
        .append(extractor)
        .append(color)
        .append(setByUser)
        .append(preferred)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("label", label)
        .append("start", start)
        .append("end", end)
        .append("value", value)
        .append("setByUser", setByUser)
        .append("preferred", preferred)
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
