package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.util.List;

/**
 * Mocked version of the {@link KikukoExtractor}.
 * Doesn't contact KiKuKo, instead returns the values defined in it's constructor.
 *
 * @author simon
 */
public class MockExtractor implements EntityExtractor {

  private List<NamedEntity> expectedEntities; // the mocked entities

  protected MockExtractor(List<NamedEntity> expectedEntities) {
    this.expectedEntities = expectedEntities;
  }

  @Override
  public List<NamedEntity> extractEntities(String text) {
    return expectedEntities;
  }
}
