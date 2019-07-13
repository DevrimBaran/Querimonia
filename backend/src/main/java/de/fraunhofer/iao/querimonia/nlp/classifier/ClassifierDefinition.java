package de.fraunhofer.iao.querimonia.nlp.classifier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Definition of a classifier for a configuration.
 */
@Entity
public class ClassifierDefinition {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private long id;

  @Enumerated(EnumType.STRING)
  @JsonProperty("type")
  private ClassifierType classifierType;

  @Column(name = "classifier_name")
  private String name;

  private String categoryName;

  @SuppressWarnings("unused")
  private ClassifierDefinition() {
    // for hibernate
  }

  public ClassifierDefinition(ClassifierType classifierType, String name, String categoryName) {
    this.classifierType = classifierType;
    this.name = name;
    this.categoryName = categoryName;
  }

  public ClassifierType getClassifierType() {
    return classifierType;
  }

  public String getName() {
    return name;
  }


  public String getCategoryName() {
    return categoryName;
  }

  public long getId() {
    return id;
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
        .append(categoryName, that.categoryName)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(classifierType)
        .append(name)
        .append(categoryName)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("classifierType", classifierType)
        .append("name", name)
        .append("categoryName", categoryName)
        .toString();
  }
}
