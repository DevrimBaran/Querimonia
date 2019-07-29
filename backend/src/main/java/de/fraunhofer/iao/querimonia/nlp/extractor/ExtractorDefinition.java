package de.fraunhofer.iao.querimonia.nlp.extractor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.NonNull;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to define the extractor which should be used in the a configuration.
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

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "color_table", joinColumns = @JoinColumn(name = "id"))
  @Column(name = "color")
  @NonNull
  private List<ColorDefinition> colors = new ArrayList<>();

  @SuppressWarnings("unused")
  public ExtractorDefinition() {
    // for hibernate
  }

  @JsonCreator
  @SuppressWarnings("unused")
  public ExtractorDefinition(@NonNull String name,
                             @NonNull ExtractorType type,
                             @NonNull List<ColorDefinition> colors) {
    this.name = name;
    this.type = type;
    this.colors = colors;
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
  public List<ColorDefinition> getColors() {
    return colors;
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
        .append(colors, that.colors)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(name)
        .append(type)
        .append(colors)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("id", id)
        .append("name", name)
        .append("type", type)
        .append("colors", colors)
        .toString();
  }

  @SuppressWarnings("unused")
  @Embeddable
  private static class ColorDefinition {

    String label;
    String color;

    @SuppressWarnings("unused")
    public ColorDefinition() {
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public ColorDefinition(String label, String color) {
      this.label = label;
      this.color = color;
    }
  }
}
