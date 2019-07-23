package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * A rule that checks for the upload date.
 */
public class UploadDateRule implements Rule {

  private final LocalDate uploadDateMin;
  private final LocalDate uploadDateMax;

  public UploadDateRule(@Nullable LocalDate uploadDateMin, @Nullable LocalDate uploadDateMax) {
    this.uploadDateMin = uploadDateMin != null ? uploadDateMin : LocalDate.MIN;
    this.uploadDateMax = uploadDateMax != null ? uploadDateMax : LocalDate.MAX;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    LocalDate uploadDate = complaint.getReceiveDate();
    return !uploadDate.isBefore(uploadDateMin) && !uploadDate.isAfter(uploadDateMax);
  }
}
