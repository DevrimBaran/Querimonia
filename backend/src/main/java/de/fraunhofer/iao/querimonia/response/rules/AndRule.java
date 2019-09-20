package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.List;

/**
 * This rule consists of a variable number of children rules. The rule is respected, if all the
 * children rules are respected.
 */
public class AndRule implements Rule {

  private final List<Rule> children;

  public AndRule(List<Rule> children) {
    this.children = children;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return children.stream()
        .allMatch(rule -> rule.isRespected(complaint, currentResponseState));
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    return children.stream()
        .allMatch(rule -> rule.isPotentiallyRespected(complaint));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AndRule andRule = (AndRule) o;

    return new EqualsBuilder()
        .append(children, andRule.children)
        .isEquals();
  }
}
