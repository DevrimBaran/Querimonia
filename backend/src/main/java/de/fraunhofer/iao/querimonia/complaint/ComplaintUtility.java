package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
   * @param entities    the named entities in the complaint text.
   * @param entityLabel the label of the named entity, like "date".
   * @return the value of the named entity or an empty optional if the entity is not present. If
   * there are multiple occurrences, it will return the first one.
   */
  public static Optional<String> getValueOfEntity(String text,
                                                   List<NamedEntity> entities,
                                                   String entityLabel) {
    return getAllValuesOfEntity(text, entities, entityLabel).stream().findFirst();
  }

  /**
   * Finds all entities with the given label in the complaint text and returns their value as a
   * list.
   *
   * @param text        the complaint text.
   * @param entities    the named entities in the complaint text
   * @param entityLabel the entity label to look for, like "date".
   * @return a list of all values of the named entities with the given label.
   */
  private static List<String> getAllValuesOfEntity(String text, List<NamedEntity> entities,
                                                   String entityLabel) {
    return entities.stream()
        // only use entities that label match the given one.
        .filter(namedEntity -> namedEntity.getLabel().equalsIgnoreCase(entityLabel))
        // find their value in the text
        .map(namedEntity -> {
          try {
            return text.substring(namedEntity.getStartIndex(),
                                  namedEntity.getEndIndex());
          } catch (IndexOutOfBoundsException e) {
            throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
                                              "Entity error: " + namedEntity.getLabel()
                                                  + " in:\n " + text
                                                  + "\n " + e.getMessage(), "Fehlerhafte Entity");
          }
        })
        .collect(Collectors.toList());
  }

  /**
   * Creates a map that maps all the entity labels to their values in the text.
   *
   * @param text     the complaint text.
   * @param entities the named entities in the complaint text.
   */
  public static Map<String, String> getEntityValueMap(String text,
                                                      List<NamedEntity> entities) {
    HashMap<String, String> result = new HashMap<>();
    entities.stream()
        .map(NamedEntity::getLabel)
        .forEach(label -> result.put(label,
                                     getValueOfEntity(text, entities, label)
                                         .orElseThrow(IllegalStateException::new)));

    return result;
  }
}
