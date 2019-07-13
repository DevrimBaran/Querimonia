package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;

import java.util.List;

/**
 * A simple rule that checks if the subject of a complaint matches.
 */
public class SubjectRule implements Rule {

  private final String subject;

  public SubjectRule(String subject) {
    this.subject = subject;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    // check if the subject of the complaint matches
    return complaint
        .getSubject()
        .getValue()
        .equals("subject");
  }
}
