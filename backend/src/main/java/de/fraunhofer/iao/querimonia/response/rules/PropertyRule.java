package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
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
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    // check if the name of the complaint matches
    return complaint
        .getProperties()
        .stream()
        .filter(complaintProperty -> complaintProperty.getName().equals(name))
        .map(ComplaintProperty::getValue)
        .allMatch(value -> value.matches(propertyRegex))
        || (name.equals("Emotion") && complaint.getEmotion().getValue().matches(propertyRegex));
  }
}
