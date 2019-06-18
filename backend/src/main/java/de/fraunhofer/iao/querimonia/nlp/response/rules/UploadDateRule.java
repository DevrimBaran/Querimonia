package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * A rule that checks for the upload date.
 */
public class UploadDateRule implements Rule {

  private LocalTime uploadTime;
  private LocalDate uploadDate;

  public UploadDateRule(LocalTime uploadTime, LocalDate uploadDate) {
    this.uploadTime = uploadTime;
    this.uploadDate = uploadDate;
  }

  @Override
  public boolean isRespected(Complaint complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(Complaint complaint) {
    return complaint.getReceiveDate().equals(uploadDate)
        && complaint.getReceiveTime().equals(uploadTime);
  }
}
