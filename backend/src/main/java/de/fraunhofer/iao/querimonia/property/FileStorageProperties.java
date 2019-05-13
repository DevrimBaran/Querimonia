package de.fraunhofer.iao.querimonia.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binding the properties of application.properties to this POJO Class.
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