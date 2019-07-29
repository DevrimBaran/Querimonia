package de.fraunhofer.iao.querimonia.nlp.sentiment;

/**
 * This interface is used to analyze the sentiment of a text.
 */
public interface SentimentAnalyzer {

  /**
   * Analyzes the sentiment of the given text.
   *
   * @param complaintText the text of the complaint.
   * @return a double value where negative values represent negative sentiment and positive
   * values represent positive sentiment.
   */
  double analyzeSentiment(String complaintText);
}
