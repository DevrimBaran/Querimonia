package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.generation.CompletedResponseComponent;

import java.util.List;

/**
 * Simple rule that checks if a template has a certain amount of predecessors.
 */
public class PredecessorCountRule implements Rule {

  private int min;
  private int max;

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
