package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.db.Complaint;
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
  public boolean isRespected(Complaint complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return !child.isRespected(complaint, currentResponseState);
  }

  @Override
  public boolean isPotentiallyRespected(Complaint complaint) {
    // no secure assumption can be made.
    return true;
  }
}
