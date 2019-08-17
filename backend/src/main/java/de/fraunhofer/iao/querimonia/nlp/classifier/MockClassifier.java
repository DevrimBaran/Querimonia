package de.fraunhofer.iao.querimonia.nlp.classifier;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;

import java.util.Map;

/**
 * Mocked version of the {@link KiKuKoClassifier}.
 * Doesn't contact KiKuKo, instead returns the values defined in it's constructor.
 *
 * @author simon
 */
public class MockClassifier implements Classifier {

  private final String categoryName;
  private final Map<String, Double> expectedCategories; // the mocked probability values

  protected MockClassifier(String categoryName,
                           Map<String, Double> expectedCategories) {
    this.categoryName = categoryName;
    this.expectedCategories = expectedCategories;
  }

  @Override
  public ComplaintProperty classifyText(String text) {
    return new ComplaintProperty(categoryName, expectedCategories);
  }
}
