package de.fraunhofer.iao.querimonia.utility;

import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Service for saving files in filesystem and retrieving them. Inspired by
 * https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/.
 */
@Service
public class FileStorageService {

  private final Path fileStorageLocation;

  /**
   * Creates a new file storage service. Only used by spring for autowired generation.
   */
  @Autowired
  public FileStorageService(FileStorageProperties fileStorageProperties) {
    String uploadDir;
    if (fileStorageProperties != null) {
      uploadDir = fileStorageProperties.getUploadDir();
      if (uploadDir == null) {
        uploadDir = "";
      }
    } else {
      uploadDir = "";
    }
    this.fileStorageLocation = Paths.get(uploadDir)
        .toAbsolutePath().normalize();

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Ordner für Datei "
          + "Uploads konnte nicht erstellt werden.", ex, "Server Fehler");
    }
  }

  /**
   * Stores the file in the upload folder.
   *
   * @param file the file to store.
   * @return the file name.
   */
  public String storeFile(MultipartFile file) {
    // Normalize file name
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());

    try {
      // Check if the file's name contains invalid characters
      if (fileName.contains("..")) {
        throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
            "Dateiname enthält ungültige Sequenz: " + fileName, "Ungültige Datei");
      }

      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = this.fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return fileName;
    } catch (IOException ex) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Datei " + fileName + "konnte nicht gespeichert werden.", ex,
          "Fehler beim Speichern");
    }
  }

  /**
   * Extracts text out of a file.
   *
   * @param fileName the name of the file
   *
   * @return the extracted complaint text.
   */
  public String getTextFromData(String fileName) {
    String fullFilePath = fileStorageLocation.toString() + File.separator + fileName;

    try (InputStream fileInputStream = new FileInputStream(fullFilePath)) {

      String text = null;
      String suffix = fullFilePath.substring(fullFilePath.lastIndexOf("."));
      switch (suffix) {
        case ".txt":
          text = Files.readString(Paths.get(fullFilePath), Charset.defaultCharset());
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
          throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Das Dateiformat " + suffix
              + " wird nicht unterstützt!", "Ungültiges Dateiformat");
      }

      if (text == null) {
        throw new IllegalStateException();
      }
      return text;
    } catch (IOException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Fehler beim Dateiupload:\n" + e.getMessage(), "Server Error");
    }
  }
}
