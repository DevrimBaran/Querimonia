package de.fraunhofer.iao.querimonia.nlp.extractor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.NonNull;

import javax.persistence.*;

/**
 * This class is used to define the extractor which should be used in the a configuration. Each
 * extractor can extract one kind of named entity.
 */
@Entity
public class ExtractorDefinition {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private int id;

  @NonNull
  private String name = "";

  @Enumerated(EnumType.STRING)
  @NonNull
  private ExtractorType type = ExtractorType.NONE;

  @NonNull
  @Column(length = 100)
  private String color = "#ffffff";

  @NonNull
  private String label = "Name";

  @SuppressWarnings("unused")
  private ExtractorDefinition() {
    // for hibernate
  }

  /**
   * Creates new extractor definition, also used for JSON creation.
   *
   * @param name  the name of the extractor.
   * @param type  the type of the extractor.
   * @param color the color of the entities.
   * @param label the label of the entities.
   */
  @JsonCreator
  @SuppressWarnings("unused")
  public ExtractorDefinition(
      @NonNull
      @JsonProperty("name")
          String name,
      @NonNull
      @JsonProperty("type")
          ExtractorType type,
      @NonNull
      @JsonProperty("color")
          String color,
      @NonNull
      @JsonProperty("label")
          String label
  ) {
    this.name = name;
    this.type = type;
    this.color = color;
    this.label = label;
  }

  public int getId() {
    return id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public ExtractorType getType() {
    return type;
  }

  @NonNull
  public String getColor() {
    return color;
  }

  @NonNull
  public String getLabel() {
    return label;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ExtractorDefinition that = (ExtractorDefinition) o;

    return new EqualsBuilder()
        .append(name, that.name)
        .append(type, that.type)
        .append(color, that.color)
        .append(label, that.label)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(name)
        .append(type)
        .append(color)
        .append(label)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("id", id)
        .append("name", name)
        .append("type", type)
        .append("label", label)
        .append("color", color)
        .toString();
  }

}
