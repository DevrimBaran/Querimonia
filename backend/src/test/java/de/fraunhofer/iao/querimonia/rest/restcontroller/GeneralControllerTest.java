package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Combination;
import de.fraunhofer.iao.querimonia.complaint.TestComplaints;
import de.fraunhofer.iao.querimonia.config.TestConfigurations;
import de.fraunhofer.iao.querimonia.manager.CombinationManager;
import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.manager.ResponseComponentManager;
import de.fraunhofer.iao.querimonia.repository.*;
import de.fraunhofer.iao.querimonia.response.component.TestComponents;
import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test class for GeneralController
 *
 * @author simon
 */
public class GeneralControllerTest {

  private MockComplaintRepository mockComplaintRepository;
  private MockConfigurationRepository mockConfigurationRepository;
  private MockCombinationRepository mockCombinationRepository;
  private MockComponentRepository mockComponentRepository;
  private ComplaintManager complaintManager;
  private ConfigurationManager configurationManager;
  private CombinationManager combinationManager;
  private ResponseComponentManager responseComponentManager;

  @Before
  public void setUp() {
    mockComplaintRepository = new MockComplaintRepository();
    mockConfigurationRepository = new MockConfigurationRepository();
    mockCombinationRepository = new MockCombinationRepository();
    mockComponentRepository = new MockComponentRepository();
    MockSuggestionRepository mockSuggestionRepository = new MockSuggestionRepository();
    MockCompletedComponentRepository mockCompletedComponentRepository = new MockCompletedComponentRepository();

    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir("src/test/resources/uploads/");
    FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

    configurationManager = new ConfigurationManager(
        mockConfigurationRepository,
        mockComplaintRepository,
        fileStorageService);

    complaintManager = new ComplaintManager(
        fileStorageService,
        mockComplaintRepository,
        mockComponentRepository,
        configurationManager,
        mockCombinationRepository);

    combinationManager = new CombinationManager(
        mockCombinationRepository,
        fileStorageService);

    responseComponentManager = new ResponseComponentManager(
        mockComponentRepository,
        mockComplaintRepository,
        mockSuggestionRepository,
        mockCompletedComponentRepository,
        fileStorageService);
  }

  @Test
  public void testReset() {
    mockComplaintRepository.save(TestComplaints.COMPLAINT_A);
    mockConfigurationRepository.save(TestConfigurations.CONFIGURATION_A);
    mockCombinationRepository.save(new Combination("123", "Test", "tseT"));
    mockComponentRepository.save(TestComponents.COMPONENT_A);

    GeneralController generalController = new GeneralController(
        complaintManager,
        configurationManager,
        combinationManager,
        responseComponentManager
    );

    generalController.reset();

    assertEquals(0, mockComplaintRepository.count());
    assertTrue(mockConfigurationRepository.count() >= 1);
    assertTrue(mockCombinationRepository.count() >= 1);
    assertTrue(mockComponentRepository.count() >= 1);
  }
}