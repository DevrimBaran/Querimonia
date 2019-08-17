package de.fraunhofer.iao.querimonia.nlp.sentiment;

/**
 * Mocked version of the {@link FlaskSentiment}.
 * Doesn't contact the flask server, instead returns the values defined in it's constructor.
 *
 * @author simon
 */
public class MockSentimentAnalyzer implements SentimentAnalyzer {

  private final double expectedSentiment; // the mocked sentiment value

  public MockSentimentAnalyzer(double expectedSentiment) {
    this.expectedSentiment = expectedSentiment;
  }

  @Override
  public double analyzeSentiment(String complaintText) {
    return expectedSentiment;
  }
}
