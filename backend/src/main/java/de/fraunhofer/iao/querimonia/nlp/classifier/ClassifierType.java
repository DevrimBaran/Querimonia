package de.fraunhofer.iao.querimonia.nlp.classifier;

/**
 * This enum specifies the type of a classifier.
 */
public enum ClassifierType {

  /**
   * The external classifier of KIKuKo.
   */
  KIKUKO_CLASSIFIER,

  /**
   * A mock classifier that works without KiKuKo.
   */
  MOCK,

  /**
   * Defines that no classifier should be used.
   */
  NONE,

  /**
   * Our own Classifier.
   */
  ML_CLASSIFIER
}
