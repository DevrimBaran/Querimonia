package de.fraunhofer.iao.querimonia.nlp.classifier;

import java.util.Map;

/**
 * This interface is used to classify a text in a category.
 */
public interface Classifier {

  /**
   * Classifies the text in a category.
   *
   * @param text the text that should be classified.
   * @return a linked hash map which maps a category to the probability that the text belongs to
   * that category, sorted by the probability.
   */
  Map<String, Double> classifyText(String text);
}
