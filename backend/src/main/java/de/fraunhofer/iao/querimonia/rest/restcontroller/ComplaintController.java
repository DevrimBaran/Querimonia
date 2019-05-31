package de.fraunhofer.iao.querimonia.rest.restcontroller;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.db.ComplaintFactory;
import de.fraunhofer.iao.querimonia.db.ComplaintFilter;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.nlp.classifier.KIKuKoClassifier;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import sun.plugin.com.event.COMEventHandler;

/**
 * This controller manages complaint view, import and export.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ComplaintController {

  private final FileStorageService fileStorageService;
  private final ComplaintRepository complaintRepository;

  public ComplaintController(FileStorageService fileStorageService,
                             ComplaintRepository complaintRepository) {
    this.fileStorageService = fileStorageService;
    this.complaintRepository = complaintRepository;
  }

  /**
   * This endpoint is used for uploading files with complaint texts. It accepts txt, word and pdf
   * files and extracts the text from them.
   *
   * @param file the multipart file which gets transferred via http.
   * @return the generated complaint object.
   */
  @PostMapping(value = "/api/complaints/import", produces = "application/json",
      consumes = "multipart/form-data")
  public Complaint uploadComplaint(@RequestParam("file") MultipartFile file) {
    Logger logger = LoggerFactory.getLogger(getClass());

    String fileName = fileStorageService.storeFile(file);
    String fullFilePath = "src/main/resources/uploads/" + fileName;

    Complaint complaint;

    try (FileInputStream fileInputStream = new FileInputStream(fullFilePath)) {

      complaint = getComplaintFromData(fullFilePath, fileInputStream);

    } catch (IOException e) {
      logger.error("Fehler beim Datei-Upload");
      throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Lesen der"
          + " Datei.");
    }

    complaintRepository.save(complaint);
    return complaint;
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
    Logger logger = LoggerFactory.getLogger(getClass());

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
        throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "Not a supported file format");
    }

    if (text == null) {
      throw new IllegalStateException();
    }
    return new ComplaintFactory(
        new KIKuKoClassifier(), (complaintText -> new LinkedHashMap<>()),
        (complaintText -> new ArrayList<>())
    ).createComplaint(text);
  }

  /**
   * This endpoint is used to transfer text which is not given in a file.
   *
   * @param input a text input object that gets deserialized from json.
   * @return the generated complaint of the text.
   */
  @PostMapping(value = "/api/complaints/import", produces = "application/json",
      consumes = "application/json")
  public Complaint uploadText(@RequestBody TextInput input) {
    Complaint complaint = new ComplaintFactory(
        new KIKuKoClassifier(), (complaintText -> new LinkedHashMap<>()),
        (complaintText -> new ArrayList<>())
    ).createComplaint(input.getText());
    complaintRepository.save(complaint);
    return complaint;
  }

  /**
   * Returns a list of all complaints that are stored in the database.
   *
   * @return a list of all complaints that are stored in the database.
   */
  @GetMapping("/api/complaints")
  public List<Complaint> getTexts(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("sentiment") Optional<String[]> sentiment,
      @RequestParam("subject") Optional<String[]> subject,
      @RequestParam("keywords") Optional<String[]> keywords
  ) {
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
   * Returns a specific complaint with the given complaint complaintId.
   *
   * @param complaintId the if of the complaint.
   * @return the complaint with the given complaintId if it exists.
   */
  @GetMapping("/api/complaints/{complaintId}")
  public Complaint getComplaint(@PathVariable int complaintId) {
    return complaintRepository.findById(complaintId).orElse(null);
  }

  /**
   * Deletes the complaint with the given id.
   *
   * @param complaintId the id of the complaint to delete.
   */
  @DeleteMapping("/api/complaints/{complaintId}")
  public void deleteComplaint(@PathVariable int complaintId) {
    complaintRepository.deleteById(complaintId);
  }
}
