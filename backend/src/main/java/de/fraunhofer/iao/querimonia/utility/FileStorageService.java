package de.fraunhofer.iao.querimonia.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
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
import java.util.Arrays;
import java.util.List;

/**
 * Service for saving files in filesystem and retrieving them. Inspired by
 * https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/.
 */
@Service
public class FileStorageService {

  public static final String JSON_ERROR_TEXT =
      "Die Default-Elemente konnten nicht geladen werden.";
  private final Path fileStorageLocation;

  /**
   * Creates a new file storage service. Only used by spring for autowired generation.
   *
   * @param fileStorageProperties the properties that contain the file location.
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
   *
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
          System.out.println(Charset.defaultCharset().displayName());
          text = Files.readString(Paths.get(fullFilePath), Charset.forName("UTF-8"));
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

  /**
   * Loads an json array from a file.
   *
   * @param valueType the array type for the deserialization.
   * @param filename  the relative path to the file.
   * @param <T>       the type of the elements of the array.
   *
   * @return a list of type T containing the elements of the JSON array of the file.
   *
   * @throws QuerimoniaException on an IO-Error or a JSON-Parse-Error.
   */
  @NonNull
  public <T> List<T> getJsonObjectsFromFile(Class<T[]> valueType, String filename) {
    List<T> defaultComponents;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
      Resource defaultComponentsResource = defaultResourceLoader
          .getResource(filename);

      if (!defaultComponentsResource.exists()) {
        throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
            JSON_ERROR_TEXT, "Fehlende Datei");
      }

      InputStream defaultComponentsStream = defaultComponentsResource.getInputStream();
      defaultComponents = Arrays.asList(objectMapper.readValue(
          defaultComponentsStream, valueType));

    } catch (IOException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
          JSON_ERROR_TEXT, e, "Ungültige JSON Datei");
    }
    return defaultComponents;
  }
}
