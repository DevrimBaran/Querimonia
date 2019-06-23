package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.nlp.response.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;
import org.springframework.lang.Nullable;

import java.time.LocalTime;
import java.util.List;

/**
 * A rule that checks if the upload time is in a certain range.
 * 
 */
public class UploadTimeRule implements Rule {

  private LocalTime uploadTimeMin;
  private LocalTime uploadTimeMax;

  public UploadTimeRule(@Nullable LocalTime uploadTimeMin, @Nullable LocalTime uploadTimeMax) {
    this.uploadTimeMin = uploadTimeMin != null ? uploadTimeMin : LocalTime.MIN;
    this.uploadTimeMax = uploadTimeMax != null ? uploadTimeMax : LocalTime.MAX;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    LocalTime uploadTime = complaint.getUploadTime().toLocalTime();
    return !uploadTime.isBefore(uploadTimeMin) && !uploadTime.isAfter(uploadTimeMax);
  }
}
