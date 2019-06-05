package de.fraunhofer.iao.querimonia.nlp.sentiment;

import java.util.Map;

/**
 * This interface is used to analyze the sentiment of a text.
 */
public interface SentimentAnalyzer {

  /**
   * Analyzes the sentiment of a given text.
   *
   * @param nonStopWords a map that contains all non stop words mapped to the count
   *                     how often their occur in the complaints
   * @return a linked hash map that maps the possible sentiments to their probability.
   */
  Map<String, Double> analyzeSentiment(Map<String, Integer> nonStopWords);
}
