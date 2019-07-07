package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintUtility;
import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

public class EntityRule implements Rule {

  private final String entityLabel;
  // if this is null, only check if entity is available
  @Nullable
  private String expectedValue = null;

  public EntityRule(String entityLabel, @Nullable String expectedValue) {
    this.entityLabel = entityLabel;
    this.expectedValue = expectedValue;
  }

  public EntityRule(String entityLabel) {
    this.entityLabel = entityLabel;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    // check for upload date and time
    if (entityLabel.equals("UploadDatum")) {
      return Objects.equals(complaint.getUploadTime().toLocalDate().toString(), expectedValue);
    }
    if (entityLabel.equals("UploadZeit")) {
      return Objects.equals(complaint.getUploadTime().toLocalTime().toString(), expectedValue);
    }

    return ComplaintUtility.getValueOfEntity(complaint.getText(),
                                             complaint.getEntities(),
                                             entityLabel)
        .map(entityValue -> expectedValue == null || entityValue.equals(expectedValue))
        // if not present, the entity was not given.
        .orElse(false);
  }
}
