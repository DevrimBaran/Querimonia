package de.fraunhofer.iao.querimonia.nlp.sentiment;

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

  public SentimentAnalyzerDefinition(SentimentAnalyzerType type, String name) {
    this.type = type;
    this.name = name;
  }

  public SentimentAnalyzerDefinition() {
    // for hibernate
  }

  public SentimentAnalyzerType getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
