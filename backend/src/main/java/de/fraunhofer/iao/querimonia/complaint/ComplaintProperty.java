package de.fraunhofer.iao.querimonia.complaint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

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

  private String value = "";

  private String name = "";

  /**
   * This map contains the possible values of the property mapped to their probabilities.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "probability_table", joinColumns = @JoinColumn(name = "id"))
  @MapKeyColumn(name = "probabilities")
  @Column(name = "probability")
  private Map<String, Double> probabilities = new HashMap<>();

  private boolean isSetByUser = false;

  @SuppressWarnings("unused")
  public ComplaintProperty() {
    // for hibernate
  }

  /**
   * Creates new complaint property from a probability map. The element with the greatest
   * probability is set as value. Set by user is set to false.
   *
   * @param probabilities the probability map, that maps each value to its probability.
   */
  public ComplaintProperty(Map<String, Double> probabilities, String name) {
    this.probabilities = probabilities;
    this.value = ComplaintUtility.getEntryWithHighestProbability(probabilities)
        .orElse("");
    this.isSetByUser = false;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public Map<String, Double> getProbabilities() {
    return probabilities;
  }

  public boolean isSetByUser() {
    return isSetByUser;
  }

  public ComplaintProperty setValue(String value) {
    this.value = value;
    isSetByUser = true;
    return this;
  }

  /**
   * Updates the probability map to a new map. If not setByUser and keepUserInformation, the
   * value gets also updated.
   *
   * @param valueProbabilities  the map that maps values to their probabilities.
   * @param keepUserInformation if true, the value attribute does not get overwritten.
   */
  public void updateValueProbabilities(
      Map<String, Double> valueProbabilities, boolean keepUserInformation) {
    this.probabilities = valueProbabilities;
    if (!keepUserInformation || !isSetByUser) {
      this.value = ComplaintUtility.getEntryWithHighestProbability(valueProbabilities)
          .orElse("");
      this.isSetByUser = false;
    }
  }

  public ComplaintProperty setSetByUser(boolean setByUser) {
    isSetByUser = setByUser;
    return this;
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
