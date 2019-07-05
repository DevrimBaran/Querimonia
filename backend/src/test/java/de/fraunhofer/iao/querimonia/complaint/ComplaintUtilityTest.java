package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test class for ComplaintUtility
 *
 * @author Simon Weiler
 */
public class ComplaintUtilityTest {

  @Test
  public void testGetEntryWithHighestProbabilityStandard() {
    Map<String, Double> probabilityMap = new HashMap<>();
    probabilityMap.put("Anger", 0.25);
    probabilityMap.put("Sadness", 0.5);
    probabilityMap.put("Happiness", 1.0);

    Optional<String> result = ComplaintUtility.getEntryWithHighestProbability(probabilityMap);
    assertNotEquals(Optional.empty(), result);
    result.ifPresent(s -> assertEquals("Happiness", s));
  }

  @Test
  public void testGetEntryWithHighestProbabilityTwoEqualValues() {
    Map<String, Double> probabilityMap = new HashMap<>();
    probabilityMap.put("Anger", 0.25);
    probabilityMap.put("Sadness", 0.5);
    probabilityMap.put("Happiness", 0.5);

    Optional<String> result = ComplaintUtility.getEntryWithHighestProbability(probabilityMap);
    assertNotEquals(Optional.empty(), result);
    result.ifPresent(s -> assertEquals("Happiness", s));
  }

  @Test
  public void testGetEntryWithHighestProbabilityThreeEqualValues() {
    Map<String, Double> probabilityMap = new HashMap<>();
    probabilityMap.put("Anger", 0.5);
    probabilityMap.put("Sadness", 0.5);
    probabilityMap.put("Happiness", 0.5);

    Optional<String> result = ComplaintUtility.getEntryWithHighestProbability(probabilityMap);
    assertNotEquals(Optional.empty(), result);
    result.ifPresent(s -> assertEquals("Anger", s));
  }

  @Test
  public void testGetEntryWithHighestProbabilityDoubleMinValue() {
    Map<String, Double> probabilityMap = new HashMap<>();
    probabilityMap.put("Anger", Double.MIN_VALUE);
    probabilityMap.put("Sadness", 0.5);
    probabilityMap.put("Happiness", 1.0);

    Optional<String> result = ComplaintUtility.getEntryWithHighestProbability(probabilityMap);
    assertNotEquals(Optional.empty(), result);
    result.ifPresent(s -> assertEquals("Happiness", s));
  }

  @Test
  public void testGetEntryWithHighestProbabilityDoubleMaxValue() {
    Map<String, Double> probabilityMap = new HashMap<>();
    probabilityMap.put("Anger", 0.25);
    probabilityMap.put("Sadness", 0.5);
    probabilityMap.put("Happiness", Double.MAX_VALUE);

    Optional<String> result = ComplaintUtility.getEntryWithHighestProbability(probabilityMap);
    assertNotEquals(Optional.empty(), result);
    result.ifPresent(s -> assertEquals("Happiness", s));
  }

  @Test
  public void testGetEntryWithHighestProbabilityEmptyResult() {
    Map<String, Double> probabilityMap = new HashMap<>();

    Optional<String> result = ComplaintUtility.getEntryWithHighestProbability(probabilityMap);
    assertEquals(Optional.empty(), result);
  }

  @SuppressWarnings("ConstantConditions")
  @Test(expected = NullPointerException.class)
  public void testGetEntryWithHighestProbabilityNullProbabilityMap() {
    ComplaintUtility.getEntryWithHighestProbability(null);
  }

  @Test
  public void testGetEntityValueMapStandard() {
    String text = "No, Luke, I am your father!";

    List<NamedEntity> entities = new ArrayList<>();
    entities.add(new NamedEntity("Response", 0, 2));
    entities.add(new NamedEntity("Name", 4, 8));
    entities.add(new NamedEntity("Relation", 20, 26));

    Map<String, String> result = ComplaintUtility.getEntityValueMap(text, entities);

    assertTrue(result.keySet().contains("Response"));
    assertEquals("No", result.get("Response"));

    assertTrue(result.keySet().contains("Name"));
    assertEquals("Luke", result.get("Name"));

    assertTrue(result.keySet().contains("Relation"));
    assertEquals("father", result.get("Relation"));
  }

  @Test
  public void testGetEntityValueMapEverything() {
    String text = "No, Luke, I am your father!";

    List<NamedEntity> entities = new ArrayList<>();
    entities.add(new NamedEntity("Everything", 0, 27));

    Map<String, String> result = ComplaintUtility.getEntityValueMap(text, entities);

    assertTrue(result.keySet().contains("Everything"));
    assertEquals(text, result.get("Everything"));
  }

  @Test
  public void testGetEntityValueMapNothing() {
    String text = "No, Luke, I am your father!";

    List<NamedEntity> entities = new ArrayList<>();
    entities.add(new NamedEntity("Nothing", 5, 5));

    Map<String, String> result = ComplaintUtility.getEntityValueMap(text, entities);

    assertTrue(result.keySet().contains("Nothing"));
    assertEquals("", result.get("Nothing"));
  }

  @Test
  public void testGetEntityValueMapEmptyString() {
    String text = "";

    List<NamedEntity> entities = new ArrayList<>();
    entities.add(new NamedEntity("Nothing", 0, 0));

    Map<String, String> result = ComplaintUtility.getEntityValueMap(text, entities);

    assertTrue(result.keySet().contains("Nothing"));
    assertEquals("", result.get("Nothing"));
  }

  @Test
  public void testGetEntityValueMapEmptyEntities() {
    String text = "No, Luke, I am your father!";

    List<NamedEntity> entities = new ArrayList<>();

    Map<String, String> result = ComplaintUtility.getEntityValueMap(text, entities);

    assertTrue(result.isEmpty());
  }

  @Test(expected = ResponseStatusException.class)
  public void testGetEntityValueMapSwitchedIndices() {
    String text = "No, Luke, I am your father!";

    List<NamedEntity> entities = new ArrayList<>();
    entities.add(new NamedEntity("Switched", 10, 5));

    ComplaintUtility.getEntityValueMap(text, entities);
  }

  @Test(expected = ResponseStatusException.class)
  public void testGetEntityValueMapNegativeIndices() {
    String text = "No, Luke, I am your father!";

    List<NamedEntity> entities = new ArrayList<>();
    entities.add(new NamedEntity("Negative", -10, -5));

    ComplaintUtility.getEntityValueMap(text, entities);
  }

  @Test(expected = ResponseStatusException.class)
  public void testGetEntityValueMapTooLargeIndices() {
    String text = "No, Luke, I am your father!";

    List<NamedEntity> entities = new ArrayList<>();
    entities.add(new NamedEntity("TooLarge", 30, 35));

    ComplaintUtility.getEntityValueMap(text, entities);
  }
}
