package de.fraunhofer.iao.querimonia.nlp.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Definition of a classifier for a configuration.
 */
@Embeddable
public class ClassifierDefinition {

  @Enumerated(EnumType.STRING)
  @JsonProperty("type")
  private ClassifierType classifierType;

  @Column(name = "classifier_name")
  private String name;

  public ClassifierDefinition() {
    // for hibernate
  }

  public ClassifierDefinition(
      ClassifierType classifierType, String name) {
    this.classifierType = classifierType;
    this.name = name;
  }

  public ClassifierType getClassifierType() {
    return classifierType;
  }

  public String getName() {
    return name;
  }
}
