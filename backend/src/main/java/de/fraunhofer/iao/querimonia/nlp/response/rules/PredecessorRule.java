package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseComponent;

import java.util.List;

/**
 * This rule checks if a template was already used in the response generation or previously used.
 */
public class PredecessorRule implements Rule {

  private boolean anyPosition = false;
  private String predecessorName;

  public PredecessorRule(String predecessorName, boolean anyPosition) {
    this.anyPosition = anyPosition;
    this.predecessorName = predecessorName;
  }

  public PredecessorRule(String predecessorName) {
    this.predecessorName = predecessorName;
  }

  @Override
  public boolean isRespected(Complaint complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    if (anyPosition) {
      return currentResponseState.stream()
          .map(CompletedResponseComponent::getComponent)
          .map(ResponseComponent::getResponsePart)
          .anyMatch(predecessorName::equals);
    } else if (currentResponseState.isEmpty()) {
      return false;
    } else {
      return currentResponseState.get(currentResponseState.size() - 1)
          .getComponent()
          .getResponsePart()
          .equals(predecessorName);
    }
  }

  @Override
  public boolean isPotentiallyRespected(Complaint complaint) {
    // no assumption can be made
    return true;
  }
}
