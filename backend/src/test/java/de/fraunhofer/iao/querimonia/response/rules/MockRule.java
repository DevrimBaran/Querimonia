package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;

import java.util.List;

/**
 * A simple mock rule that can be used for testing.
 *
 * @author Simon Weiler
 */
public class MockRule implements Rule {

  /**
   * The boolean returned by the isRespected() method
   */
  private boolean isRespectedValue;

  /**
   * The boolean returned by the isPotentiallyRespected() method
   */
  private boolean isPotentiallyRespectedValue;

  public MockRule(boolean isRespectedValue, boolean isPotentiallyRespectedValue) {
    this.isRespectedValue = isRespectedValue;
    this.isPotentiallyRespectedValue = isPotentiallyRespectedValue;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint, List<CompletedResponseComponent> currentResponseState) {
    return isRespectedValue;
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    return isPotentiallyRespectedValue;
  }
}
