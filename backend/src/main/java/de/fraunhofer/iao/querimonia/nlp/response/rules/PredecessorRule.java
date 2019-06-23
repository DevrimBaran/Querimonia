package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.nlp.response.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.nlp.response.SingleCompletedComponent;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseComponent;

import java.util.List;

/**
 * This rule checks if a template was already used in the response generation or previously used.
 */
public class PredecessorRule implements Rule {

  private String position = "before";
  private String predecessorName;

  /**
   * Creates new predecessor rule.
   *
   * @param predecessorName the template name.
   * @param position the position of the predecessor. Must be "any", "before" or a positive number.
   */
  public PredecessorRule(String predecessorName, String position) {
    this.position = position;
    this.predecessorName = predecessorName;
  }

  public PredecessorRule(String predecessorName) {
    this.predecessorName = predecessorName;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    // position does not matter
    if (position.equals("any")) {
      return currentResponseState.stream()
          // position does not matter
          .map(CompletedResponseComponent::getResponseComponents)
          .map(list -> list.get(0))
          .map(SingleCompletedComponent::getComponent)
          .map(ResponseComponent::getComponentName)
          .anyMatch(predecessorName::equals);

    } else if (currentResponseState.isEmpty()) {
      return false;
    } else {
      // look for specific index or last element
      int index = position.equals("before")
          ? currentResponseState.size() - 1
          : Integer.parseInt(position);
      if (index >= currentResponseState.size() || index < 0) {
        // out of bounds
        return false;
      }
      return currentResponseState.get(index)
          .getResponseComponents()
          .get(0)
          .getComponent()
          .getComponentName()
          .equals(predecessorName);
    }
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    // no assumption can be made
    return true;
  }
}
