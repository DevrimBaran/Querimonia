package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.db.ComplaintFactory;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * This controller manages complaint view, import and export.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ComplaintController {

  @Autowired
  FileStorageService fileStorageService;
  @Autowired
  ResponseRepository responseRepository;
  @Autowired
  TemplateRepository templateRepository;
  @Autowired
  private ComplaintRepository complaintRepository;

  /**
   * This endpoint is used for uploading files with complaint texts. It accepts txt, word and pdf
   * files and extracts the text from them.
   *
   * @param file the multipart file which gets transferred via http.
   * @return the generated complaint object.
   */
  @PostMapping("/api/import/file")
  public Complaint uploadComplaint(@RequestParam("file") MultipartFile file) {
    Logger logger = LoggerFactory.getLogger(getClass());

    String fileName = fileStorageService.storeFile(file);
    String fullFilePath = "src/main/resources/uploads/" + fileName;

    Complaint complaint;

    try (FileReader reader = new FileReader(fullFilePath)) {
      FileInputStream fileInputStream = new FileInputStream(fullFilePath);

      complaint = getComplaintFromData(reader, fullFilePath, fileInputStream);

      fileInputStream.close();
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
   * @param reader          the fileReader that opened the file
   * @param fullFilePath    the file path
   * @param fileInputStream a input stream of that file
   * @return the extracted complaint.
   * @throws IOException on an io-error.
   */
  private Complaint getComplaintFromData(FileReader reader, String fullFilePath,
                                         InputStream fileInputStream)
      throws IOException {
    Logger logger = LoggerFactory.getLogger(getClass());

    String text = null;
    switch (fullFilePath.substring(fullFilePath.lastIndexOf("."))) {
      case ".txt":
        BufferedReader bufferedReader = new BufferedReader(reader);
        text = bufferedReader.lines().collect(Collectors.joining("\n"));
        //read a pdf file
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

    return ComplaintFactory.createComplaint(text);
  }

  /**
   * This endpoint is used to transfer text which is not given in a file.
   *
   * @param input a text input object that gets deserialized from json.
   * @return the generated complaint of the text.
   */
  @PostMapping("/api/import/text")
  public Complaint uploadText(@RequestBody TextInput input) {
    Complaint complaint = ComplaintFactory.createComplaint(input.getText());
    complaintRepository.save(complaint);
    return complaint;
  }

  /**
   * Returns a list of all complaints that are stored in the database.
   *
   * @return a list of all complaints that are stored in the database.
   */
  @GetMapping("/api/complaints")
  public List<Complaint> getTexts() {
    ArrayList<Complaint> result = new ArrayList<>();
    complaintRepository.findAll().forEach(result::add);

    return result;
  }

  /**
   * Returns a specific complaint with the given complaint id.
   *
   * @param id the if of the complaint.
   * @return the complaint with the given id if it exists.
   */
  @GetMapping("/api/complaints/{id}")
  public Complaint getComplaint(@PathVariable int id) {
    return complaintRepository.findById(id).orElse(null);
  }

  /**
   * Deletes the complaint with the given id.
   *
   * @param id the id of the complaint to delete.
   */
  @DeleteMapping("/api/complaints/{id}")
  public void deleteComplaint(@PathVariable int id) {
    complaintRepository.deleteById(id);
  }
}
