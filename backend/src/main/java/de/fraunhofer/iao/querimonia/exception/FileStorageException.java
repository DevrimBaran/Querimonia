package de.fraunhofer.iao.querimonia.exception;

/**
 * It’s thrown when an unexpected situation occurs while storing a file in the file system.
 * Inspired by https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/.
 */
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
