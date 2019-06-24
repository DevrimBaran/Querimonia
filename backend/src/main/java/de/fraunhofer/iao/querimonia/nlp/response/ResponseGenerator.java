package de.fraunhofer.iao.querimonia.nlp.response;

/**
 * This interface is used to generate response texts from given complaints.
 */
public interface ResponseGenerator {

  /**
   * Generates a response out of the given information.
   *
   * @return a generated response suggestion out of the given information.
   */
  ResponseSuggestion generateResponse(ComplaintData complaintData);
}
