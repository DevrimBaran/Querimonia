package de.fraunhofer.iao.querimonia.complaint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iao.querimonia.utility.XmlMapAdapter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for emotions and subjects of complaints. It contains a value for the
 * property and a map that maps possible values to their probability.
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
public class ComplaintProperty implements Comparable<ComplaintProperty> {

  @JsonIgnore
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  /**
   * The value of the property.
   */
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
  @MapKeyColumn(name = "value")
  @Column(name = "probability")
  @NonNull
  private Map<String, Double> probabilities = new HashMap<>();

  private boolean setByUser = false;

  @SuppressWarnings("unused")
  private ComplaintProperty() {
    // for hibernate
  }

  /**
   * Creates a new complaint property with all possible parameters.
   *
   * @param name          the name of the property.
   * @param value         the value of the property.
   * @param probabilities the probability map for the property.
   * @param isSetByUser   the flag if the property is set by the user.
   */
  public ComplaintProperty(
      @NonNull String name,
      @NonNull String value,
      @NonNull Map<String, Double> probabilities,
      boolean isSetByUser
  ) {
    this.value = value;
    this.name = name;
    this.probabilities = probabilities;
    this.setByUser = isSetByUser;
  }

  /**
   * Creates new complaint property from a probability map. The element with the greatest
   * probability is set as value. Set by user is set to false.
   *
   * @param probabilities the probability map, that maps each value to its probability.
   */
  public ComplaintProperty(@NonNull String name, @NonNull Map<String, Double> probabilities) {
    this.probabilities = probabilities;
    this.value = ComplaintUtility.getEntryWithHighestProbability(probabilities)
        .orElse("Unbekannt");
    this.setByUser = false;
    this.name = name;
  }

  /**
   * Creates a new complaint property with the given name and the given value. The set by user
   * flag is set to true. The probability map only has this one value.
   *
   * @param name  the name of the property.
   * @param value the value of the property.
   */
  public ComplaintProperty(@NonNull String name, @NonNull String value) {
    this.probabilities = new HashMap<>(Collections.singletonMap(value, 1.0));
    this.value = value;
    this.name = name;
    this.setByUser = true;
  }

  /**
   * The default property that contains no information.
   */
  public static ComplaintProperty getDefaultProperty(String name) {
    return new ComplaintProperty(name, "Unbekannt", new HashMap<>(
        Collections.singletonMap("Unbekannt", 1.0)), false);
  }

  public long getId() {
    return id;
  }

  @NonNull
  public String getValue() {
    return value;
  }

  public boolean isSetByUser() {
    return setByUser;
  }

  @NonNull
  @XmlJavaTypeAdapter(XmlMapAdapter.class)
  public Map<String, Double> getProbabilities() {
    return probabilities;
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
        .append(setByUser, that.setByUser)
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
        .append(setByUser)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("id", id)
        .append("value", value)
        .append("name", name)
        .append("probabilities", probabilities)
        .append("setByUser", setByUser)
        .toString();
  }

  @Override
  public int compareTo(@NotNull ComplaintProperty o) {
    return this.getName().compareTo(o.getName());
  }
}
