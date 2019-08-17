package de.fraunhofer.iao.querimonia.nlp.sentiment;

/**
 * Definition of a {@link MockSentimentAnalyzer}.
 * Additionally to a normal sentiment analyzer definition, this one contains predefined output
 * values.
 */
public class MockSentimentAnalyzerDefinition extends SentimentAnalyzerDefinition {

  private double expectedSentiment;

  public MockSentimentAnalyzerDefinition(double expectedSentiment) {
    this.expectedSentiment = expectedSentiment;
  }

  public double getExpectedSentiment() {
    return expectedSentiment;
  }
}
