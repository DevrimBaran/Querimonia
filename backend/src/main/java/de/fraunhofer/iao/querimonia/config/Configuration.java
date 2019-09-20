package de.fraunhofer.iao.querimonia.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierType;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerType;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;
import tec.uom.lib.common.function.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A configuration defines which tools are used by querimonia to analyze complaint texts. This
 * consists of entity extractors, classifiers and sentiment analyzers (but not response
 * generators).
 * <p>Instanced can be created using a {@link ConfigurationBuilder}</p>
 */
@Entity
@JsonPropertyOrder(value = {
    "id",
    "name",
    "extractors",
    "classifiers",
    "sentimentAnalyzer",
    "emotionAnalyzer"
})
public class Configuration implements Identifiable<Long> {

  /**
   * This configuration is used as fallback, when the database does not contain any configurations.
   * This contains to extractors, no classifier and no sentiment analyzer.
   */
  @Transient
  @JsonIgnore
  public static final Configuration FALLBACK_CONFIGURATION = new ConfigurationBuilder()
      .setName("Leere Konfiguration")
      .setExtractors(new ArrayList<>())
      .setClassifiers(new ArrayList<>(List.of(
          new ClassifierDefinition(ClassifierType.NONE, "Default", "Kategorie"))))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, "Default"))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, "Default"))
      .setActive(false)
      .createConfiguration();

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private long configId;

  @Column(name = "config_name")
  @NonNull
  private String name = "";

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "config_id")
  @NonNull
  private List<ExtractorDefinition> extractors = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @Fetch(FetchMode.SUBSELECT)
  @JoinColumn(name = "config_id")
  @NonNull
  private List<ClassifierDefinition> classifiers = new ArrayList<>();

  @NonNull
  private SentimentAnalyzerDefinition sentimentAnalyzer = new SentimentAnalyzerDefinition();

  @NonNull
  private EmotionAnalyzerDefinition emotionAnalyzer = new EmotionAnalyzerDefinition();

  private boolean active = false;

  /**
   * Json constructor.
   */
  @JsonCreator
  @SuppressWarnings("unused")
  Configuration(

      @JsonProperty("name")
      @NonNull
          String name,

      @JsonProperty(value = "extractors", required = true)
      @NonNull
          List<ExtractorDefinition> extractors,

      @JsonProperty(value = "classifiers", required = true)
      @NonNull
          List<ClassifierDefinition> classifiers,

      @JsonProperty(value = "sentimentAnalyzer", required = true)
      @NonNull
          SentimentAnalyzerDefinition sentimentAnalyzer,

      @JsonProperty(value = "emotionAnalyzer", required = true)
      @NonNull
          EmotionAnalyzerDefinition emotionAnalyzer,

      @JsonProperty("active")
          boolean active,

      @JsonProperty("id")
          long id
  ) {

    this.name = name;
    this.extractors = extractors;
    this.classifiers = classifiers;
    this.sentimentAnalyzer = sentimentAnalyzer;
    this.emotionAnalyzer = emotionAnalyzer;
    this.active = false;
    this.configId = 0;
  }

  /**
   * Constructor for builder.
   */
  Configuration(long id,
                @NonNull String name,
                @NonNull List<ExtractorDefinition> extractors,
                @NonNull List<ClassifierDefinition> classifiers,
                @NonNull SentimentAnalyzerDefinition sentimentAnalyzer,
                @NonNull EmotionAnalyzerDefinition emotionAnalyzer,
                boolean active) {
    this.configId = id;
    this.name = name;
    this.extractors = extractors;
    this.classifiers = classifiers;
    this.sentimentAnalyzer = sentimentAnalyzer;
    this.emotionAnalyzer = emotionAnalyzer;
    this.active = active;
  }

  @SuppressWarnings("unused")
  private Configuration() {
    // for hibernate
  }

  /**
   * Returns the unique id of the configuration.
   *
   * @return the unique id of the configuration.
   */
  @Override
  @JsonProperty("id")
  public Long getId() {
    return configId;
  }

  /**
   * Returns a copy of this configuration with the id set to the given id.
   *
   * @param configId the id that the configuration should have.
   *
   * @return a copy of this configuration with the id set to the given id.
   */
  public Configuration withConfigId(long configId) {
    return new ConfigurationBuilder(this)
        .setId(configId)
        .createConfiguration();
  }

  /**
   * Returns the name of the configuration. The name is an attribute that is chosen by the user
   * to identify configurations.
   *
   * @return the name of the configuration.
   */
  @NonNull
  public String getName() {
    return name;
  }

  /**
   * Returns the list of {@link ExtractorDefinition extractors} that are used in this
   * configuration. All extractors that are defined in this list will be used in the analysis
   * when this configuration is selected.
   *
   * @return the list of {@link ExtractorDefinition extractors} that are used in this configuration.
   */
  @NonNull
  public List<ExtractorDefinition> getExtractors() {
    return extractors;
  }

  /**
   * Returns the list of {@link ClassifierDefinition classifiers} that are used in this
   * configuration. All classifiers. that are defined in this list will be used in the analysis
   * when this configuration is selected.
   *
   * @return the list of {@link ClassifierDefinition classifiers} that are used in this
   *     configuration.
   */
  @NonNull
  public List<ClassifierDefinition> getClassifiers() {
    return classifiers;
  }

  /**
   * Returns the {@link SentimentAnalyzerDefinition sentiment analyzer} that is defined in this
   * configuration.
   *
   * @return the {@link SentimentAnalyzerDefinition sentiment analyzer} that is defined in this
   *     configuration.
   */
  @NonNull
  public SentimentAnalyzerDefinition getSentimentAnalyzer() {
    return sentimentAnalyzer;
  }

  /**
   * Returns the {@link EmotionAnalyzerDefinition emotion analyzer} that is defined in this
   * configuration.
   *
   * @return the {@link EmotionAnalyzerDefinition emotion analyzer} that is defined in this
   *     configuration.
   */
  @NonNull
  public EmotionAnalyzerDefinition getEmotionAnalyzer() {
    return emotionAnalyzer;
  }

  /**
   * Returns true, if this is the currently active configuration. There should always be only one
   * active configuration. The active configuration is used in the analysis process by default.
   *
   * @return true, if this is the currently active configuration, else false.
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Returns a copy of this configuration with the given active state. There should always be
   * only one active configuration.
   *
   * @param active if the copy should be the active configuration or not.
   *
   * @return a copy of this configuration with the given active state.
   */
  public Configuration withActive(boolean active) {
    return new ConfigurationBuilder(this)
        .setActive(active)
        .createConfiguration();
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
        .append(classifiers, that.classifiers)
        .append(sentimentAnalyzer, that.sentimentAnalyzer)
        .append(emotionAnalyzer, that.emotionAnalyzer)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(name)
        .append(extractors)
        .append(classifiers)
        .append(sentimentAnalyzer)
        .append(emotionAnalyzer)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("configId", configId)
        .append("name", name)
        .append("extractors", extractors)
        .append("classifiers", classifiers)
        .append("sentimentAnalyzer", sentimentAnalyzer)
        .append("emotionAnalyzer", emotionAnalyzer)
        .toString();
  }
}
