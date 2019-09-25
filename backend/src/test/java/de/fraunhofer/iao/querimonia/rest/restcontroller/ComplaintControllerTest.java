package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.complaint.TestComplaints;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.config.TestConfigurations;
import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.repository.*;
import de.fraunhofer.iao.querimonia.rest.restobjects.ComplaintUpdateRequest;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import de.fraunhofer.iao.querimonia.utility.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.apache.commons.collections4.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.fraunhofer.iao.querimonia.complaint.ComplaintState.*;
import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.*;
import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.TestTexts.ENTITIES_F;
import static de.fraunhofer.iao.querimonia.matchers.EmptyMatcher.empty;
import static de.fraunhofer.iao.querimonia.matchers.OptionalPresentMatcher.present;
import static de.fraunhofer.iao.querimonia.matchers.ResponseStatusCodeMatcher.hasStatusCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SuppressWarnings( {"OptionalGetWithoutIsPresent", "EmptyMethod"})
public class ComplaintControllerTest {

  private ComplaintController complaintController;
  private ComplaintRepository complaintRepository;
  private ResponseComponentRepository responseComponentRepository;
  private ConfigurationRepository configurationRepository;

  @Before
  public void setUp() {
    complaintRepository = new MockComplaintRepository();
    responseComponentRepository = new MockComponentRepository();
    configurationRepository = new MockConfigurationRepository();

    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir("src/test/resources/uploads/");
    var fileStorageService = new FileStorageService(fileStorageProperties);

    complaintController = new ComplaintController(new ComplaintManager(
        fileStorageService,
        complaintRepository,
        responseComponentRepository,
        new ConfigurationManager(
            configurationRepository,
            complaintRepository,
            fileStorageService
        ),
        new MockCombinationRepository()
    ));
  }

  @After
  public void tearDown() {
  }

  // TC 1
  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsEmptyParameters() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(3, complaints.size());
    assertEquals(COMPLAINT_F, complaints.get(0));
    assertEquals(COMPLAINT_H, complaints.get(1));
    assertEquals(COMPLAINT_G, complaints.get(2));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsCount() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.of(2),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(2, complaints.size());
    assertEquals(COMPLAINT_F, complaints.get(0));
    assertEquals(COMPLAINT_H, complaints.get(1));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsPage() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.of(2),
        Optional.of(1), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(1, complaints.size());
    assertEquals(COMPLAINT_G, complaints.get(0));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsSortBy() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.empty(),
        Optional.empty(), Optional.of(new String[] {"upload_date_asc"}), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(3, complaints.size());
    assertEquals(COMPLAINT_F, complaints.get(0));
    assertEquals(COMPLAINT_G, complaints.get(1));
    assertEquals(COMPLAINT_H, complaints.get(2));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsState() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.of(new String[] {"NEW"}), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(1, complaints.size());
    assertEquals(COMPLAINT_F, complaints.get(0));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsDateMin() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("2019-07-01"), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(2, complaints.size());
    assertEquals(COMPLAINT_H, complaints.get(0));
    assertEquals(COMPLAINT_G, complaints.get(1));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsDateMax() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("2019-07-30"),
        Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(2, complaints.size());
    assertEquals(COMPLAINT_F, complaints.get(0));
    assertEquals(COMPLAINT_G, complaints.get(1));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsSentiment() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.of(new String[] {"Wut"}), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(2, complaints.size());
    assertEquals(COMPLAINT_F, complaints.get(0));
    assertEquals(COMPLAINT_G, complaints.get(1));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsSubject() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.of(new String[] {"Beschwerde"}), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(2, complaints.size());
    assertEquals(COMPLAINT_F, complaints.get(0));
    assertEquals(COMPLAINT_H, complaints.get(1));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetComplaintsKeywords() {
    complaintRepository.save(COMPLAINT_F);
    complaintRepository.save(COMPLAINT_G);
    complaintRepository.save(COMPLAINT_H);
    ResponseEntity<?> responseEntity = complaintController.getComplaints(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.of(new String[] {"123"}));
    assertNotNull(responseEntity.getBody());
    List<Complaint> complaints = (List<Complaint>) responseEntity.getBody();
    assertEquals(1, complaints.size());
    assertEquals(COMPLAINT_G, complaints.get(0));
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
            testText.getBytes(Charset.forName("UTF-8"))), Optional.of(1L), Optional.empty());
    assertEquals("Wrong status code on success", HttpStatus.CREATED,
        responseEntity.getStatusCode());
    Complaint body = (Complaint) responseEntity.getBody();
    assertNotNull("Missing body", body);
    assertThat(body.getState(), is(ANALYSING));
    assertTrue("db does not contain new complaint", complaintRepository.existsById(1L));
    assertEquals("wrong complaint in db",
        complaintRepository.findById(1L).map(Complaint::getText).orElse(null),
        TestComplaints.COMPLAINT_A.getText());
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
            testText.getBytes(Charset.defaultCharset())), Optional.of(1L), Optional.empty());
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
            testText.getBytes(Charset.defaultCharset())), Optional.of(1L), Optional.empty());
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
            testText.getBytes(Charset.defaultCharset())), Optional.of(1L), Optional.empty());
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
    var response = complaintController.uploadText(new TextInput(testText), Optional.empty(),
        Optional.empty());
    assertThat(response, hasStatusCode(HttpStatus.CREATED));
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
    var response = complaintController.uploadText(new TextInput(testText), Optional.of(2L),
        Optional.empty());
    assertThat(response, hasStatusCode(HttpStatus.CREATED));
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
    var response = complaintController.uploadText(new TextInput(testText), Optional.of(3L),
        Optional.empty());
    assertThat(response, hasStatusCode(HttpStatus.NOT_FOUND));
    assertNotNull("Missing response body", response.getBody());
    var body = (NotFoundException) response.getBody();
    assertEquals("Wrong id", 3L, body.getId());
  }

  @Test(timeout = 10000)
  public void uploadText4() throws InterruptedException{
    // setup
    String testText = TestComplaints.TestTexts.TEXT_B;
    Configuration testConfiguration = TestConfigurations.CONFIGURATION_G;
    configurationRepository.save(testConfiguration);

    // TC 4.0 mock configuration
    var immediateResponse = complaintController.uploadText(new TextInput(testText),
        Optional.of(7L), Optional.empty());
    assertThat(immediateResponse, hasStatusCode(HttpStatus.CREATED));
    Optional<Complaint> analyzedComplaintOptional = complaintRepository.findById(2L);
    // Test has to wait for async analysis to finish
    // TODO: Improve this! (bad performance)
    while (analyzedComplaintOptional.isEmpty()) {
      Thread.sleep(100);
      analyzedComplaintOptional = complaintRepository.findById(2L);
    }
    Complaint analyzedComplaint = analyzedComplaintOptional.get();
    // TODO fix tests
    //assertEquals("Unbekannt", analyzedComplaint.getSubject().getValue());
    // assertEquals(List.of(TestEntities.ENTITY_A, TestEntities.ENTITY_B), analyzedComplaint.getEntities());
    // assertEquals(0.5, analyzedComplaint.getSentiment().getTendency(), 0.1);
    // assertEquals("Wut", analyzedComplaint.getSentiment().getEmotion().getValue());
  }

  @Test
  public void getComplaint() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);
    Complaint testComplaint2 = TestComplaints.COMPLAINT_B;
    complaintRepository.save(testComplaint2);

    ResponseEntity<?> response = complaintController.getComplaint(1L);
    assertEquals(testComplaint, response.getBody());
    assertThat(response, hasStatusCode(HttpStatus.OK));

    response = complaintController.getComplaint(2L);
    assertEquals(testComplaint2, response.getBody());
    assertThat(response, hasStatusCode(HttpStatus.OK));

    response = complaintController.getComplaint(3L);
    assertThat(response, hasStatusCode(HttpStatus.NOT_FOUND));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, is(instanceOf(NotFoundException.class)));
  }

  @Test
  public void updateComplaint1() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);
    ComplaintUpdateRequest complaintUpdateRequest = new ComplaintUpdateRequest(null, null, null, null);

    var response =
        complaintController.updateComplaint(testComplaint.getId(), complaintUpdateRequest);
    assertThat(response, hasStatusCode(HttpStatus.OK));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, is(equalTo(testComplaint)));
  }

  @Test
  public void updateComplaint2() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);
    ComplaintUpdateRequest complaintUpdateRequest = new ComplaintUpdateRequest("Insanity", null,
        null, null);

    var response =
        complaintController.updateComplaint(testComplaint.getId(), complaintUpdateRequest);
    assertThat(response, hasStatusCode(HttpStatus.OK));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, not(equalTo(testComplaint)));
    assertThat(body, is(instanceOf(Complaint.class)));
    var complaint = (Complaint) body;
    assertThat(complaint.getSentiment().getEmotion(),
        is(equalTo(new ComplaintProperty("Emotion", "Insanity"))));
    assertThat(complaint.getSentiment().getEmotion().isSetByUser(), is(true));
    assertThat(complaint.getLog(), is(not(empty())));
    assertThat(body, is(equalTo(complaintRepository.findById(1L).orElse(null))));
  }

  @Test
  public void updateComplaint3() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);
    ComplaintUpdateRequest complaintUpdateRequest = new ComplaintUpdateRequest(null, 0.5,null,
        null);

    var response =
        complaintController.updateComplaint(testComplaint.getId(), complaintUpdateRequest);
    assertThat(response, hasStatusCode(HttpStatus.OK));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, not(equalTo(testComplaint)));
    assertThat(body, is(instanceOf(Complaint.class)));
    var complaint = (Complaint) body;
    assertThat(complaint.getSentiment().getTendency(),
        is(equalTo(0.5)));
    assertThat(complaint.getSentiment().getEmotion().isSetByUser(), is(true));
    assertThat(complaint.getLog(), is(not(empty())));
    assertThat(body, is(equalTo(complaintRepository.findById(1L).orElse(null))));
  }

  @Test
  public void updateComplaint4() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);
    ComplaintUpdateRequest complaintUpdateRequest = new ComplaintUpdateRequest(null, null,"Bad driver",
        null);

    var response =
        complaintController.updateComplaint(testComplaint.getId(), complaintUpdateRequest);
    assertThat(response, hasStatusCode(HttpStatus.OK));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, not(equalTo(testComplaint)));
    assertThat(body, is(instanceOf(Complaint.class)));
    var complaint = (Complaint) body;
    assertThat(complaint.getSubject().isSetByUser(), is(true));
    assertThat(complaint.getSubject().getValue(), is(equalTo("Bad driver")));
    assertThat(body, is(equalTo(complaintRepository.findById(1L).orElse(null))));
  }

  @Test
  public void updateComplaint5() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);
    ComplaintUpdateRequest complaintUpdateRequest = new ComplaintUpdateRequest(null, null, null,
        CLOSED);

    var response =
        complaintController.updateComplaint(testComplaint.getId(), complaintUpdateRequest);
    assertThat(response, hasStatusCode(HttpStatus.OK));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, not(equalTo(testComplaint)));
    assertThat(body, is(instanceOf(Complaint.class)));
    var complaint = (Complaint) body;
    assertThat(complaint.getState(), is(CLOSED));
    var repositoryComplaint = complaintRepository.findById(1L).orElse(null);
    assertThat(body, is(equalTo(repositoryComplaint)));
  }

  @Test
  public void updateComplaint6() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    ComplaintUpdateRequest complaintUpdateRequest = new ComplaintUpdateRequest("Anger", null, null,
        CLOSED);

    var response =
        complaintController.updateComplaint(testComplaint.getId(), complaintUpdateRequest);
    assertThat(response, hasStatusCode(HttpStatus.NOT_FOUND));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, is(instanceOf(NotFoundException.class)));
  }

  @Test
  public void updateComplaint7() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A.withState(CLOSED);
    complaintRepository.save(testComplaint);
    ComplaintUpdateRequest complaintUpdateRequest = new ComplaintUpdateRequest(null, null, null, NEW);

    var response =
        complaintController.updateComplaint(testComplaint.getId(), complaintUpdateRequest);
    assertThat(response, hasStatusCode(HttpStatus.BAD_REQUEST));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, is(instanceOf(QuerimoniaException.class)));
  }

  @Test
  public void deleteComplaint1() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);

    var response = complaintController.deleteComplaint(2L);
    assertThat(response, hasStatusCode(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), is(notNullValue()));
    assertThat(response.getBody(), is(instanceOf(NotFoundException.class)));

    response = complaintController.deleteComplaint(testComplaint.getId());
    assertThat(response, hasStatusCode(HttpStatus.NO_CONTENT));
    assertThat(response.getBody(), is(nullValue()));
    assertThat(complaintRepository.findAll(), is(empty()));

    response = complaintController.deleteComplaint(1L);
    assertThat(response, hasStatusCode(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), is(notNullValue()));
    assertThat(response.getBody(), is(instanceOf(NotFoundException.class)));
  }

  @Test
  public void deleteComplaint2() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);
    complaintRepository.save(TestComplaints.COMPLAINT_B);

    var response = complaintController.deleteComplaint(2L);
    assertThat(response, hasStatusCode(HttpStatus.NO_CONTENT));
    assertThat(response.getBody(), is(nullValue()));

    List<Complaint> complaints = IteratorUtils.toList(complaintRepository.findAll().iterator(), 1);
    assertThat(complaints, hasSize(1));
    assertThat(complaints.get(0), is(equalTo(testComplaint)));
  }

  @Test
  public void refreshComplaint() {
  }

  @Test
  public void closeComplaint() {
    Complaint testComplaint = TestComplaints.COMPLAINT_A;
    complaintRepository.save(testComplaint);

    var response = complaintController.closeComplaint(1L);
    assertThat(response, hasStatusCode(HttpStatus.OK));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, is(instanceOf(Complaint.class)));
    var complaint = (Complaint) body;
    assertThat(complaint.getState(), is(CLOSED));
    assertThat(complaintRepository.findById(1L), is(present()));
    assertThat(complaintRepository.findById(1L).get().getState(), is(CLOSED));
  }

  @Test
  public void closeComplaint2() {
    // complaint already closed
    Complaint testComplaint = TestComplaints.COMPLAINT_A.withState(CLOSED);
    complaintRepository.save(testComplaint);

    var response = complaintController.closeComplaint(1L);
    assertThat(response, hasStatusCode(HttpStatus.BAD_REQUEST));
    var body = response.getBody();
    assertThat(body, is(notNullValue()));
    assertThat(body, is(instanceOf(QuerimoniaException.class)));
  }

  @Test
  public void countComplaints1() {
    // empty repository
    var response = complaintController.countComplaints(Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    assertThat(response, hasStatusCode(HttpStatus.OK));
    assertThat(response.getBody(), is("0"));

    response = complaintController.countComplaints(Optional.of(new String[] {"CLOSED"}),
        Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    assertThat(response, hasStatusCode(HttpStatus.OK));
    assertThat(response.getBody(), is("0"));

    response = complaintController.countComplaints(Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.of(new String[] {"Anger"}), Optional.empty(), Optional.empty());
    assertThat(response, hasStatusCode(HttpStatus.OK));
    assertThat(response.getBody(), is("0"));

    response = complaintController.countComplaints(Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    assertThat(response, hasStatusCode(HttpStatus.OK));
    assertThat(response.getBody(), is("0"));
  }

  @Test
  public void getEntities1() {
    // empty entity list
    var testComplaint = new ComplaintBuilder(TestComplaints.COMPLAINT_A)
        .setEntities(Collections.emptyList())
        .createComplaint();
    complaintRepository.save(testComplaint);

    var response = complaintController.getEntities(testComplaint.getId());
    assertThat(response, hasStatusCode(HttpStatus.OK));
    assertThat(response.getBody(), is(notNullValue()));
    assertThat(response.getBody(), is(instanceOf(List.class)));
    assertThat(((List) response.getBody()).size(), is(0));
  }
  @Test
  public void getEntities2() {
    // non empty entity list
    var testComplaint = TestComplaints.COMPLAINT_B;
    complaintRepository.save(testComplaint);

    var response = complaintController.getEntities(testComplaint.getId());
    assertThat(response, hasStatusCode(HttpStatus.OK));
    assertThat(response.getBody(), is(notNullValue()));
    assertThat(response.getBody(), is(instanceOf(List.class)));
    assertThat((List) response.getBody(), is(not(empty())));
    // assertThat(response.getBody(), is(equalTo(testComplaint.getEntities())));
  }

  @Test
  public void getEntities3() {
    var response = complaintController.getEntities(5L);
    assertThat(response, hasStatusCode(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), is(notNullValue()));
    assertThat(response.getBody(), is(instanceOf(NotFoundException.class)));
  }

  @Test
  public void testAddEntity() {
    NamedEntity testEntity = new NamedEntityBuilder()
        .setLabel("Test")
        .setStart(50)
        .setEnd(55)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("test")
        .createNamedEntity();
    List<NamedEntity> testEntities = new ArrayList<>(ENTITIES_F);
    testEntities.add(testEntity);
    Complaint testComplaint = new ComplaintBuilder(COMPLAINT_F)
        .setEntities(testEntities)
        .createComplaint();
    complaintRepository.save(testComplaint);
    complaintController.addEntity(6L, testEntity);
    List<NamedEntity> entities = complaintRepository.findById(6L).get().getEntities();
    assertEquals(entities, testEntities);
  }

  @Test
  public void testAddEntityNegativeStart() {
    NamedEntity testEntity = new NamedEntityBuilder()
        .setLabel("Test")
        .setStart(-50)
        .setEnd(55)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("test")
        .createNamedEntity();
    Complaint testComplaint = new ComplaintBuilder(COMPLAINT_F).createComplaint();
    complaintRepository.save(testComplaint);
    ResponseEntity<?> responseEntity = complaintController.addEntity(6L, testEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(QuerimoniaException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testAddEntityNegativeLength() {
    NamedEntity testEntity = new NamedEntityBuilder()
        .setLabel("Test")
        .setStart(50)
        .setEnd(5)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("test")
        .createNamedEntity();
    Complaint testComplaint = new ComplaintBuilder(COMPLAINT_F).createComplaint();
    complaintRepository.save(testComplaint);
    ResponseEntity<?> responseEntity = complaintController.addEntity(6L, testEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(QuerimoniaException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testAddEntityEndTooBig() {
    NamedEntity testEntity = new NamedEntityBuilder()
        .setLabel("Test")
        .setStart(50)
        .setEnd(500)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("test")
        .createNamedEntity();
    Complaint testComplaint = new ComplaintBuilder(COMPLAINT_F).createComplaint();
    complaintRepository.save(testComplaint);
    ResponseEntity<?> responseEntity = complaintController.addEntity(6L, testEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(QuerimoniaException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testAddEntityEndExistingLabel() {
    NamedEntity testEntity = new NamedEntityBuilder()
        .setLabel("Name")
        .setStart(15)
        .setEnd(19)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("Peter")
        .createNamedEntity();
    Complaint testComplaint = new ComplaintBuilder(COMPLAINT_F).createComplaint();
    complaintRepository.save(testComplaint);
    ResponseEntity<?> responseEntity = complaintController.addEntity(6L, testEntity);
    assertNotNull(responseEntity.getBody());
    assertEquals(QuerimoniaException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testRemoveEntity() {
    NamedEntity testEntity = new NamedEntityBuilder()
        .setLabel("Name")
        .setStart(15)
        .setEnd(19)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("Peter")
        .createNamedEntity();
    Complaint testComplaint = new ComplaintBuilder(COMPLAINT_F).createComplaint();
    complaintRepository.save(testComplaint);
    complaintController.removeEntity(6L, 0);
    List<NamedEntity> entities = complaintRepository.findById(6L).get().getEntities();
    assertFalse(entities.contains(testEntity));
  }

  @Test
  public void testRemoveEntityInvalidEntity() {
    Complaint testComplaint = new ComplaintBuilder(COMPLAINT_G).createComplaint();
    complaintRepository.save(testComplaint);
    ResponseEntity<?> responseEntity = complaintController.removeEntity(6L, 0);
    assertNotNull(responseEntity.getBody());
    assertEquals(NotFoundException.class, responseEntity.getBody().getClass());
  }


  @Test
  public void deleteAllComplaints1() {
    var response = complaintController.deleteAllComplaints();
    assertThat(response, hasStatusCode(HttpStatus.NO_CONTENT));
    assertThat(complaintRepository.findAll(), is(empty()));
  }

  @Test
  public void deleteAllComplaints2() {
    complaintRepository.save(TestComplaints.COMPLAINT_A);
    complaintRepository.save(TestComplaints.COMPLAINT_B);

    var response = complaintController.deleteAllComplaints();
    assertThat(response, hasStatusCode(HttpStatus.NO_CONTENT));
    assertThat(complaintRepository.findAll(), is(empty()));
  }
}