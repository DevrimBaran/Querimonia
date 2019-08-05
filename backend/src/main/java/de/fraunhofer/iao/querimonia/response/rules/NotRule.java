package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.List;

/**
 * Negation rule, is respected when child is not respected.
 */
public class NotRule implements Rule {

  private final Rule child;

  public NotRule(Rule child) {
    this.child = child;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return !child.isRespected(complaint, currentResponseState);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    // no assumption can be made.
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NotRule notRule = (NotRule) o;

    return new EqualsBuilder()
        .append(child, notRule.child)
        .isEquals();
  }
}
