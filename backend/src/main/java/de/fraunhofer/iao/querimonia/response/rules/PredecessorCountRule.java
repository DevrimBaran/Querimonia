package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;

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
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return currentResponseState.size() <= max && currentResponseState.size() >= min;
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    return true;
  }
}