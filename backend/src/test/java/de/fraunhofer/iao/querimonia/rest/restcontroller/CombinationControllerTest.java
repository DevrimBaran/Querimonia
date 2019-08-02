package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.combination.LineStopCombination;
import de.fraunhofer.iao.querimonia.db.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.db.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.db.manager.LineStopCombinationManager;
import de.fraunhofer.iao.querimonia.db.repository.MockCombinationRepository;
import de.fraunhofer.iao.querimonia.db.repository.MockComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repository.MockComponentRepository;
import de.fraunhofer.iao.querimonia.db.repository.MockConfigurationRepository;
import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test class for CombinationController
 *
 * @author simon
 */
public class CombinationControllerTest {

  private MockComplaintRepository complaintRepository;
  private MockComponentRepository componentRepository;
  private MockConfigurationRepository configurationRepository;
  private MockCombinationRepository combinationRepository;
  private CombinationController combinationController;

  @Before
  public void setUp() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir("src/test/resources/uploads/");

    complaintRepository = new MockComplaintRepository();
    componentRepository = new MockComponentRepository();
    configurationRepository = new MockConfigurationRepository();
    combinationRepository = new MockCombinationRepository();

    ComplaintManager complaintManager = new ComplaintManager(
        new FileStorageService(fileStorageProperties), complaintRepository, componentRepository,
        new ConfigurationManager(configurationRepository, complaintRepository), combinationRepository);

    LineStopCombinationManager combinationManager = new LineStopCombinationManager(combinationRepository);

    combinationController = new CombinationController(complaintManager, combinationManager);
  }

  @Test
  public void testGetCombinationsOfComplaint() {
    //TODO: Write tests for getCombinationsOfComplaint() of CombinationController
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllCombinations() {
    LineStopCombination combination1 = new LineStopCombination("U6", "Hauptbahnhof", "Stuttgart");
    LineStopCombination combination2 = new LineStopCombination("U6", null, "Stuttgart");
    LineStopCombination combination3 = new LineStopCombination("U6", "Hauptbahnhof", null);
    LineStopCombination combination4 = new LineStopCombination(null, "Hauptbahnhof", "Stuttgart");
    List<LineStopCombination> correctCombinations = List.of(combination1, combination2, combination3, combination4);
    combinationRepository.saveAll(correctCombinations);

    ResponseEntity<?> responseEntity = combinationController.getAllCombinations();
    assertNotNull(responseEntity.getBody());
    List<LineStopCombination> testCombinations = (List<LineStopCombination>) responseEntity.getBody();

    assertEquals(correctCombinations, testCombinations);
  }

  @Test
  public void testAddCombinations() {
    LineStopCombination combination = new LineStopCombination("U6", "Hauptbahnhof", "Stuttgart");
    combinationController.addCombinations(Collections.singletonList(combination));

    Iterator<LineStopCombination> iterator = combinationRepository.findAll().iterator();
    assertEquals(combination, iterator.next());
    assertFalse(iterator.hasNext());
  }
}