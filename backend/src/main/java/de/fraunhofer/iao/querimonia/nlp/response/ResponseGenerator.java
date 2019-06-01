package de.fraunhofer.iao.querimonia.nlp.response;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.ResponseSuggestion;

import java.util.List;
import java.util.Map;

/**
 * This interface is used to generate response texts from given complaints.
 */
public interface ResponseGenerator {

  /**
   * Generates a response out of the given information.
   *
   * @param text the complaint text.
   * @param subjectMap contains the subjects of the complaint mapped to their probabilities.
   * @param sentimentMap contains the sentiments of the complaint mapped to their probabilities.
   * @param entities the found named entities in the text, also including upload date and time.
   * @return a generated response suggestion out of the given information.
   */
  ResponseSuggestion generateResponse(String text,
                                      Map<String, Double> subjectMap,
                                      Map<String, Double> sentimentMap,
                                      List<NamedEntity> entities);
}
