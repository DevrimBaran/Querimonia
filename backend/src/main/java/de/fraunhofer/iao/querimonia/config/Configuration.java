package de.fraunhofer.iao.querimonia.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierType;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Collections;
import java.util.List;

/**
 * A configuration defines which tools are used by querimonia to analyze complaint texts. This
 * consists of entity extractors, classifiers and sentiment analyzers (but not response
 * generators).
 */
@Entity
@JsonPropertyOrder(value = {
    "configIg",
    "name",
    "extractors",
    "classifier",
    "sentimentAnalyzer"
})
public class Configuration {

  /**
   * This configuration is used as fallback, when the database does not contain any configurations.
   * This contains to extractors, no classifier and no sentiment analyzer.
   */
  @Transient
  @JsonIgnore
  public static final Configuration FALLBACK_CONFIGURATION = new Configuration()
      .setConfigId(0)
      .setName("Default")
      .setExtractors(Collections.emptyList())
      .setClassifier(new ClassifierDefinition(ClassifierType.NONE, "Default"))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, "Default"));

  @Id
  @GeneratedValue
  private int configId;

  @Column(name = "config_name", unique = true)
  private String name;

  @OneToMany(cascade = CascadeType.ALL)
  private List<ExtractorDefinition> extractors;

  private ClassifierDefinition classifier;

  private SentimentAnalyzerDefinition sentimentAnalyzer;

  /**
   * Creates a new configuration object.
   *
   * @param name              a identifier for the configuration.
   * @param extractors        the list of extractors.
   * @param classifier        the classifier.
   * @param sentimentAnalyzer the sentiment analyzer.
   */
  @JsonCreator
  public Configuration(String name,
                       List<ExtractorDefinition> extractors,
                       ClassifierDefinition classifier,
                       SentimentAnalyzerDefinition sentimentAnalyzer) {
    this.name = name;
    this.extractors = extractors;
    this.classifier = classifier;
    this.sentimentAnalyzer = sentimentAnalyzer;
  }

  private Configuration() {
    // for hibernate
  }

  public int getConfigId() {
    return configId;
  }

  public Configuration setConfigId(int configId) {
    this.configId = configId;
    return this;
  }

  public String getName() {
    return name;
  }

  public Configuration setName(String name) {
    this.name = name;
    return this;
  }

  public List<ExtractorDefinition> getExtractors() {
    return extractors;
  }

  public Configuration setExtractors(
      List<ExtractorDefinition> extractors) {
    this.extractors = extractors;
    return this;
  }

  public ClassifierDefinition getClassifier() {
    return classifier;
  }

  public Configuration setClassifier(
      ClassifierDefinition classifier) {
    this.classifier = classifier;
    return this;
  }

  public SentimentAnalyzerDefinition getSentimentAnalyzer() {
    return sentimentAnalyzer;
  }

  public Configuration setSentimentAnalyzer(
      SentimentAnalyzerDefinition sentimentAnalyzer) {
    this.sentimentAnalyzer = sentimentAnalyzer;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Configuration that = (Configuration) o;

    return new EqualsBuilder()
        .append(name, that.name)
        .append(extractors, that.extractors)
        .append(classifier, that.classifier)
        .append(sentimentAnalyzer, that.sentimentAnalyzer)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(name)
        .append(extractors)
        .append(classifier)
        .append(sentimentAnalyzer)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("configId", configId)
        .append("name", name)
        .append("extractors", extractors)
        .append("classifier", classifier)
        .append("sentimentAnalyzer", sentimentAnalyzer)
        .toString();
  }
}
