package de.fraunhofer.iao.querimonia.rest.restobjects;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Simple POJO for returning combinations.
 */
public class Combination {

  private List<NamedEntity> entities;

  public Combination(List<NamedEntity> entities) {
    this.entities = entities;
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Combination that = (Combination) o;

    return new EqualsBuilder()
        .append(entities, that.entities)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(entities)
        .toHashCode();
  }
}
