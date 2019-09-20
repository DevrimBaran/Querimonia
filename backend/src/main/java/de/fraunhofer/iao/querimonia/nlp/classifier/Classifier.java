package de.fraunhofer.iao.querimonia.nlp.classifier;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;

/**
 * This interface is used to classify a text in a category.
 */
public interface Classifier {

  /**
   * Classifies the text in a category.
   *
   * @param text the text that should be classified.
   * @return a complaint property that contains the content of the classification.
   */
  ComplaintProperty classifyText(String text);
}
