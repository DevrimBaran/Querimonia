package de.fraunhofer.iao.querimonia.nlp.sentiment;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;

import java.util.HashMap;

/**
 * This factory creates {@link SentimentAnalyzer} objects from {@link SentimentAnalyzerDefinition
 * SentimentAnalyzerDefinitions}.
 */
public class SentimentAnalyzerFactory {

  /**
   * Creates a sentiment analyzer from a definition.
   *
   * @param definition this must specify a valid sentiment analyzer.
   * @return a sentiment analyzer from the definition.
   * @throws IllegalArgumentException if the definition is not valid.
   */
  public static SentimentAnalyzer getFromDefinition(SentimentAnalyzerDefinition definition) {
    switch (definition.getType()) {
      case NONE:
        var baseMap = new HashMap<String, Double>();
        baseMap.put("Unbekannt", 1.0);
        return new SentimentAnalyzer() {
          @Override
          public ComplaintProperty analyzeEmotion(String complaintText) {
            return new ComplaintProperty(baseMap, "Emotion");
          }

          @Override
          public double analyzeSentiment(String complaintText) {
            return 0;
          }
        };
      case QUERIMONIA_SENTIMENT:
        return new FlaskSentiment();
      default:
        throw new IllegalArgumentException("Unbekannter Typ: " + definition.getType().name());
    }
  }
}
