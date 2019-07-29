package de.fraunhofer.iao.querimonia.nlp.emotion;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Embeddable;

/**
 * Defines a {@link EmotionAnalyzer}.
 */
@Embeddable
public class EmotionAnalyzerDefinition {

  private String name;
  private EmotionAnalyzerType type = EmotionAnalyzerType.NONE;

  @JsonCreator
  public EmotionAnalyzerDefinition(EmotionAnalyzerType type, String name) {
    this.name = name;
    this.type = type;
  }

  public EmotionAnalyzerDefinition() {
    // for hibernate
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    EmotionAnalyzerDefinition that = (EmotionAnalyzerDefinition) o;

    return new EqualsBuilder()
        .append(name, that.name)
        .append(type, that.type)
        .isEquals();
  }

  public String getName() {
    return name;
  }

  public EmotionAnalyzerType getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(name)
        .append(type)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("name", name)
        .append("type", type)
        .toString();
  }
}
