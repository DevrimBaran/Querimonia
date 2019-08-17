package de.fraunhofer.iao.querimonia.nlp.emotion;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;

import java.util.HashMap;

/**
 * This class creates an {@link EmotionAnalyzer} from its {@link EmotionAnalyzerDefinition}.
 */
public class EmotionAnalyzerFactory {

  /**
   * Creates an emotion analyzer from the definition.
   *
   * @param emotionAnalyzerDefinition the definition of the configuration.
   * @return a corresponding emotion analyzer.
   */
  public static EmotionAnalyzer getFromDefinition(
      EmotionAnalyzerDefinition emotionAnalyzerDefinition) {

    if (emotionAnalyzerDefinition.getType().equals(EmotionAnalyzerType.QUERIMONIA_EMOTION)) {
      return new FlaskEmotion();
    } else if (emotionAnalyzerDefinition.getType().equals(EmotionAnalyzerType.MOCK)) {
      return new MockEmotionAnalyzer(
          ((MockEmotionAnalyzerDefinition) emotionAnalyzerDefinition).getExpectedEmotions());
    }

    // create emotion analyzers
    return complaintText -> new ComplaintProperty("Emotion",
        "Unbekannt", new HashMap<>(), false);

  }
}
