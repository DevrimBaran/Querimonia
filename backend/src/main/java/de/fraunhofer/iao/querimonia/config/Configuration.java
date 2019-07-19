package de.fraunhofer.iao.querimonia.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierType;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.NonNull;
import tec.uom.lib.common.function.Identifiable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A configuration defines which tools are used by querimonia to analyze complaint texts. This
 * consists of entity extractors, classifiers and sentiment analyzers (but not response
 * generators).
 */
@Entity
@JsonPropertyOrder(value = {
    "id",
    "name",
    "extractors",
    "classifiers",
    "sentimentAnalyzer"
})
public class Configuration implements Identifiable<Long> {

  /**
   * This configuration is used as fallback, when the database does not contain any configurations.
   * This contains to extractors, no classifier and no sentiment analyzer.
   */
  @Transient
  @JsonIgnore
  public static final Configuration FALLBACK_CONFIGURATION = new ConfigurationBuilder()
      .setName("Default")
      .setExtractors(Collections.emptyList())
      .setClassifiers(List.of(
          new ClassifierDefinition(ClassifierType.NONE, "Default", "Kategorie")))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, "Default"))
      .createConfiguration();

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private long configId;

  @Column(name = "config_name", nullable = false)
  @NonNull
  private String name = "";

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "config_id")
  @NonNull
  private List<ExtractorDefinition> extractors = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "config_id")
  @NonNull
  private List<ClassifierDefinition> classifiers = new ArrayList<>();

  @NonNull
  private SentimentAnalyzerDefinition sentimentAnalyzer = new SentimentAnalyzerDefinition();

  /**
   * Creates a new configuration object.
   *
   * @param name              a identifier for the configuration.
   * @param extractors        the list of extractors.
   * @param classifiers       the classifiers of this configuration.
   * @param sentimentAnalyzer the sentiment analyzer.
   */
  @JsonCreator
  public Configuration(
      long id,
      @NonNull String name,
      @NonNull List<ExtractorDefinition> extractors,
      @NonNull List<ClassifierDefinition> classifiers,
      @NonNull SentimentAnalyzerDefinition sentimentAnalyzer) {
    this.configId = id;
    this.name = name;
    this.extractors = extractors;
    this.classifiers = classifiers;
    this.sentimentAnalyzer = sentimentAnalyzer;
  }

  @SuppressWarnings("unused")
  private Configuration() {
    // for hibernate
  }

  public Configuration withConfigId(long configId) {
    return new ConfigurationBuilder(this)
        .setId(configId)
        .createConfiguration();
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public List<ExtractorDefinition> getExtractors() {
    return extractors;
  }

  @NonNull
  public List<ClassifierDefinition> getClassifiers() {
    return classifiers;
  }

  @NonNull
  public SentimentAnalyzerDefinition getSentimentAnalyzer() {
    return sentimentAnalyzer;
  }

  @Override
  @JsonProperty("id")
  public Long getId() {
    return configId;
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
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(name)
        .append(extractors)
        .append(classifiers)
        .append(sentimentAnalyzer)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("configId", configId)
        .append("name", name)
        .append("extractors", extractors)
        .append("classifier", classifiers)
        .append("sentimentAnalyzer", sentimentAnalyzer)
        .toString();
  }
}
