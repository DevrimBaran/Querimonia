package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.PersistentResponseComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.List;

/**
 * This rule checks if a component was already used in the response generation or previously used.
 */
public class PredecessorRule implements Rule {

  private final String position;
  private final String predecessorRegex;

  /**
   * Creates new predecessor rule.
   *
   * @param predecessorRegex the component name.
   * @param position the position of the predecessor. Must be "any", "last" or a positive number.
   */
  public PredecessorRule(String predecessorRegex, String position) {
    this.position = position;
    this.predecessorRegex = predecessorRegex;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    // position does not matter
    if (position.equals("any")) {
      return currentResponseState.stream()
          // position does not matter
          .map(CompletedResponseComponent::getComponent)
          .map(PersistentResponseComponent::getComponentName)
          .anyMatch(name -> name.matches(predecessorRegex));

    } else if (currentResponseState.isEmpty()) {
      return false;
    } else {
      // look for specific index or last element
      int index = position.equals("last")
          ? currentResponseState.size() - 1
          : Integer.parseInt(position);
      if (index >= currentResponseState.size() || index < 0) {
        // out of bounds
        return false;
      }
      return currentResponseState.get(index)
          .getComponent()
          .getComponentName()
          .matches(predecessorRegex);
    }
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    // no assumption can be made
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PredecessorRule that = (PredecessorRule) o;

    return new EqualsBuilder()
        .append(position, that.position)
        .append(predecessorRegex, that.predecessorRegex)
        .isEquals();
  }
}
