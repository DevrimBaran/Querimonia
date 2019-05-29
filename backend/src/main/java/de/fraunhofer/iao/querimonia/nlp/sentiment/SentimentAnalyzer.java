package de.fraunhofer.iao.querimonia.nlp.sentiment;

import java.util.LinkedHashMap;

/**
 * This interface is used to analyze the sentiment of a text.
 */
public interface SentimentAnalyzer {

  /**
   * Analyzes the sentiment of a given text.
   *
   * @param text the text that should be analyzed.
   * @return a linked hash map that maps the possible sentiments to their probability.
   */
  LinkedHashMap<String, Double> analyzeSentiment(String text);
}
