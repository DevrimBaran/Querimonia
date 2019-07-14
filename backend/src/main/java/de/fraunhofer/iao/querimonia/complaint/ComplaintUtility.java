package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * Helper methods for working with complaints.
 */
public class ComplaintUtility {

  /**
   * Helper function to get the value with the highest probability out a probability map.
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
   * This methods returns the value of a named entity that occurs in this complaint.
   *
   * @param text        the complaint text
   *
   * @return the value of the named entity.
   */
  public static String getValueOfEntity(String text, NamedEntity namedEntity) {
    try {
      return text.substring(namedEntity.getStartIndex(),
          namedEntity.getEndIndex());
    } catch (IndexOutOfBoundsException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Entity error: " + namedEntity.getLabel()
              + " in:\n " + text
              + "\n " + e.getMessage(), "Fehlerhafte Entity");
    }
  }

  /**
   * Returns a certain property of the complaint.
   *
   * @param complaint the complaint.
   * @param name the name of the property that should be extracted.
   * @return the property with the given name.
   */
  public static ComplaintProperty getPropertyOfComplaint(ComplaintData complaint, String name) {
    return complaint.getProperties()
        .stream()
        .filter(complaintProperty -> complaintProperty.getName().equals(name))
        .findAny()
        .orElseThrow(IllegalStateException::new);
  }

  public static ComplaintProperty getPropertyOfComplaint(Complaint complaint, String name) {
    return complaint.getProperties()
        .stream()
        .filter(complaintProperty -> complaintProperty.getName().equals(name))
        .findAny()
        .orElseThrow(IllegalStateException::new);
  }
}
