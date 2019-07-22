package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.TestComplaints;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.config.TestConfigurations;
import de.fraunhofer.iao.querimonia.db.repositories.*;
import de.fraunhofer.iao.querimonia.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.property.AnalyzerConfigProperties;
import de.fraunhofer.iao.querimonia.property.FileStorageProperties;
import de.fraunhofer.iao.querimonia.rest.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.rest.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import io.micrometer.core.ipc.http.HttpSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    // a lot TODO get complaints test
  }

  // TC 2
  @Test
  public void uploadComplaint1() {
    // various config parameters and other equivalency classes are tested in TC 3
    String testText = TestComplaints.TestTexts.TEXT_A;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_A;
    configurationRepository.save(testConfiguration);

    // TC 2.1 complaint upload
    ResponseEntity<?> responseEntity =
        complaintController.uploadComplaint(new MockMultipartFile("TestFile.txt", "TestFile.txt",
            "text/plain",
            testText.getBytes(Charset.defaultCharset())), Optional.of(1L));
    assertEquals("Wrong status code on success", HttpStatus.CREATED,
        responseEntity.getStatusCode());
    Complaint body = (Complaint) responseEntity.getBody();
    assertNotNull("Missing body", body);
    assertEquals("complaint is not correct", TestComplaints.COMPLAINT_A, body);
    assertTrue("db does not contain new complaint", complaintRepository.existsById(1L));
    assertEquals("wrong complaint in db", complaintRepository.findById(1L).orElse(null),
        TestComplaints.COMPLAINT_A);
  }

  @Test
  public void uploadComplaint2() {
    String testText = TestComplaints.TestTexts.TEXT_A;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_A;
    configurationRepository.save(testConfiguration);

    // TC 2.2 illegal file format
    var responseEntity =
        complaintController.uploadComplaint(new MockMultipartFile("TestFile.xlsx", "TestFile.xlsx",
            "text/plain",
            testText.getBytes(Charset.defaultCharset())), Optional.of(1L));
    assertEquals("Wrong status code on success", HttpStatus.BAD_REQUEST,
        responseEntity.getStatusCode());
    var exceptionBody = (QuerimoniaException) responseEntity.getBody();
    assertNotNull("Missing body", exceptionBody);
    assertEquals("db does contain anything", 0, complaintRepository.count());
  }

  @Test
  public void uploadComplaint3() {
    String testText = TestComplaints.TestTexts.TEXT_A;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_A;
    configurationRepository.save(testConfiguration);

    // TC 2.2 illegal file contents
    var responseEntity =
        complaintController.uploadComplaint(new MockMultipartFile("TestFile.pdf", "TestFile.pdf",
            "text/plain",
            testText.getBytes(Charset.defaultCharset())), Optional.of(1L));
    assertEquals("Wrong status code on fail", HttpStatus.INTERNAL_SERVER_ERROR,
        responseEntity.getStatusCode());
    var exceptionBody = (QuerimoniaException) responseEntity.getBody();
    assertNotNull("Missing body", exceptionBody);
    assertEquals("db does contain anything", 0, complaintRepository.count());
  }

  @Test
  public void uploadComplaint4() {
    String testText = TestComplaints.TestTexts.TEXT_A;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_A;
    configurationRepository.save(testConfiguration);

    // TC 2.2 illegal file format docx
    var responseEntity =
        complaintController.uploadComplaint(new MockMultipartFile("TestFile.docx", "TestFile.docx",
            "text/plain",
            testText.getBytes(Charset.defaultCharset())), Optional.of(1L));
    assertEquals("Wrong status code on fail", HttpStatus.INTERNAL_SERVER_ERROR,
        responseEntity.getStatusCode());
    var exceptionBody = (QuerimoniaException) responseEntity.getBody();
    assertNotNull("Missing body", exceptionBody);
    assertEquals("db does contain anything", 0, complaintRepository.count());
  }

  // TC 3
  @Test
  public void uploadText1() {
    // setup
    String testText = TestComplaints.TestTexts.TEXT_E;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_A.withActive(true);
    configurationRepository.save(testConfiguration);
    Configuration testConfiguration2 = TestConfigurations.CONFIGURATION_B;
    configurationRepository.save(testConfiguration2);

    // TC 3.1 using default configuration
    var response = complaintController.uploadText(new TextInput(testText), Optional.empty());
    assertEquals("Wrong status code on success", HttpStatus.CREATED, response.getStatusCode());
    assertNotNull("Missing response body", response.getBody());
    var complaint = (Complaint) response.getBody();
    assertEquals("wrong text", TestComplaints.TestTexts.TEXT_E, complaint.getText());
    assertEquals("wrong preview", TestComplaints.TestTexts.PREVIEW_E, complaint.getPreview());
    assertEquals("wrong date", LocalDate.now(), complaint.getReceiveDate());
    assertEquals("wrong configuration", testConfiguration, complaint.getConfiguration());
    assertEquals(complaint, complaintRepository.findById(1L).orElse(null));
  }

  @Test
  public void uploadText2() {
    // setup
    String testText = TestComplaints.TestTexts.TEXT_E;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_A.withActive(true);
    configurationRepository.save(testConfiguration);
    Configuration testConfiguration2 = TestConfigurations.CONFIGURATION_B;
    configurationRepository.save(testConfiguration2);

    // TC 3.2 using a given configuration
    var response = complaintController.uploadText(new TextInput(testText), Optional.of(2L));
    assertEquals("Wrong status code on success", HttpStatus.CREATED, response.getStatusCode());
    assertNotNull("Missing response body", response.getBody());
    var complaint = (Complaint) response.getBody();
    assertEquals("wrong text", TestComplaints.TestTexts.TEXT_E, complaint.getText());
    assertEquals("wrong preview", TestComplaints.TestTexts.PREVIEW_E, complaint.getPreview());
    assertEquals("wrong date", LocalDate.now(), complaint.getReceiveDate());
    assertEquals("wrong configuration", testConfiguration2, complaint.getConfiguration());
    assertEquals(complaint, complaintRepository.findById(1L).orElse(null));
  }

  @Test
  public void uploadText3() {
    // setup
    String testText = TestComplaints.TestTexts.TEXT_E;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_A.withActive(true);
    configurationRepository.save(testConfiguration);
    Configuration testConfiguration2 = TestConfigurations.CONFIGURATION_B;
    configurationRepository.save(testConfiguration2);

    // TC 3.1 illegal configuration
    var response = complaintController.uploadText(new TextInput(testText), Optional.of(3L));
    assertEquals("Wrong status code on success", HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull("Missing response body", response.getBody());
    var body = (NotFoundException) response.getBody();
    assertEquals("Wrong id", 3L, body.getId());
  }

  @Test
  public void getComplaint() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);
    Complaint testComplaint2 = TestComplaints.COMPLAINT_B;
    complaintRepository.save(testComplaint2);

    var response = complaintController.getComplaint(1L);
    assertEquals(testComplaint, response.getBody());
    assertEquals("wrong status code", HttpStatus.OK, response.getStatusCode());

    response = complaintController.getComplaint(2L);
    assertEquals(testComplaint2, response.getBody());
    assertEquals("wrong status code", HttpStatus.OK, response.getStatusCode());

    response = complaintController.getComplaint(3L);
    assertEquals("wrong status code", HttpStatus.NOT_FOUND, response.getStatusCode());
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