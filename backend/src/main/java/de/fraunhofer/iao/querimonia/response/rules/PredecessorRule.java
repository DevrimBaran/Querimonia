package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;

import java.util.List;

/**
 * This rule checks if a component was already used in the response generation or previously used.
 */
public class PredecessorRule implements Rule {

  private String position = "last";
  private String predecessorRegex;

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

  public PredecessorRule(String predecessorRegex) {
    this.predecessorRegex = predecessorRegex;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    // position does not matter
    if (position.equals("any")) {
      return currentResponseState.stream()
          // position does not matter
          .map(CompletedResponseComponent::getComponent)
          .map(ResponseComponent::getComponentName)
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
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    // no assumption can be made
    return true;
  }
}
