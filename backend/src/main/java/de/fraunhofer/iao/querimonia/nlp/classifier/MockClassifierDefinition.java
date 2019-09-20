package de.fraunhofer.iao.querimonia.nlp.classifier;

import java.util.Map;

/**
 * Definition of a {@link MockClassifier}.
 * Additionally to a normal classifier definition, this one contains predefined output values.
 */
public class MockClassifierDefinition extends ClassifierDefinition {

  private Map<String, Double> expectedCategories;

  public MockClassifierDefinition(ClassifierType classifierType,
                                  String name,
                                  String categoryName,
                                  Map<String, Double> expectedCategories) {
    super(classifierType, name, categoryName);
    this.expectedCategories = expectedCategories;
  }

  public Map<String, Double> getExpectedCategories() {
    return expectedCategories;
  }
}
