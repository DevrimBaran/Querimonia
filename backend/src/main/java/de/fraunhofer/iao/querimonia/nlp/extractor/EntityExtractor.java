package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.util.List;

/**
 * This interface is used to extract entities from a given complaint text.
 */
public interface EntityExtractor {

  /**
   * This methods extracts all the entities from a complaint text.
   *
   * @param text the text which entities should be extracted.
   * @return a list of named entities that occur in the text.
   */
  List<NamedEntity> extractEntities(String text);
}
