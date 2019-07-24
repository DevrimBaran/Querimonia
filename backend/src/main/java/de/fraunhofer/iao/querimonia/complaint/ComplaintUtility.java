package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Helper methods for working with complaints.
 */
public class ComplaintUtility {

  /**
   * Helper function to get the value with the highest probability out a probability map.
   *
   * @param probabilityMap the map which maps the value to their probabilities.
   *
   * @return the entry with the highest probability or an empty optional if the map is empty.
   */
  public static Optional<String> getEntryWithHighestProbability(
      Map<String, Double> probabilityMap) {
    return probabilityMap.entrySet()
        .stream()
        // find entry with highest probability
        .max(Comparator.comparingDouble(Map.Entry::getValue))
        .map(Map.Entry::getKey);
  }

  /**
   * Returns a certain property of the complaint.
   *
   * @param complaint the complaint.
   * @param name      the name of the property that should be extracted.
   *
   * @return the property with the given name.
   *
   * @throws IllegalStateException if the no property with the given name exists.
   */
  public static ComplaintProperty getPropertyOfComplaint(Complaint complaint, String name) {
    return complaint.getProperties()
        .stream()
        .filter(complaintProperty -> complaintProperty.getName().equals(name))
        .findAny()
        .orElseThrow(IllegalStateException::new);
  }

  /**
   * Checks the length of a string an throws an exception if the length is too long.
   *
   * @param toCheck   the string which length gets checked.
   * @param maxLength the maximal length that the string may have.
   *
   * @throws QuerimoniaException if the given string is too long.
   */
  public static void checkStringLength(String toCheck, int maxLength) {
    if (toCheck.length() > maxLength) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
          "Textlänge überschreitet Maximum von " + maxLength + " Zeichen!", "Zu langer Text");
    }
  }
}
