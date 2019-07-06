package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.complaint.ComplaintFactory;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.CompletedResponseComponentRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.analyze.TokenAnalyzer;
import de.fraunhofer.iao.querimonia.nlp.classifier.KiKuKoClassifier;
import de.fraunhofer.iao.querimonia.nlp.extractor.KikukoExtractor;
import de.fraunhofer.iao.querimonia.nlp.sentiment.FlaskSentiment;
import de.fraunhofer.iao.querimonia.response.generation.DefaultResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.rest.manager.filter.ComplaintFilter;
import de.fraunhofer.iao.querimonia.rest.restcontroller.ComplaintController;
import de.fraunhofer.iao.querimonia.rest.restobjects.ComplaintUpdateRequest;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manager class for complaints.
 */
public class ComplaintManager {

  private static final Logger logger = LoggerFactory.getLogger(ComplaintManager.class);
  private final FileStorageService fileStorageService;
  private final ComplaintRepository complaintRepository;
  private final CompletedResponseComponentRepository completedResponseComponentRepository;
  private final ComplaintFactory complaintFactory;

  /**
   * Constructor gets only called by spring. Sets up the complaint factory.
   */
  public ComplaintManager(FileStorageService fileStorageService,
                          ComplaintRepository complaintRepository,
                          TemplateRepository templateRepository,
                          CompletedResponseComponentRepository
                              completedResponseComponentRepository) {
    this.fileStorageService = fileStorageService;
    this.complaintRepository = complaintRepository;
    this.completedResponseComponentRepository = completedResponseComponentRepository;

    complaintFactory = new ComplaintFactory()
        .setClassifier(new KiKuKoClassifier())
        .setEntityExtractor(new KikukoExtractor())
        .setResponseGenerator(new DefaultResponseGenerator(templateRepository))
        .setStopWordFilter(new TokenAnalyzer())
        .setSentimentAnalyzer(new FlaskSentiment());
  }

  private static QuerimoniaException getNotFoundException(int complaintId) {
    return new QuerimoniaException(HttpStatus.NOT_FOUND, "Es existiert keine Beschwerde mit der "
        + "ID " + complaintId, "Ungültige ID");
  }

  /**
   * Returns the complaints of the database with filtering and sorting.
   *
   * @see ComplaintController#getComplaints(Optional, Optional, Optional, Optional, Optional,
   * Optional, Optional, Optional, Optional) getComplaints
   */
  public synchronized List<Complaint> getComplaints(
      Optional<Integer> count, Optional<Integer> page, Optional<String[]> sortBy,
      Optional<String[]> state, Optional<String> dateMin, Optional<String> dateMax,
      Optional<String[]> sentiment, Optional<String[]> subject, Optional<String[]> keywords) {

    ArrayList<Complaint> result = new ArrayList<>();
    complaintRepository.findAll().forEach(result::add);

    Stream<Complaint> filteredResult =
        result.stream()
            .filter(complaint -> ComplaintFilter.filterByState(complaint, state))
            .filter(complaint -> ComplaintFilter.filterByDate(complaint, dateMin, dateMax))
            .filter(complaint -> ComplaintFilter.filterBySentiment(complaint, sentiment))
            .filter(complaint -> ComplaintFilter.filterBySubject(complaint, subject))
            .filter(complaint -> ComplaintFilter.filterByKeywords(complaint, keywords))
            .sorted(ComplaintFilter.createComplaintComparator(sortBy));

    if (count.isPresent()) {
      if (page.isPresent()) {
        // skip pages
        filteredResult = filteredResult
            .skip(page.get() * count.get());
      }
      // only take count amount of entries
      filteredResult = filteredResult.limit(count.get());
    }

    return filteredResult.collect(Collectors.toList());
  }

  /**
   * Upload method for complaints from files.
   *
   * @see ComplaintController#uploadComplaint(MultipartFile, Optional) uploadComplaint
   */
  public synchronized Complaint uploadComplaint(MultipartFile file, Optional<Integer> configId) {
    String fileName = fileStorageService.storeFile(file);
    String fullFilePath = "src/main/resources/uploads/" + fileName;

    Complaint complaint;

    try (FileInputStream fileInputStream = new FileInputStream(fullFilePath)) {

      String text = getTextFromData(fullFilePath, fileInputStream);
      complaint = uploadText(new TextInput(text), configId);

    } catch (IOException e) {
      logger.error("Fehler beim Datei-Upload");
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Fehler beim Dateiupload:\n" + e.getMessage(), "Server Error");
    }
    return complaint;
  }

  /**
   * Methods for uploading raw text.
   *
   * @see ComplaintController#uploadText(TextInput, Optional) uploadText
   */
  public Complaint uploadText(TextInput input, Optional<Integer> configId) {
    // TODO use config id
    Complaint complaint = complaintFactory.createComplaint(input.getText());
    // save the components
    storeComplaint(complaint);
    return complaint;
  }

  /**
   * Method for getting a complaint with an id.
   *
   * @see ComplaintController#getComplaint(int) getComplaint
   */
  public synchronized Complaint getComplaint(int complaintId) {
    return complaintRepository.findById(complaintId)
        .orElseThrow(() -> getNotFoundException(complaintId));
  }

  /**
   * Method for updating complaints.
   *
   * @see ComplaintController#updateComplaint(int, ComplaintUpdateRequest) updateComplaint
   */
  public Complaint updateComplaint(int complaintId, ComplaintUpdateRequest updateRequest) {
    Complaint complaint = getComplaint(complaintId);
    checkState(complaint);

    updateRequest.getNewSentiment()
        .ifPresent(sentiment -> complaint.getSentiment().setValue(sentiment));
    updateRequest.getNewSubject()
        .ifPresent(subject -> complaint.getSubject().setValue(subject));
    updateRequest.getNewState()
        .ifPresent(complaint::setState);

    storeComplaint(complaint);
    return complaint;
  }

  /**
   * Deletes a complaint with the given id.
   *
   * @see ComplaintController#deleteComplaint(int) deleteComplaint
   */
  public synchronized void deleteComplaint(int complaintId) {
    if (complaintRepository.existsById(complaintId)) {
      complaintRepository.deleteById(complaintId);
      logger.info("Deleted complaint with id {}", complaintId);
    } else {
      throw getNotFoundException(complaintId);
    }
  }

  /**
   * Reanalyzes a complaint.
   *
   * @see ComplaintController#refreshComplaint(int, Optional, Optional) refreshComplaint
   */
  public synchronized Complaint refreshComplaint(
      int complaintId,
      Optional<Boolean> keepUserInformation,
      Optional<Integer> configId) {
    Complaint complaint = getComplaint(complaintId);
    checkState(complaint);
    // TODO work with config id
    complaint = complaintFactory.analyzeComplaint(complaint, keepUserInformation.orElse(false));
    storeComplaint(complaint);
    return complaint;
  }

  /**
   * Counts the complaints.
   *
   * @see ComplaintController#countComplaints(Optional, Optional, Optional, Optional, Optional,
   * Optional)  countComplaints
   */
  public synchronized int countComplaints(Optional<String[]> state, Optional<String> dateMin,
                                          Optional<String> dateMax, Optional<String[]> sentiment,
                                          Optional<String[]> subject, Optional<String[]> keywords
  ) {
    return getComplaints(Optional.empty(), Optional.empty(), Optional.empty(), state, dateMin,
        dateMax, sentiment, subject, keywords).size();
  }

  /**
   * Adds a named entity to a complaint.
   *
   * @see ComplaintController#addEntity(int, String, int, int, String) addEntity
   */
  public List<NamedEntity> addEntity(int complaintId, String label, int start,
                                     int end, String extractor) {
    Complaint complaint = getComplaint(complaintId);
    NamedEntity newEntity = new NamedEntity(label, start, end, true);

    List<NamedEntity> complaintEntities = complaint.getEntities();
    if (!complaintEntities.contains(newEntity)) {
      complaintEntities.add(newEntity);
    } else {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Die Entität mit Label " + label
          + "existiert bereits!", "Entität bereits vorhanden");
    }
    // TODO check entities ranges
    storeComplaint(complaint);
    return complaintEntities;
  }

  /**
   * Removes an entity in the database.
   *
   * @see ComplaintController#removeEntity(int, String, int, int, String) removeEntity
   */
  public List<NamedEntity> removeEntity(int complaintId, String label, int start,
                                        int end, String extractor) {
    Complaint complaint = getComplaint(complaintId);
    NamedEntity newEntity = new NamedEntity(label, start, end);

    List<NamedEntity> complaintEntities = complaint.getEntities();
    boolean removed = complaintEntities.remove(newEntity);
    if (!removed) {
      throw new QuerimoniaException(HttpStatus.NOT_FOUND, "Die gegebene Entität existiert nicht "
          + "in der Beschwerde.", "Ungültige Entity");
    }
    // TODO check entities ranges
    storeComplaint(complaint);
    return complaintEntities;
  }

  /**
   * Removes all complaints.
   *
   * @see ComplaintController#deleteAllComplaints() deleteAllComplaints
   */
  public void deleteAllComplaints() {
    complaintRepository.deleteAll();
    completedResponseComponentRepository.deleteAll();
  }

  /**
   * Refreshed the response for a complaint.
   */
  public ResponseSuggestion refreshResponse(int complaintId) {
    Complaint complaint = getComplaint(complaintId);
    ResponseSuggestion suggestion = complaintFactory.createResponse(new ComplaintData(complaint));
    storeComplaint(complaint);
    return suggestion;
  }

  private synchronized void storeComplaint(Complaint complaint) {
    // save the components
    complaint.getResponseSuggestion()
        .getResponseComponents()
        .forEach(completedResponseComponentRepository::save);
    logger.info("Saved complaint with id {}", complaint.getComplaintId());
    complaintRepository.save(complaint);
  }

  private void checkState(Complaint complaint) {
    if (complaint.getState().equals(ComplaintState.CLOSED)) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Beschwerde kann nicht bearbeitet "
          + "werden, da sie bereits geschlossen ist.", "Beschwerde geschlossen");
    }
  }

  /**
   * Extracts text out of a file.
   *
   * @param fullFilePath    the file path
   * @param fileInputStream a input stream of that file
   * @return the extracted complaint text.
   * @throws IOException on an io-error.
   */
  private String getTextFromData(String fullFilePath, InputStream fileInputStream)
      throws IOException {

    String text = null;
    String suffix = fullFilePath.substring(fullFilePath.lastIndexOf("."));
    switch (suffix) {
      case ".txt":
        text = new String(Files.readAllBytes(Paths.get(fullFilePath)), Charset.defaultCharset());
        break;
      case ".pdf":
        PDDocument document = PDDocument.load(new File(fullFilePath));
        if (!document.isEncrypted()) {
          PDFTextStripper stripper = new PDFTextStripper();
          text = stripper.getText(document);
        }
        document.close();
        break;
      //read a word file (docx)
      case ".docx":
        XWPFDocument docxDocument = new XWPFDocument(fileInputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(docxDocument);
        text = extractor.getText();
        extractor.close();
        break;
      //read word file (doc)
      case ".doc":
        HWPFDocument docDocument = new HWPFDocument(fileInputStream);
        WordExtractor docExtractor = new WordExtractor(docDocument);
        text = docExtractor.getText();
        docExtractor.close();
        break;
      default:
        logger.error("Not a supported file format");
        throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Das Dateiformat " + suffix
            + " wird nicht unterstützt!", "Ungültiges Dateiformat");
    }

    if (text == null) {
      throw new IllegalStateException();
    }
    return text;
  }
}