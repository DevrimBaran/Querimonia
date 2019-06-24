package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.db.Complaints.ComplaintUtility;
import de.fraunhofer.iao.querimonia.nlp.response.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;

import java.util.List;

/**
 * A simple rule that checks if the subject of a complaint matches.
 */
public class SubjectRule implements Rule {

  private String subject;

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
    return ComplaintUtility.getEntryWithHighestProbability(complaint.getSubjectMap())
        .map(subject::equals)
        .orElse(false);
  }
}
