package de.fraunhofer.iao.querimonia.nlp.extractor;

import java.util.ArrayList;

/**
 * This factory is used to create {@link EntityExtractor EntityExtractors} from
 * {@link ExtractorDefinition their definitions}.
 */
public class ExtractorFactory {

  /**
   * Creates an entity extractor from a definition.
   *
   * @param definition the definition of the extractor.
   * @return an entity extractor as defined in the extractor definition.
   */
  public static EntityExtractor getFromDefinition(ExtractorDefinition definition) {
    switch (definition.getType()) {
      case NONE:
        return (text) -> new ArrayList<>();
      case KIKUKO_TOOL:
        return new KikukoExtractor("tool", definition.getName(), definition);
      case KIKUKO_PIPELINE:
        return new KikukoExtractor("pipeline", definition.getName(), definition);
      case KIKUKO_DOMAIN:
        return new KikukoExtractor("domain", definition.getName(), definition);
      case MOCK:
        return new MockExtractor(((MockExtractorDefinition) definition).getExpectedEntities());
      default:
        throw new IllegalArgumentException(
            "Unbekannter Typ: " + definition.getType().name());
    }
  }
}
