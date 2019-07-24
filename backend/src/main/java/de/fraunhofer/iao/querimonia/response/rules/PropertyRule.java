package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;

import java.util.List;

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
    return
        complaint.getProperties() != null
            && complaint.getProperties()
            .stream()
            .filter(complaintProperty -> complaintProperty.getName().equals(name))
            .map(ComplaintProperty::getValue)
            .allMatch(value -> value.matches(propertyRegex));
  }
}