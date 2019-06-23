package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.db.ComplaintUtility;
import de.fraunhofer.iao.querimonia.nlp.response.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;
import org.springframework.lang.Nullable;

import java.util.List;

public class EntityRule implements Rule {

  private String entityLabel;
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
    return ComplaintUtility.getValueOfEntity(complaint.getText(),
                                             complaint.getEntities(),
                                             entityLabel)
        .map(entityValue -> expectedValue == null || entityValue.equals(expectedValue))
        // if not present, the entity was not given.
        .orElse(false);
  }
}
