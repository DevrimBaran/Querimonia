package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Combination;
import de.fraunhofer.iao.querimonia.manager.CombinationManager;
import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.repository.MockCombinationRepository;
import de.fraunhofer.iao.querimonia.repository.MockComplaintRepository;
import de.fraunhofer.iao.querimonia.repository.MockComponentRepository;
import de.fraunhofer.iao.querimonia.repository.MockConfigurationRepository;
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
    var fileStorageService = new FileStorageService(fileStorageProperties);

    ComplaintManager complaintManager = new ComplaintManager(
        fileStorageService, complaintRepository, componentRepository,
        new ConfigurationManager(configurationRepository, complaintRepository, fileStorageService),
        combinationRepository);

    CombinationManager combinationManager =
        new CombinationManager(combinationRepository, fileStorageService);

    combinationController = new CombinationController(complaintManager, combinationManager);
  }

  @Test
  public void testGetCombinationsOfComplaint() {
    //TODO: Write tests for getCombinationsOfComplaint() of CombinationController
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllCombinations() {
    Combination combination1 = new Combination("U6", "Hauptbahnhof", "Stuttgart");
    Combination combination2 = new Combination("U6", null, "Stuttgart");
    Combination combination3 = new Combination("U6", "Hauptbahnhof", null);
    Combination combination4 = new Combination(null, "Hauptbahnhof", "Stuttgart");
    List<Combination> correctCombinations = List.of(combination1, combination2, combination3, combination4);
    combinationRepository.saveAll(correctCombinations);

    ResponseEntity<?> responseEntity = combinationController.getAllCombinations();
    assertNotNull(responseEntity.getBody());
    List<Combination> testCombinations = (List<Combination>) responseEntity.getBody();

    assertEquals(correctCombinations, testCombinations);
  }

  @Test
  public void testAddCombinations() {
    Combination combination = new Combination("U6", "Hauptbahnhof", "Stuttgart");
    combinationController.addCombinations(Collections.singletonList(combination));

    Iterator<Combination> iterator = combinationRepository.findAll().iterator();
    assertEquals(combination, iterator.next());
    assertFalse(iterator.hasNext());
  }
}