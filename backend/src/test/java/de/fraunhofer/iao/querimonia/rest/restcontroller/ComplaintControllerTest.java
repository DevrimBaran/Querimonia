package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.TestComplaints;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.config.TestConfigurations;
import de.fraunhofer.iao.querimonia.db.repositories.*;
import de.fraunhofer.iao.querimonia.property.AnalyzerConfigProperties;
import de.fraunhofer.iao.querimonia.property.FileStorageProperties;
import de.fraunhofer.iao.querimonia.rest.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.rest.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import io.micrometer.core.ipc.http.HttpSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.Charset;
import java.util.Optional;

import static org.junit.Assert.*;

public class ComplaintControllerTest {

  private ComplaintController complaintController;
  private ComplaintRepository complaintRepository;
  private ResponseComponentRepository responseComponentRepository;
  private ConfigurationRepository configurationRepository;

  @Before
  public void setUp() throws Exception {
    complaintRepository = new MockComplaintRepository();
    responseComponentRepository = new MockComponentRepository();
    configurationRepository = new MockConfigurationRepository();

    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir("src/test/resources/uploads/");

    AnalyzerConfigProperties configProperties = new AnalyzerConfigProperties();
    configProperties.setId(0);

    complaintController = new ComplaintController(new ComplaintManager(
        new FileStorageService(fileStorageProperties),
        complaintRepository,
        responseComponentRepository,
        new ConfigurationManager(
            configurationRepository,
            complaintRepository
        )
    ));
  }

  @After
  public void tearDown() throws Exception {
  }

  // TC 1
  @Test
  public void getComplaints() {
    // TODO get complaints test
  }

  // TC 2
  @Test
  public void uploadComplaint() {
    // various config parameters and other equivalency classes are tested in TC 3
    String testText = TestComplaints.TestTexts.TEXT_A;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_A;
    configurationRepository.save(testConfiguration);

    // TC 2.1 complaint upload
    ResponseEntity<?> responseEntity =
        complaintController.uploadComplaint(new MockMultipartFile("TestFile.txt", "TestFile.txt",
            "text/plain",
            testText.getBytes(Charset.defaultCharset())), Optional.of(1));
    assertEquals("Wrong status code on success", HttpStatus.CREATED,
        responseEntity.getStatusCode());
    Complaint body = (Complaint) responseEntity.getBody();
    assertNotNull("Missing body", body);
    assertEquals("complaint is not correct", TestComplaints.COMPLAINT_A, body);
    assertTrue("db does not contain new complaint", complaintRepository.existsById(1L));
    assertEquals("wrong complaint in db", complaintRepository.findById(1L).orElse(null),
        TestComplaints.COMPLAINT_A);
  }

  // TC 3
  @Test
  public void uploadText() {
  }

  @Test
  public void getComplaint() {
  }

  @Test
  public void updateComplaint() {
  }

  @Test
  public void deleteComplaint() {
  }

  @Test
  public void refreshComplaint() {
  }

  @Test
  public void closeComplaint() {
  }

  @Test
  public void countComplaints() {
  }

  @Test
  public void getEntities() {
  }

  @Test
  public void addEntity() {
  }

  @Test
  public void removeEntity() {
  }

  @Test
  public void deleteAllComplaints() {
  }
}