package de.fraunhofer.iao.querimonia.rest.restcontroller;


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

import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.COMPLAINT_F;
import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.TestResponses.SUGGESTION_C;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test class for ResponseController
 *
 * @author Simon Weiler
 */
public class ResponseControllerTest {

  private MockComplaintRepository complaintRepository;
  private ResponseController responseController;

  @Before
  public void setUp() {
    FileStorageService fileStorageService = new FileStorageService(new FileStorageProperties());
    complaintRepository = new MockComplaintRepository();
    MockComponentRepository componentRepository = new MockComponentRepository();
    MockConfigurationRepository configurationRepository = new MockConfigurationRepository();
    ConfigurationManager
        configurationManager =
        new ConfigurationManager(configurationRepository, complaintRepository, fileStorageService);
    ComplaintManager complaintManager =
        new ComplaintManager(fileStorageService, complaintRepository, componentRepository,
            configurationManager, new MockCombinationRepository());
    responseController = new ResponseController(complaintManager);
  }

  @Test
  public void testGetResponse() {
    complaintRepository.save(COMPLAINT_F);
    ResponseEntity<?> responseEntity = responseController.getResponse(6);
    assertNotNull(responseEntity.getBody());
    assertEquals(SUGGESTION_C, responseEntity.getBody());
  }

  @Test
  public void testRefreshResponse() {
    complaintRepository.save(COMPLAINT_F);
    ResponseEntity<?> responseEntity = responseController.refreshResponse(6);
    assertNotNull(responseEntity.getBody());
  }
}
