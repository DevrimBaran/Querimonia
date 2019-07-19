package de.fraunhofer.iao.querimonia.config;

import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

public class ConfigurationBuilder {
  @Nullable private String name;
  @Nullable private List<ExtractorDefinition> extractors;
  @Nullable private List<ClassifierDefinition> classifiers;
  @Nullable private SentimentAnalyzerDefinition sentimentAnalyzer;
  private long id;

  public ConfigurationBuilder() {
    // default constructor
  }

  public ConfigurationBuilder(Configuration configuration) {
    this.name = configuration.getName();
    this.extractors = configuration.getExtractors();
    this.classifiers = configuration.getClassifiers();
    this.sentimentAnalyzer = configuration.getSentimentAnalyzer();
    this.id = configuration.getId();
  }

  public ConfigurationBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public ConfigurationBuilder setExtractors(List<ExtractorDefinition> extractors) {
    this.extractors = extractors;
    return this;
  }

  public ConfigurationBuilder setClassifiers(List<ClassifierDefinition> classifiers) {
    this.classifiers = classifiers;
    return this;
  }

  public ConfigurationBuilder setSentimentAnalyzer(SentimentAnalyzerDefinition sentimentAnalyzer) {
    this.sentimentAnalyzer = sentimentAnalyzer;
    return this;
  }

  public Configuration createConfiguration() {
    return new Configuration(id, Objects.requireNonNull(name), Objects.requireNonNull(extractors),
        Objects.requireNonNull(classifiers), Objects.requireNonNull(sentimentAnalyzer));
  }

  public ConfigurationBuilder setId(long id) {
    this.id = id;
    return this;
  }
}