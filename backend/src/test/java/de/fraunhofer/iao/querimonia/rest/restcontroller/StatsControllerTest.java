package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.nlp.analyze.TokenAnalyzer;
import de.fraunhofer.iao.querimonia.repository.MockComplaintRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Optional;

import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test class for StatsController
 *
 * @author Simon Weiler
 */
public class StatsControllerTest {
  private StatsController statsController;
  private static Complaint complaintF;
  private static Complaint complaintG;
  private static Complaint complaintH;

  @BeforeClass
  public static void initializeWordLists() {
    TokenAnalyzer tokenAnalyzer = new TokenAnalyzer();
    complaintF = new ComplaintBuilder(COMPLAINT_F)
        .setWordList(tokenAnalyzer.filterStopWords(COMPLAINT_F.getText()))
        .createComplaint();
    complaintG = new ComplaintBuilder(COMPLAINT_G)
        .setWordList(tokenAnalyzer.filterStopWords(COMPLAINT_G.getText()))
        .createComplaint();
    complaintH = new ComplaintBuilder(COMPLAINT_H)
        .setWordList(tokenAnalyzer.filterStopWords(COMPLAINT_H.getText()))
        .createComplaint();
  }

  @Before
  public void setUp() {
    MockComplaintRepository mockComplaintRepository = new MockComplaintRepository();
    mockComplaintRepository.save(complaintF);
    mockComplaintRepository.save(complaintG);
    mockComplaintRepository.save(complaintH);
    statsController = new StatsController(mockComplaintRepository);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsAll() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    correctWords.put("test", 5);
    correctWords.put("hallo", 3);
    correctWords.put("haltestelle", 2);
    correctWords.put("linie", 2);
    correctWords.put("gebt", 1);
    correctWords.put("u6", 1);
    correctWords.put("testbeschwerde", 1);
    correctWords.put("u7", 1);
    correctWords.put("hauptbahnhof", 1);
    correctWords.put("€", 1);
    correctWords.put("123", 1);
    correctWords.put("überfüllt", 1);
    correctWords.put("peter", 1);
    correctWords.put("schlecht", 1);
    correctWords.put("20", 1);
    correctWords.put("42", 1);
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsNone() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.of(0),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsCount() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    correctWords.put("test", 5);
    correctWords.put("hallo", 3);
    correctWords.put("haltestelle", 2);
    correctWords.put("linie", 2);
    correctWords.put("gebt", 1);
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.of(5),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsDateMin() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    correctWords.put("test", 5);
    correctWords.put("hallo", 2);
    correctWords.put("haltestelle", 1);
    correctWords.put("linie", 1);
    correctWords.put("testbeschwerde", 1);
    correctWords.put("u7", 1);
    correctWords.put("123", 1);
    correctWords.put("überfüllt", 1);
    correctWords.put("42", 1);
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.empty(),
        Optional.of("2019-07-01"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsDateMax() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    correctWords.put("test", 5);
    correctWords.put("hallo", 1);
    correctWords.put("haltestelle", 1);
    correctWords.put("linie", 1);
    correctWords.put("gebt", 1);
    correctWords.put("u6", 1);
    correctWords.put("testbeschwerde", 1);
    correctWords.put("hauptbahnhof", 1);
    correctWords.put("€", 1);
    correctWords.put("123", 1);
    correctWords.put("peter", 1);
    correctWords.put("schlecht", 1);
    correctWords.put("20", 1);
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.empty(),
        Optional.empty(), Optional.of("2019-07-30"), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsMinAndMaxDate() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    correctWords.put("test", 5);
    correctWords.put("testbeschwerde", 1);
    correctWords.put("123", 1);
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.empty(),
        Optional.of("2019-07-01"), Optional.of("2019-07-30"), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsSentiment() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    correctWords.put("test", 5);
    correctWords.put("hallo", 1);
    correctWords.put("haltestelle", 1);
    correctWords.put("linie", 1);
    correctWords.put("gebt", 1);
    correctWords.put("u6", 1);
    correctWords.put("testbeschwerde", 1);
    correctWords.put("hauptbahnhof", 1);
    correctWords.put("€", 1);
    correctWords.put("123", 1);
    correctWords.put("peter", 1);
    correctWords.put("schlecht", 1);
    correctWords.put("20", 1);
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.of(new String[] {"Wut"}), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsSubject() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    correctWords.put("hallo", 3);
    correctWords.put("haltestelle", 2);
    correctWords.put("linie", 2);
    correctWords.put("gebt", 1);
    correctWords.put("u6", 1);
    correctWords.put("u7", 1);
    correctWords.put("hauptbahnhof", 1);
    correctWords.put("€", 1);
    correctWords.put("überfüllt", 1);
    correctWords.put("peter", 1);
    correctWords.put("schlecht", 1);
    correctWords.put("20", 1);
    correctWords.put("42", 1);
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new String[] {"Beschwerde"}), Optional.empty());
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetMostCommonWordsWordsOnly() {
    LinkedHashMap<String, Integer> correctWords = new LinkedHashMap<>();
    correctWords.put("test", 5);
    correctWords.put("hallo", 3);
    correctWords.put("haltestelle", 2);
    correctWords.put("linie", 2);
    correctWords.put("gebt", 1);
    correctWords.put("u6", 1);
    correctWords.put("testbeschwerde", 1);
    correctWords.put("u7", 1);
    correctWords.put("hauptbahnhof", 1);
    correctWords.put("überfüllt", 1);
    correctWords.put("peter", 1);
    correctWords.put("schlecht", 1);
    ResponseEntity<?> responseEntity = statsController.getMostCommonWords(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(true));
    assertNotNull(responseEntity.getBody());
    LinkedHashMap<String, Integer> mostCommonWords
        = (LinkedHashMap<String, Integer>) responseEntity.getBody();
    assertEquals(correctWords, mostCommonWords);
  }
}
