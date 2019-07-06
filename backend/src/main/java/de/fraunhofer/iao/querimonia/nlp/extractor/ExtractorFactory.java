package de.fraunhofer.iao.querimonia.nlp.extractor;

import java.util.ArrayList;

public class ExtractorFactory {

  public static EntityExtractor getFromDefinition(ExtractorDefinition definition) {
    switch (definition.getType()) {
      case NONE:
        return (text) -> new ArrayList<>();
      case KIKUKO_TOOL:
        return new KikukoExtractor("tool", definition.getName());
      case KIKUKO_PIPELINE:
        return new KikukoExtractor("pipeline", definition.getName());
      case KIKUKO_DOMAIN:
        return new KikukoExtractor("domain", definition.getName());
      default:
        throw new IllegalArgumentException(
            "Unbekannter Typ: " + definition.getType().name());
    }
  }
}
