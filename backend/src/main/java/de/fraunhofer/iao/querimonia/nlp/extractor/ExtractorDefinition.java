package de.fraunhofer.iao.querimonia.nlp.extractor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import java.util.Map;

/**
 * This class is used to define the extractor which should be used in the a configuration.
 */
@Entity
public class ExtractorDefinition {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private int id;

  private String name;

  @Enumerated(EnumType.STRING)
  private ExtractorType type;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "color_table", joinColumns = @JoinColumn(name = "id"))
  @MapKeyColumn(name = "label")
  @Column(name = "color")
  private Map<String, String> colors;

  @SuppressWarnings("unused")
  public ExtractorDefinition() {
    // for hibernate
  }

  public ExtractorDefinition(String name,
                             ExtractorType type,
                             Map<String, String> colors) {
    this.name = name;
    this.type = type;
    this.colors = colors;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public ExtractorType getType() {
    return type;
  }

  public Map<String, String> getColors() {
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
    return new ToStringBuilder(this)
        .append("id", id)
        .append("name", name)
        .append("type", type)
        .append("colors", colors)
        .toString();
  }
}
