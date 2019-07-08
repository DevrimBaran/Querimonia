package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintFactory;
import de.fraunhofer.iao.querimonia.db.repositories.ActionRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.CompletedResponseComponentRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.analyze.TokenAnalyzer;
import de.fraunhofer.iao.querimonia.nlp.classifier.KiKuKoClassifier;
import de.fraunhofer.iao.querimonia.nlp.extractor.KikukoExtractor;
import de.fraunhofer.iao.querimonia.response.generation.DefaultResponseGenerator;
import de.fraunhofer.iao.querimonia.nlp.sentiment.FlaskSentiment;
import de.fraunhofer.iao.querimonia.rest.manager.filter.ComplaintFilter;
import de.fraunhofer.iao.querimonia.rest.restcontroller.ComplaintController;
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
import org.springframework.web.server.ResponseStatusException;

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
  private ComplaintFactory complaintFactory;

  /**
   * Constructor gets only called by spring. Sets up the complaint factory.
   */
  public ComplaintManager(FileStorageService fileStorageService,
                          ComplaintRepository complaintRepository,
                          TemplateRepository templateRepository,
                          ActionRepository actionRepository,
                          CompletedResponseComponentRepository
                              completedResponseComponentRepository) {
    this.fileStorageService = fileStorageService;
    this.complaintRepository = complaintRepository;
    this.completedResponseComponentRepository = completedResponseComponentRepository;

    complaintFactory = new ComplaintFactory()
        .setClassifier(new KiKuKoClassifier())
        .setEntityExtractor(new KikukoExtractor())
        .setResponseGenerator(new DefaultResponseGenerator(templateRepository, actionRepository))
        .setStopWordFilter(new TokenAnalyzer())
        .setSentimentAnalyzer(new FlaskSentiment());
  }

  /**
   * Extracts text out of a file.
   *
   * @param fullFilePath    the file path
   * @param fileInputStream a input stream of that file
   * @return the extracted complaint.
   * @throws IOException on an io-error.
   */
  private Complaint getComplaintFromData(String fullFilePath,
                                         InputStream fileInputStream)
      throws IOException {

    String text = null;
    switch (fullFilePath.substring(fullFilePath.lastIndexOf("."))) {
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
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a supported file format");
    }

    if (text == null) {
      throw new IllegalStateException();
    }
    return complaintFactory.createComplaint(text);
  }

  /**
   * Returns the complaints of the database with filtering and sorting.
   *
   * @see ComplaintController#getComplaints(Optional, Optional, Optional, Optional, Optional,
   * Optional, Optional, Optional, Optional) getComplaints
   */
  // TODO filter by state
  public List<Complaint> getComplaints(
      Optional<Integer> count, Optional<Integer> page, Optional<String[]> sortBy,
      Optional<String> state, Optional<String> dateMin, Optional<String> dateMax,
      Optional<String[]> sentiment, Optional<String[]> subject, Optional<String[]> keywords) {

    ArrayList<Complaint> result = new ArrayList<>();
    complaintRepository.findAll().forEach(result::add);

    Stream<Complaint> filteredResult =
        result.stream()
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
   *
   */
  public Complaint uploadComplaint(MultipartFile file, Optional<Integer> configId) {
    String fileName = fileStorageService.storeFile(file);
    String fullFilePath = "src/main/resources/uploads/" + fileName;

    Complaint complaint;

    try (FileInputStream fileInputStream = new FileInputStream(fullFilePath)) {

      complaint = getComplaintFromData(fullFilePath, fileInputStream);

    } catch (IOException e) {
      logger.error("Fehler beim Datei-Upload");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Fehler beim Dateiupload\n" + e.getMessage());
    }

    complaintRepository.save(complaint);
    logger.info("Added complaint with id {}", complaint.getComplaintId());
    return complaint;
  }
}
