package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;

import java.util.List;

/**
 * Rules are used for response components and actions. Rules determine, when a component may be used
 * during the response generation.
 *
 * <p>Rules are structured like logical expressions in a tree structure. There is one
 * <b>root rule</b> which can have child rules. Only rules that combine child rules in a logical
 * operation like disjunction may have child rules. Rules without children are called leaf rules
 * and are elementary logical expressions that check certain attributes of complaints.</p>
 */
public interface Rule {

  /**
   * This methods checks, if this rule is respected.
   *
   * @param complaint            the complaint which should be checked.
   * @param currentResponseState the current state of the response generation. Contains all response
   *                             components that are used until now.
   *
   * @return true, if the complaint respects this rule, else false.
   */
  boolean isRespected(ComplaintBuilder complaint,
                      List<CompletedResponseComponent> currentResponseState);

  /**
   * Checks if a complaint could potentially respect a rule, independent from the state of the
   * response generation.
   *
   * @param complaint the complaint that gets checked.
   *
   * @return true, if the complaint could respect the rule.
   */
  boolean isPotentiallyRespected(ComplaintBuilder complaint);

  /**
   * Rule that always is respected.
   */
  Rule TRUE = new Rule() {
    @Override
    public boolean isRespected(ComplaintBuilder complaint,
                               List<CompletedResponseComponent> currentResponseState) {
      return true;
    }

    @Override
    public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
      return true;
    }
  };
}
