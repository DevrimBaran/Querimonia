package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.MockComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.MockComponentRepository;
import de.fraunhofer.iao.querimonia.db.repositories.MockConfigurationRepository;
import de.fraunhofer.iao.querimonia.property.FileStorageProperties;
import de.fraunhofer.iao.querimonia.rest.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.rest.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.COMPLAINT_F;
import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.TestResponses.SUGGESTION_C;
import static org.junit.Assert.*;

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
    ConfigurationManager configurationManager = new ConfigurationManager(configurationRepository, complaintRepository);
    ComplaintManager complaintManager = new ComplaintManager(fileStorageService, complaintRepository, componentRepository, configurationManager);
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
