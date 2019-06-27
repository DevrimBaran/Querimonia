package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.generation.CompletedResponseComponent;

import java.util.List;

/**
 * Rule for disjunction of rules. Is respected, when all children are respected.
 */
public class OrRule implements Rule {

  private List<Rule> children;

  public OrRule(List<Rule> children) {
    this.children = children;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return children.stream()
        .anyMatch(rule -> rule.isRespected(complaint, currentResponseState));
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    return children.stream()
        .anyMatch(rule -> rule.isPotentiallyRespected(complaint));
  }
}
