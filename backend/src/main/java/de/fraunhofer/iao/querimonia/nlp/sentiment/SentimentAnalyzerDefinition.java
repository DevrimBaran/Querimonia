package de.fraunhofer.iao.querimonia.nlp.sentiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * A definition of a sentiment analyzer for a configuration.
 */
@Embeddable
public class SentimentAnalyzerDefinition {

  @Enumerated(EnumType.STRING)
  @Column(name = "sentiment_analyzer_type")
  private SentimentAnalyzerType type;

  @Column(name = "sentiment_analyzer_name")
  private String name;

  @JsonCreator
  public SentimentAnalyzerDefinition(
      @NonNull
      @JsonProperty("type")
          SentimentAnalyzerType type,
      @NonNull
      @JsonProperty("name")
          String name
  ) {
    this.type = type;
    this.name = name;
  }

  @SuppressWarnings("unused")
  public SentimentAnalyzerDefinition() {
    // for hibernate
  }

  public SentimentAnalyzerType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SentimentAnalyzerDefinition that = (SentimentAnalyzerDefinition) o;

    return new EqualsBuilder()
        .append(type, that.type)
        .append(name, that.name)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(type)
        .append(name)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("type", type)
        .append("name", name)
        .toString();
  }
}
