package de.fraunhofer.iao.querimonia.complaint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for sentiments and subjects of complaints. It contains a value for the
 * property and a map that maps possible values to their probability.
 */
@Entity
public class ComplaintProperty implements Comparable<ComplaintProperty> {

  @JsonIgnore
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @Column(nullable = false)
  @NonNull
  private String value = "";

  @Column(nullable = false)
  @NonNull
  private String name = "";

  /**
   * This map contains the possible values of the property mapped to their probabilities.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "probability_table", joinColumns = @JoinColumn(name = "id"))
  @MapKeyColumn(name = "probabilities")
  @Column(name = "probability")
  @NonNull
  private Map<String, Double> probabilities = new HashMap<>();

  private boolean isSetByUser = false;

  @SuppressWarnings("unused")
  public ComplaintProperty() {
    // for hibernate
  }

  public ComplaintProperty( @NonNull String name, @NonNull String value,
                           @NonNull Map<String, Double> probabilities, boolean isSetByUser) {
    this.value = value;
    this.name = name;
    this.probabilities = probabilities;
    this.isSetByUser = isSetByUser;
  }

  /**
   * Creates new complaint property from a probability map. The element with the greatest
   * probability is set as value. Set by user is set to false.
   *
   * @param probabilities the probability map, that maps each value to its probability.
   */
  public ComplaintProperty(@NonNull Map<String, Double> probabilities, @NonNull String name) {
    this.probabilities = probabilities;
    this.value = ComplaintUtility.getEntryWithHighestProbability(probabilities)
        .orElse("");
    this.isSetByUser = false;
    this.name = name;
  }

  public ComplaintProperty(@NonNull String name, @NonNull String value) {
    this.probabilities = new HashMap<>();
    this.value = value;
    this.name = name;
    this.isSetByUser = true;
  }

  public long getId() {
    return id;
  }

  @NonNull
  public String getValue() {
    return value;
  }

  @NonNull
  public Map<String, Double> getProbabilities() {
    return probabilities;
  }

  public boolean isSetByUser() {
    return isSetByUser;
  }

  public ComplaintProperty withValue(String value) {
    return new ComplaintProperty(this.name, value, this.probabilities, true);
  }

  @NonNull
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

    ComplaintProperty that = (ComplaintProperty) o;

    return new EqualsBuilder()
        .append(isSetByUser, that.isSetByUser)
        .append(value, that.value)
        .append(name, that.name)
        .append(probabilities, that.probabilities)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(value)
        .append(name)
        .append(probabilities)
        .append(isSetByUser)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("value", value)
        .append("name", name)
        .append("probabilities", probabilities)
        .append("isSetByUser", isSetByUser)
        .toString();
  }

  @Override
  public int compareTo(@NotNull ComplaintProperty o) {
    return this.getValue().compareTo(o.getValue());
  }
}
