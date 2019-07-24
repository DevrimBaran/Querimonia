package de.fraunhofer.iao.querimonia.config;

import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * This is builder class for {@link Configuration configurations}.
 */
public class ConfigurationBuilder {
  @Nullable
  private String name;
  @Nullable
  private List<ExtractorDefinition> extractors;
  @Nullable
  private List<ClassifierDefinition> classifiers;
  @Nullable
  private SentimentAnalyzerDefinition sentimentAnalyzer;
  @Nullable
  private EmotionAnalyzerDefinition emotionAnalyzer;
  private long id;
  private boolean active;

  public ConfigurationBuilder() {
    // default constructor
  }

  public ConfigurationBuilder(Configuration configuration) {
    this.name = configuration.getName();
    this.extractors = configuration.getExtractors();
    this.classifiers = configuration.getClassifiers();
    this.sentimentAnalyzer = configuration.getSentimentAnalyzer();
    this.id = configuration.getId();
    this.active = configuration.isActive();
    this.emotionAnalyzer = configuration.getEmotionAnalyzer();
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

  public ConfigurationBuilder setEmotionAnalyzer(
      @Nullable EmotionAnalyzerDefinition emotionAnalyzer) {
    this.emotionAnalyzer = emotionAnalyzer;
    return this;
  }

  public ConfigurationBuilder setActive(boolean active) {
    this.active = active;
    return this;
  }

  public Configuration createConfiguration() {
    return new Configuration(id, Objects.requireNonNull(name), Objects.requireNonNull(extractors),
        Objects.requireNonNull(classifiers), Objects.requireNonNull(sentimentAnalyzer),
        Objects.requireNonNull(emotionAnalyzer), active);
  }

  public ConfigurationBuilder setId(long id) {
    this.id = id;
    return this;
  }
}