package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.util.List;

/**
 * Definition of a {@link MockExtractor}.
 * Additionally to a normal extractor definition, this one contains predefined output values.
 */
public class MockExtractorDefinition extends ExtractorDefinition {

  private List<NamedEntity> expectedEntities;

  public MockExtractorDefinition(
      String name,
      ExtractorType type,
      String color,
      String label,
      List<NamedEntity> expectedEntities) {
    super(name, type, color, label);
    this.expectedEntities = expectedEntities;
  }

  public List<NamedEntity> getExpectedEntities() {
    return expectedEntities;
  }
}
