package de.fraunhofer.iao.querimonia.config;

import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * This is builder class for {@link Configuration configurations}. It is used to create
 * configuration instances.
 *
 * <p>Usage:</p>
 * <pre>{@code Configuration config = new ConfigurationBuilder()
 *        .setName("example")
 *        .setClassifiers(exampleClassifiers)
 *        ...
 *        .createConfiguration();}</pre>
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

  /**
   * Creates a new empty configuration builder.
   */
  public ConfigurationBuilder() {
    // default constructor
  }

  /**
   * Creates a new configuration builder with the attributes of a configuration.
   *
   * @param configuration the builder will have the save attribute values as this configuration.
   */
  public ConfigurationBuilder(Configuration configuration) {
    this.name = configuration.getName();
    this.extractors = configuration.getExtractors();
    this.classifiers = configuration.getClassifiers();
    this.sentimentAnalyzer = configuration.getSentimentAnalyzer();
    this.id = configuration.getId();
    this.active = configuration.isActive();
    this.emotionAnalyzer = configuration.getEmotionAnalyzer();
  }

  /**
   * Sets the {@link Configuration#getName() name} of the configuration.
   *
   * @param name the name of the configuration.
   *
   * @return this configuration builder.
   */
  public ConfigurationBuilder setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the {@link Configuration#getExtractors() extractors} of the configuration.
   *
   * @param extractors a list of {@link ExtractorDefinition extractor definitions} that defines
   *                   which extractors should be used in the analysis.
   *
   * @return this configuration builder.
   */
  public ConfigurationBuilder setExtractors(List<ExtractorDefinition> extractors) {
    this.extractors = extractors;
    return this;
  }

  /**
   * Sets the {@link Configuration#getClassifiers() classifiers} of the configuration.
   *
   * @param classifiers a list of {@link ClassifierDefinition classifier definitions} that defines
   *                    which classifiers should be used in the analysis.
   *
   * @return this configuration builder.
   */
  public ConfigurationBuilder setClassifiers(@NonNull List<ClassifierDefinition> classifiers) {
    this.classifiers = classifiers;
    return this;
  }

  /**
   * Sets the {@link Configuration#getSentimentAnalyzer() sentiment analyzer} of the configuration.
   *
   * @param sentimentAnalyzer the {@link SentimentAnalyzerDefinition definition} of the sentiment
   *                          analyzer, that should be used for the analysis.
   *
   * @return this configuration builder.
   */
  public ConfigurationBuilder setSentimentAnalyzer(
      @NonNull SentimentAnalyzerDefinition sentimentAnalyzer) {
    this.sentimentAnalyzer = sentimentAnalyzer;
    return this;
  }

  /**
   * Sets the {@link Configuration#getEmotionAnalyzer() emtion analyzer} of the configuration.
   *
   * @param emotionAnalyzer the {@link EmotionAnalyzerDefinition definition} of the emotion
   *                        analyzer, that should be used for the analysis.
   *
   * @return this configuration builder.
   */
  public ConfigurationBuilder setEmotionAnalyzer(
      @NonNull EmotionAnalyzerDefinition emotionAnalyzer) {
    this.emotionAnalyzer = emotionAnalyzer;
    return this;
  }

  /**
   * Sets the active state of the configuration. There can only be one active configuration, that
   * is used for the analysis by default.
   *
   * @param active if the configuration should be active or not.
   *
   * @return this configuration builder.
   */
  public ConfigurationBuilder setActive(boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Creates a configuration with the attributes of this builder.
   *
   * @return a new configuration with the attributes of this builder.
   */
  public Configuration createConfiguration() {
    return new Configuration(id, Objects.requireNonNull(name), Objects.requireNonNull(extractors),
        Objects.requireNonNull(classifiers), Objects.requireNonNull(sentimentAnalyzer),
        Objects.requireNonNull(emotionAnalyzer), active);
  }

  /**
   * Sets the id of the configuration. <b>Use with caution!</b> When adding new configurations to
   * the database, the id will be generated automatically.
   *
   * @param id the unique id of the configuration.
   *
   * @return this configuration builder.
   */
  ConfigurationBuilder setId(long id) {
    this.id = id;
    return this;
  }
}