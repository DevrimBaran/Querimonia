package de.fraunhofer.iao.querimonia.nlp.emotion;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;

/**
 * This is used to analyze the emotion of a complaint.
 */
public interface EmotionAnalyzer {

  /**
   * Returns the emotion of the text as a complaint property.
   *
   * @param complaintText the text of the complaint.
   *
   * @return a complaint property containing the values for the emotions. This property should have
   * the name "Emotion"
   */
  ComplaintProperty analyzeEmotion(String complaintText);
}
