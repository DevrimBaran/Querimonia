
package de.fraunhofer.iao.querimonia.rest.manager.filter;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Unit test class for ComplaintFilter
 *
 * @author Simon Weiler
 */

public class ComplaintFilterTest {

  private static String testText;
  private static ComplaintProperty testSentiment;
  private static ComplaintProperty testSubject;
  private static LocalDate testReceiveDate;
  private static Optional<String> optionalMinDateString;
  private static Optional<String> optionalMaxDateString;
  private final String TEST_PREVIEW = "preview";
  private final ComplaintState TEST_STATE = ComplaintState.NEW;
  private final LocalTime TEST_RECEIVE_TIME = LocalTime.NOON;
  private final List<NamedEntity> TEST_ENTITIES = new ArrayList<>();

  @BeforeClass
  public static void initialize() {

    testText = "It's over Anakin, I have the high ground!";

    HashMap<String, Double> testSentimentProbabilityMap = new HashMap<>();
    testSentimentProbabilityMap.put("Anger", 0.75);
    testSentimentProbabilityMap.put("Sadness", 0.5);
    testSentimentProbabilityMap.put("Joy", 0.1);
    testSentiment = new ComplaintProperty(testSentimentProbabilityMap);

    HashMap<String, Double> testSubjectProbabilityMap = new HashMap<>();
    testSubjectProbabilityMap.put("Late arrival", 0.75);
    testSubjectProbabilityMap.put("Impolite driver", 0.25);
    testSubjectProbabilityMap.put("Other", 0.1);
    testSubject = new ComplaintProperty(testSubjectProbabilityMap);

    testReceiveDate = LocalDate.of(1970, 1, 1);

    optionalMinDateString = Optional.of("2019-01-01");
    optionalMaxDateString = Optional.of("2019-12-31");
  }

  @Test
  public void testFilterByDateStandard() {
    LocalDate receiveDate = LocalDate.of(2019, 6, 15);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            receiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(
        ComplaintFilter.filterByDate(testComplaint, optionalMinDateString, optionalMaxDateString));
  }

  @Test
  public void testFilterByDateMin() {

    LocalDate receiveDate = LocalDate.of(2019, 1, 1);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            receiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(
        ComplaintFilter.filterByDate(testComplaint, optionalMinDateString, optionalMaxDateString));
  }

  @Test
  public void testFilterByDateMax() {

    LocalDate receiveDate = LocalDate.of(2019, 12, 31);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            receiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(
        ComplaintFilter.filterByDate(testComplaint, optionalMinDateString, optionalMaxDateString));
  }

  @Test
  public void testFilterByDateBelowMinDate() {
    LocalDate receiveDate = LocalDate.of(2018, 6, 15);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            receiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(
        ComplaintFilter.filterByDate(testComplaint, optionalMinDateString, optionalMaxDateString));
  }

  @Test
  public void testFilterByDateAboveMaxDate() {
    LocalDate receiveDate = LocalDate.of(2020, 6, 15);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            receiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(
        ComplaintFilter.filterByDate(testComplaint, optionalMinDateString, optionalMaxDateString));
  }

  @Test
  public void testFilterByDateNoMinDate() {
    LocalDate receiveDate = LocalDate.of(2019, 6, 15);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            receiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(
        ComplaintFilter.filterByDate(testComplaint, Optional.empty(), optionalMaxDateString));
  }

  @Test
  public void testFilterByDateNoMaxDate() {
    LocalDate receiveDate = LocalDate.of(2019, 6, 15);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            receiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(
        ComplaintFilter.filterByDate(testComplaint, optionalMinDateString, Optional.empty()));
  }

  @Test
  public void testFilterByDateNoLimits() {
    LocalDate receiveDate = LocalDate.of(2019, 6, 15);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            receiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterByDate(testComplaint, Optional.empty(), Optional.empty()));
  }

  @Test
  public void testFilterByKeywordsStandard() {
    String[] keywords = {"over", "Anakin", "high ground"};
    Optional<String[]> optionalKeywords = Optional.of(keywords);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterByKeywords(testComplaint, optionalKeywords));
  }

  @Test
  public void testFilterByKeywordsNegative() {
    String[] keywords = {"over", "Anakin", "high ground", "Obi-Wan"};
    Optional<String[]> optionalKeywords = Optional.of(keywords);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterByKeywords(testComplaint, optionalKeywords));
  }

  @Test
  public void testFilterByKeywordsCases() {
    String[] keywords = {"OVER", "anakin", "hIgH gRoUnD"};
    Optional<String[]> optionalKeywords = Optional.of(keywords);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterByKeywords(testComplaint, optionalKeywords));
  }

  @Test
  public void testFilterByKeywordsNoKeywords() {
    String[] keywords = {};
    Optional<String[]> optionalKeywords = Optional.of(keywords);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterByKeywords(testComplaint, optionalKeywords));
  }

  @Test
  public void testFilterByKeywordsEmptyKeywords() {
    Optional<String[]> optionalKeywords = Optional.empty();

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterByKeywords(testComplaint, optionalKeywords));
  }

  @Test
  public void testFilterByKeywordsNoComplaintText() {
    String[] keywords = {"over", "Anakin", "high ground"};
    Optional<String[]> optionalKeywords = Optional.of(keywords);

    Complaint testComplaint = new Complaint(null,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterByKeywords(testComplaint, optionalKeywords));
  }

  @Test
  public void testFilterBySentimentStandard() {
    String[] sentiments = {"Fury", "Anger", "Rage"};
    Optional<String[]> optionalSentiments = Optional.of(sentiments);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterBySentiment(testComplaint, optionalSentiments));
  }

  @Test
  public void testFilterBySentimentNegative() {
    String[] sentiments = {"Fury", "Unhappiness", "Rage"};
    Optional<String[]> optionalSentiments = Optional.of(sentiments);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterBySentiment(testComplaint, optionalSentiments));
  }

  @Test
  public void testFilterBySentimentNotMaxSentiment() {
    String[] sentiments = {"Sadness"};
    Optional<String[]> optionalSentiments = Optional.of(sentiments);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterBySentiment(testComplaint, optionalSentiments));
  }

  @Test
  public void testFilterBySentimentNoSentiments() {
    String[] sentiments = {};
    Optional<String[]> optionalSentiments = Optional.of(sentiments);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterBySentiment(testComplaint, optionalSentiments));
  }

  @Test
  public void testFilterBySentimentEmptySentiments() {
    Optional<String[]> optionalSentiments = Optional.empty();

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterBySentiment(testComplaint, optionalSentiments));
  }

  @Test
  public void testFilterBySentimentNoComplaintSentiment() {
    ComplaintProperty complaintSentiment = new ComplaintProperty(new HashMap<>());

    String[] sentiments = {"Anger"};
    Optional<String[]> optionalSentiments = Optional.of(sentiments);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            complaintSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterBySentiment(testComplaint, optionalSentiments));
  }

  @Test
  public void testFilterBySubjectStandard() {
    String[] subjects = {"Late arrival"};
    Optional<String[]> optionalSubjects = Optional.of(subjects);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterBySubject(testComplaint, optionalSubjects));
  }

  @Test
  public void testFilterBySubjectNegative() {
    String[] subjects = {"Bad service"};
    Optional<String[]> optionalSubjects = Optional.of(subjects);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterBySubject(testComplaint, optionalSubjects));
  }

  @Test
  public void testFilterBySubjectNotMaxSubject() {
    String[] subjects = {"Impolite driver"};
    Optional<String[]> optionalSubjects = Optional.of(subjects);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterBySubject(testComplaint, optionalSubjects));
  }

  @Test
  public void testFilterBySubjectNoSubjects() {
    String[] subjects = {};
    Optional<String[]> optionalSubjects = Optional.of(subjects);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterBySubject(testComplaint, optionalSubjects));
  }

  @Test
  public void testFilterBySubjectEmptySubjects() {
    Optional<String[]> optionalSubjects = Optional.empty();

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            testSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertTrue(ComplaintFilter.filterBySubject(testComplaint, optionalSubjects));
  }

  @Test
  public void testFilterBySubjectNoComplaintSubjects() {
    ComplaintProperty complaintSubject = new ComplaintProperty(new HashMap<>());

    String[] subjects = {"Late arrival"};
    Optional<String[]> optionalSubjects = Optional.of(subjects);

    Complaint testComplaint = new Complaint(testText,
                                            TEST_PREVIEW,
                                            TEST_STATE,
                                            testSentiment,
                                            complaintSubject,
                                            testReceiveDate,
                                            TEST_RECEIVE_TIME,
                                            TEST_ENTITIES);

    assertFalse(ComplaintFilter.filterBySubject(testComplaint, optionalSubjects));
  }

  @Test
  public void testCreateComplaintComparatorUndefinedSortingByDatesPositive() {
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(Optional.empty());

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 15),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 16),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUndefinedSortingByDatesNegative() {
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(Optional.empty());

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 16),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 15),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(-1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUndefinedSortingByTimesPositive() {
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(Optional.empty());

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 30),
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 45),
                                             TEST_ENTITIES);

    assertEquals(1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUndefinedSortingByTimesNegative() {
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(Optional.empty());

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 45),
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 30),
                                             TEST_ENTITIES);

    assertEquals(-1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUndefinedSortingEqualTimes() {
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(Optional.empty());

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(0, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateSortingByDatesPositive() {
    String[] sortBy = {"upload_date_"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 15),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 16),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(-1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateSortingByDatesNegative() {
    String[] sortBy = {"upload_date_"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 16),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 15),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateSortingByTimesPositive() {
    String[] sortBy = {"upload_date_"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 30),
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 45),
                                             TEST_ENTITIES);

    assertEquals(-1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateSortingByTimesNegative() {
    String[] sortBy = {"upload_date_"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 45),
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 30),
                                             TEST_ENTITIES);

    assertEquals(1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateSortingEqualTimes() {
    String[] sortBy = {"upload_date_"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(0, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateDescendingSortingByDatesPositive() {
    String[] sortBy = {"upload_date_desc"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 15),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 16),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateDescendingSortingByDatesNegative() {
    String[] sortBy = {"upload_date_desc"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 16),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             LocalDate.of(2019, 6, 15),
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(-1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateDescendingSortingByTimesPositive() {
    String[] sortBy = {"upload_date_desc"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 30),
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 45),
                                             TEST_ENTITIES);

    assertEquals(1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateDescendingSortingByTimesNegative() {
    String[] sortBy = {"upload_date_desc"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 45),
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             LocalTime.of(12, 30),
                                             TEST_ENTITIES);

    assertEquals(-1, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test
  public void testCreateComplaintComparatorUploadDateDescendingSortingEqualTimes() {
    String[] sortBy = {"upload_date_desc"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    assertEquals(0, complaintComparator.compare(testComplaint1, testComplaint2));
  }

  @Test(expected = ResponseStatusException.class)
  public void testCreateComplaintComparatorIllegalSortingParameter() {
    String[] sortBy = {"cool_sorting_parameter"};
    Optional<String[]> optionalSortBy = Optional.of(sortBy);
    Comparator<Complaint> complaintComparator =
        ComplaintFilter.createComplaintComparator(optionalSortBy);

    Complaint testComplaint1 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    Complaint testComplaint2 = new Complaint(testText,
                                             TEST_PREVIEW,
                                             TEST_STATE,
                                             testSentiment,
                                             testSubject,
                                             testReceiveDate,
                                             TEST_RECEIVE_TIME,
                                             TEST_ENTITIES);

    int comparison = complaintComparator.compare(testComplaint1, testComplaint2);
  }
}
