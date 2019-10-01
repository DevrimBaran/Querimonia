package de.fraunhofer.iao.querimonia.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glaforge.i18n.io.CharsetToolkit;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for saving files in filesystem and retrieving them. Inspired by
 * https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/.
 */
@Service
public class FileStorageService {

  /**
   * Gets the encoding of the Input txt file.
   * @param input Is the input txt file.
   * @return Returns the encoding of the file.
   * @throws IOException May be thrown because of inputstream.
   */
  private static String guessEncoding(InputStream input) throws IOException {
    // Load input data
    long count = 0;
    int n = 0;
    int eof = -1;
    byte[] buffer = new byte[4096];
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    while ((eof != (n = input.read(buffer))) && (count <= Integer.MAX_VALUE)) {
      output.write(buffer, 0, n);
      count += n;
    }

    if (count > Integer.MAX_VALUE) {
      throw new RuntimeException("Inputstream too large.");
    }

    byte[] data = output.toByteArray();

    // Detect encoding
    Map<String, int[]> encodingsScores = new HashMap<>();

    // * GuessEncoding
    updateEncodingsScores(encodingsScores, new CharsetToolkit(data).guessEncoding().displayName());

    // * ICU4j
    CharsetDetector charsetDetector = new CharsetDetector();
    charsetDetector.setText(data);
    charsetDetector.enableInputFilter(true);
    CharsetMatch cm = charsetDetector.detect();
    if (cm != null) {
      updateEncodingsScores(encodingsScores, cm.getName());
    }

    // * juniversalchardset
    UniversalDetector universalDetector = new UniversalDetector(null);
    universalDetector.handleData(data, 0, data.length);
    universalDetector.dataEnd();
    String encodingName = universalDetector.getDetectedCharset();
    if (encodingName != null) {
      updateEncodingsScores(encodingsScores, encodingName);
    }

    // Find winning encoding
    Map.Entry<String, int[]> maxEntry = null;
    for (Map.Entry<String, int[]> e : encodingsScores.entrySet()) {
      if (maxEntry == null || (e.getValue()[0] > maxEntry.getValue()[0])) {
        maxEntry = e;
      }
    }
    return maxEntry.getKey();
  }

  /**
   * Update the score if encoding can read character.
   * @param encodingsScores Ther score of the encoding.
   * @param encoding Is the encoding which is used.
   */
  private static void updateEncodingsScores(Map<String, int[]> encodingsScores, String encoding) {
    String encodingName = encoding.toLowerCase();
    int[] encodingScore = encodingsScores.get(encodingName);

    if (encodingScore == null) {
      encodingsScores.put(encodingName, new int[] {1});
    } else {
      encodingScore[0]++;
    }
  }

  private static final String JSON_ERROR_TEXT =
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
   * Uploads the file.
   *
   * @param file the file to upload.
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
   * @return the extracted complaint text.
   */
  public String getTextFromData(String fileName) {
    String fullFilePath = fileStorageLocation.toString() + File.separator + fileName;

    try (InputStream fileInputStream = new FileInputStream(fullFilePath)) {

      String text = null;
      String suffix = fullFilePath.substring(fullFilePath.lastIndexOf("."));
      switch (suffix) {
        //read a txt file.
        case ".txt":
          InputStream inputStream = new FileInputStream(fullFilePath);
          String encoding = guessEncoding(inputStream);
          BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
              fullFilePath), Charset.forName(encoding)));
          String readText;
          while ((readText = br.readLine()) != null) {
            text = readText;
          }
          break;
        //read a pdf file.
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
          "Fehler beim Dateiupload:\n" + e.getMessage(), e, "Server Error");
    }
  }

  /**
   * Loads an json array from a file.
   *
   * @param valueType the array type for the deserialization.
   * @param filename  the relative path to the file.
   * @param <T>       the type of the elements of the array.
   * @return a list of type T containing the elements of the JSON array of the file.
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
