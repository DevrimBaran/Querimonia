package de.fraunhofer.iao.querimonia.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binding the properties of application.properties to this POJO Class. Inspired by
 * https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/.
 */
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
  private String uploadDir;

  public String getUploadDir() {
    return uploadDir;
  }

  public void setUploadDir(String uploadDir) {
    this.uploadDir = uploadDir;
  }
}