package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.springframework.lang.Nullable;

import java.util.List;

public class EntityRule implements Rule {

  private final String entityLabel;
  // if this is null, only check if entity is available
  @Nullable
  private final String expectedRegex;

  public EntityRule(String entityLabel, @Nullable String expectedRegex) {
    this.entityLabel = entityLabel;
    this.expectedRegex = expectedRegex;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    // check for upload date and time
    if (entityLabel.equals("Eingangsdatum")) {
      return expectedRegex == null
          || complaint.getReceiveDate().toString().equals(expectedRegex);
    }
    if (entityLabel.equals("Eingangszeit")) {
      return expectedRegex == null
          || complaint.getReceiveTime().toString().equals(expectedRegex);
    }

    if (complaint.getEntities()
            .stream()
            .noneMatch(namedEntity -> namedEntity.getLabel().equals(entityLabel))) {
      return false;
    }
    return expectedRegex == null
        || complaint.getEntities().stream()
        // find matching entities
        .filter(namedEntity -> namedEntity.getLabel().equals(entityLabel))
        // map to entity values
        .map(NamedEntity::getValue)
        .anyMatch(value -> value.matches(expectedRegex));
  }
}
