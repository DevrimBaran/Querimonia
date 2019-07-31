package de.fraunhofer.iao.querimonia.nlp.extractor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Embeddable;

@SuppressWarnings("unused")
@Embeddable
class ColorDefinition {

  @JsonProperty
  private String label;
  @JsonProperty
  private String color;

  @SuppressWarnings("unused")
  private ColorDefinition() {
  }

  @JsonCreator
  @SuppressWarnings("unused")
  public ColorDefinition(@JsonProperty("label") String label,
                         @JsonProperty("color") String color) {
    this.label = label;
    this.color = color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ColorDefinition that = (ColorDefinition) o;

    return new EqualsBuilder()
        .append(label, that.label)
        .append(color, that.color)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(label)
        .append(color)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("label", label)
        .append("color", color)
        .toString();
  }
}
