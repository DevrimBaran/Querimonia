package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * This rule checks if a certain entity is available and its content matches a certain regex.
 */
public class EntityRule implements Rule {

  private final String entityLabel;
  // if this is null, only check if entity is available
  @Nullable
  private final String expectedRegex;
  private final int countMin;
  private final int countMax;

  /**
   * Creates a new entity rule.
   *
   * @param entityLabel   the label that the entity should have.
   * @param expectedRegex the regex that the value of the entity should match.
   * @param countMin      the minimal count of entities that occurred in the text with the given
   *                      label.
   * @param countMax      the maximal count of entities that occurred in the text with the given
   *                      label.
   */
  public EntityRule(String entityLabel, @Nullable String expectedRegex, int countMin,
                    int countMax) {
    this.entityLabel = entityLabel;
    this.expectedRegex = expectedRegex;
    this.countMin = countMin;
    this.countMax = countMax;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    // check for upload date and time
    if (entityLabel.equals("Eingangsdatum")) {
      if (countMin > 1 || countMax < 1) {
        return false;
      }
      return expectedRegex == null
          || complaint.getReceiveDate().toString().matches(expectedRegex);
    }
    if (entityLabel.equals("Eingangszeit")) {
      if (countMin > 1 || countMax < 1) {
        return false;
      }
      return expectedRegex == null
          || complaint.getReceiveTime().toString().matches(expectedRegex);
    }

    var matchingEntities = complaint
        .getEntities()
        .stream()
        .filter(namedEntity -> namedEntity.getLabel().equals(entityLabel))
        .filter(namedEntity -> expectedRegex == null || namedEntity.getValue()
            .matches(expectedRegex))
        .count();
    return countMin <= matchingEntities && matchingEntities <= countMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    EntityRule that = (EntityRule) o;

    return new EqualsBuilder()
        .append(entityLabel, that.entityLabel)
        .append(expectedRegex, that.expectedRegex)
        .append(countMax, that.countMax)
        .append(countMin, that.countMin)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(entityLabel)
        .append(expectedRegex)
        .append(countMin)
        .append(countMax)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("entityLabel", entityLabel)
        .append("expectedRegex", expectedRegex)
        .append("countMin", countMin)
        .append("countMax", countMax)
        .toString();
  }
}
