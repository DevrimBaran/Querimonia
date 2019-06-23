package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.nlp.response.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;

import java.util.List;

/**
 * Negation rule, is respected when child is not respected.
 */
public class NotRule implements Rule {

  private Rule child;

  public NotRule(Rule child) {
    this.child = child;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return !child.isRespected(complaint, currentResponseState);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    // no secure assumption can be made.
    return true;
  }
}