package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.List;

/**
 * Simple rule that checks if a component has a certain amount of predecessors.
 */
public class PredecessorCountRule implements Rule {

  private final int min;
  private final int max;

  public PredecessorCountRule(int min, int max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return currentResponseState.size() <= max && currentResponseState.size() >= min;
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
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

    PredecessorCountRule that = (PredecessorCountRule) o;

    return new EqualsBuilder()
        .append(min, that.min)
        .append(max, that.max)
        .isEquals();
  }
}
