package de.fraunhofer.iao.querimonia.nlp.classifier;

import java.util.HashMap;

/**
 * This factory creates {@link Classifier classifiers} from {@link ClassifierDefinition their
 * definitions}.
 */
public class ClassifierFactory {

  /**
   * Creates a {@link Classifier} from a {@link ClassifierDefinition}.
   *
   * @param definition the definition of a classifier.
   * @return a classifier.
   * @throws IllegalArgumentException if the definition is invalid.
   */
  public static Classifier getFromDefinition(ClassifierDefinition definition) {
    switch (definition.getClassifierType()) {
      case NONE:
        var baseMap = new HashMap<String, Double>();
        baseMap.put("Unbekannt", 1.0);
        return text -> baseMap;
      case KIKUKO_CLASSIFIER:
        return new KiKuKoClassifier(definition.getName());
      default:
        throw new IllegalArgumentException(
            "Unbekannter Typ: " + definition.getClassifierType().name());
    }
  }
}
