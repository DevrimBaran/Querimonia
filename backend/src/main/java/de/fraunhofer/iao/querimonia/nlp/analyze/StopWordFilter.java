package de.fraunhofer.iao.querimonia.nlp.analyze;

import java.util.Map;

/**
 * This interface is used to extract the words of a complaint text.
 */
public interface StopWordFilter {

  /**
   * Tokenizes the given text into the words and removes all stop words.
   *
   * @param complaintText the text of the complaint.
   *
   * @return a map which contains all non stop-words as keys mapped to the count they occur in the
   *     text.
   */
  Map<String, Integer> filterStopWords(String complaintText);
}
