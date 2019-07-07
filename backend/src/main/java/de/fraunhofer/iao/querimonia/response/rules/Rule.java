package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;

import java.util.List;

/**
 * Rules are used for response components and actions. Rules determine, when a component may be used
 * during the response generation.
 */
public interface Rule {

  /**
   * This methods checks, if this rule is respected.
   *
   * @param complaint            the complaint which should be checked.
   * @param currentResponseState the current state of the response generation. Contains all response
   *                             components that are used until now.
   * @return true, if the complaint respects this rule, else false.
   */
  boolean isRespected(ComplaintData complaint,
                      List<CompletedResponseComponent> currentResponseState);

  /**
   * Checks if a complaint could potentially respect a rule, independent from the state of the
   * response generation.
   *
   * @param complaint the complaint that gets checked.
   * @return true, if the complaint could respect the rule.
   */
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
