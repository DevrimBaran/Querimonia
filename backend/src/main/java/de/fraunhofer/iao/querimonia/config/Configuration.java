package de.fraunhofer.iao.querimonia.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierType;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerType;

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
public class Configuration {

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

  public Configuration() {
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

}
