package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A rule that checks if the value of a complaint property matches.
 */
public class PropertyRule implements Rule {

  private final String name;
  private final String propertyRegex;

  public PropertyRule(String name, String propertyRegex) {
    this.name = name;
    this.propertyRegex = propertyRegex;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    // check if the name of the complaint matches
    var propertyValues =
        complaint.getProperties()
            .stream()
            .filter(complaintProperty -> complaintProperty.getName().equals(name))
            .map(ComplaintProperty::getValue)
            .collect(Collectors.toList());
    return !propertyValues.isEmpty() && propertyValues.stream()
        .allMatch(value -> value.matches(propertyRegex));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PropertyRule that = (PropertyRule) o;

    return new EqualsBuilder()
        .append(name, that.name)
        .append(propertyRegex, that.propertyRegex)
        .isEquals();
  }
}
