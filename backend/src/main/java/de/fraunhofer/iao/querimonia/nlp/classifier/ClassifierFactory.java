package de.fraunhofer.iao.querimonia.nlp.classifier;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        return text -> new ComplaintProperty(baseMap, "Kategorie");
      case KIKUKO_CLASSIFIER:
        return new KiKuKoClassifier(definition.getName(), definition.getCategoryName());
      default:
        throw new IllegalArgumentException(
            "Unbekannter Typ: " + definition.getClassifierType().name());
    }
  }

  /**
   * Creates classifiers from their definitions.
   *
   * @param definitions the list of definitions.
   * @return a list of all creates classifiers.
   */
  public static List<Classifier> getFromDefinition(List<ClassifierDefinition> definitions) {
    return definitions
        .stream()
        .map(ClassifierFactory::getFromDefinition)
        .collect(Collectors.toList());
  }
}
