package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.nlp.response.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseComponent;

import java.util.List;

/**
 * This rule checks if a template was already used in the response generation or previously used.
 */
public class PredecessorRule implements Rule {

  private String position = "before";
  private String predecessorRegex;

  /**
   * Creates new predecessor rule.
   *
   * @param predecessorRegex the template name.
   * @param position the position of the predecessor. Must be "any", "before" or a positive number.
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
      int index = position.equals("before")
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
