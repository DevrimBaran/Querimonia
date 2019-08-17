package de.fraunhofer.iao.querimonia.nlp.emotion;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;

import java.util.Map;

/**
 * Mocked version of the {@link FlaskEmotion}.
 * Doesn't contact the flask server, instead returns the values defined in it's constructor.
 *
 * @author simon
 */
public class MockEmotionAnalyzer implements EmotionAnalyzer {

  private final Map<String, Double> expectedEmotions; // the mocked probability values

  public MockEmotionAnalyzer(Map<String, Double> expectedEmotions) {
    this.expectedEmotions = expectedEmotions;
  }

  @Override
  public ComplaintProperty analyzeEmotion(String complaintText) {
    return new ComplaintProperty("Emotion", expectedEmotions);
  }
}
