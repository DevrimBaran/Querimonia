package de.fraunhofer.iao.querimonia.complaint;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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


}
