package de.fraunhofer.iao.querimonia.nlp.emotion;

import java.util.Map;

/**
 * Definition of a {@link MockEmotionAnalyzer}.
 * Additionally to a normal emotion analyzer definition, this one contains predefined output values.
 */
public class MockEmotionAnalyzerDefinition extends EmotionAnalyzerDefinition {

  private Map<String, Double> expectedEmotions;

  public MockEmotionAnalyzerDefinition(Map<String, Double> expectedEmotions) {
    this.expectedEmotions = expectedEmotions;
  }

  public Map<String, Double> getExpectedEmotions() {
    return expectedEmotions;
  }
}
