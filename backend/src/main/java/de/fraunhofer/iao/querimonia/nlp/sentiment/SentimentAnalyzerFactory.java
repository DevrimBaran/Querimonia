package de.fraunhofer.iao.querimonia.nlp.sentiment;

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
        return complaintText -> -2.0;
      case QUERIMONIA_SENTIMENT:
        return new FlaskSentiment();
      case MOCK:
        return new MockSentimentAnalyzer(
            ((MockSentimentAnalyzerDefinition) definition).getExpectedSentiment());
      default:
        throw new IllegalArgumentException("Unbekannter Typ: " + definition.getType().name());
    }
  }
}
