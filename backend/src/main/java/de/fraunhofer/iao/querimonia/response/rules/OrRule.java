package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.List;

/**
 * Rule for disjunction of rules. Is respected, when all children are respected.
 */
public class OrRule implements Rule {

  private final List<Rule> children;

  public OrRule(List<Rule> children) {
    this.children = children;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return children.stream()
        .anyMatch(rule -> rule.isRespected(complaint, currentResponseState));
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    return children.stream()
        .anyMatch(rule -> rule.isPotentiallyRespected(complaint));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrRule orRule = (OrRule) o;

    return new EqualsBuilder()
        .append(children, orRule.children)
        .isEquals();
  }
}
