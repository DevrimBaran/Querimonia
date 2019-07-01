package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;

import java.util.List;

public interface Rule {

  boolean isRespected(ComplaintData complaint,
                      List<CompletedResponseComponent> currentResponseState);

  boolean isPotentiallyRespected(ComplaintData complaint);

  /**
   * Rule that always is respected.
   */
  Rule TRUE = new Rule() {
    @Override
    public boolean isRespected(ComplaintData complaint,
                               List<CompletedResponseComponent> currentResponseState) {
      return true;
    }

    @Override
    public boolean isPotentiallyRespected(ComplaintData complaint) {
      return true;
    }
  };
}
