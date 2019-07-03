package de.fraunhofer.iao.querimonia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * The standard exception for Querimonia that extends the ResponseStatusException.
 */
public class QuerimoniaException extends ResponseStatusException {
  public QuerimoniaException(HttpStatus status, Throwable cause) {
    super(status, cause.getMessage(), cause);
  }

  public QuerimoniaException(HttpStatus status, String message) {
    super(status, message, new RuntimeException(message));
  }
}
