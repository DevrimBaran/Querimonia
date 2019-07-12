package de.fraunhofer.iao.querimonia.nlp.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

  @SuppressWarnings("unused")
  private ClassifierDefinition() {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ClassifierDefinition that = (ClassifierDefinition) o;

    return new EqualsBuilder()
        .append(classifierType, that.classifierType)
        .append(name, that.name)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(classifierType)
        .append(name)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("classifierType", classifierType)
        .append("name", name)
        .toString();
  }
}
