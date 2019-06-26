package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.nlp.response.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;

import java.util.List;

/**
 * This rule consists of a variable number of children rules. The rule is respected, if all the
 * children rules are respected.
 */
public class AndRule implements Rule {

  private List<Rule> children;

  public AndRule(List<Rule> children) {
    this.children = children;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return children.stream()
        .allMatch(rule -> rule.isRespected(complaint, currentResponseState));
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    return children.stream()
        .allMatch(rule -> rule.isPotentiallyRespected(complaint));
  }
}
