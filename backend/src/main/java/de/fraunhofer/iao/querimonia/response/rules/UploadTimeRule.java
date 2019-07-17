package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.springframework.lang.Nullable;

import java.time.LocalTime;
import java.util.List;

/**
 * A rule that checks if the upload time is in a certain range.
 * 
 */
public class UploadTimeRule implements Rule {

  private final LocalTime uploadTimeMin;
  private final LocalTime uploadTimeMax;

  public UploadTimeRule(@Nullable LocalTime uploadTimeMin, @Nullable LocalTime uploadTimeMax) {
    this.uploadTimeMin = uploadTimeMin != null ? uploadTimeMin : LocalTime.MIN;
    this.uploadTimeMax = uploadTimeMax != null ? uploadTimeMax : LocalTime.MAX;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    LocalTime uploadTime = complaint.getReceiveTime();
    return !uploadTime.isBefore(uploadTimeMin) && !uploadTime.isAfter(uploadTimeMax);
  }
}
