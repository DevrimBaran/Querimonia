package de.fraunhofer.iao.querimonia.service;

import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * Service for saving files in filesystem and retrieving them. Inpired by
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

}
