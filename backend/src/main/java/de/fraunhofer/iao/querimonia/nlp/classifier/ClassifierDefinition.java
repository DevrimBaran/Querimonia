package de.fraunhofer.iao.querimonia.nlp.classifier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.NonNull;

import javax.persistence.*;

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
  @NonNull
  private ClassifierType classifierType = ClassifierType.NONE;

  @Column(name = "classifier_name")
  @NonNull
  private String name = "";

  @NonNull
  @JsonProperty("propertyName")
  private String categoryName = "Kategorie";

  @SuppressWarnings("unused")
  private ClassifierDefinition() {
    // for hibernate
  }

  @JsonCreator
  public ClassifierDefinition(
      @NonNull
      @JsonProperty("type")
          ClassifierType classifierType,
      @NonNull
      @JsonProperty("name")
          String name,
      @NonNull
      @JsonProperty("propertyName")
          String categoryName) {
    this.classifierType = classifierType;
    this.name = name;
    this.categoryName = categoryName;
  }

  @NonNull
  public ClassifierType getClassifierType() {
    return classifierType;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
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
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("classifierType", classifierType)
        .append("name", name)
        .append("categoryName", categoryName)
        .toString();
  }
}
