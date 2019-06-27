package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.generation.CompletedResponseComponent;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * A rule that checks for the upload date.
 */
public class UploadDateRule implements Rule {

  private LocalDate uploadDateMin;
  private LocalDate uploadDateMax;

  public UploadDateRule(@Nullable LocalDate uploadDateMin, @Nullable LocalDate uploadDateMax) {
    this.uploadDateMin = uploadDateMin != null ? uploadDateMin : LocalDate.MIN;
    this.uploadDateMax = uploadDateMax != null ? uploadDateMax : LocalDate.MAX;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    LocalDate uploadDate = complaint.getUploadTime().toLocalDate();
    return !uploadDate.isBefore(uploadDateMin) && !uploadDate.isAfter(uploadDateMax);
  }
}
