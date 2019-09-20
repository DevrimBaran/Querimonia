package de.fraunhofer.iao.querimonia.utility.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * The standard exception for Querimonia that extends the ResponseStatusException. This exception
 * can be used as body for response entities.
 */
@SuppressWarnings("EmptyMethod")
public class QuerimoniaException extends ResponseStatusException {

  @JsonProperty
  private final LocalDateTime timestamp;
  @JsonProperty
  private final int statusCode;
  @JsonProperty
  private final String message;
  @JsonProperty
  private final String title;

  // work around to remove the "suppressed"-field from the super class in the json object.
  @JsonIgnore
  @SuppressWarnings("unused")
  private Object suppressed;

  /**
   * Creates a new Querimonia Exception.
   *
   * @param status  the http status code that gets returned to the client.
   * @param message the exception message.
   * @param cause   the exception that caused an error.
   * @param title   a title for a dialog window.
   */
  public QuerimoniaException(HttpStatus status, String message, Throwable cause,
                             String title) {
    super(status, message, cause);
    this.title = title;
    this.statusCode = status.value();
    this.timestamp = LocalDateTime.now();
    this.message = message;
  }

  /**
   * Creates a new Querimonia Exception with the cause exception type set to RuntimeException.
   *
   * @param status  the http status code of the response.
   * @param message the message of the exception.
   * @param title   a title for a dialog window.
   */
  public QuerimoniaException(HttpStatus status, String message, String title) {
    this(status, message, new RuntimeException(message), title);
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  @JsonProperty("status")
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public String getTitle() {
    return title;
  }

  /**
   * Returns the name of the java exception, that caused this exception. This can be set
   * when using the constructor with the cause parameter. When not set, this will return
   * {@link RuntimeException java.lang.RuntimeException}
   *
   * @return the name of the java exception, that caused this exception.
   */
  @JsonProperty("exception")
  public String getException() {
    if (getCause() != null) {
      return getCause().getClass().getName();
    }
    return null;
  }

  // Overwrite getters to add JsonIgnore tag.
  @Override
  @JsonIgnore
  public HttpStatus getStatus() {
    return super.getStatus();
  }

  @Override
  @JsonIgnore
  public String getReason() {
    return super.getReason();
  }

  @Override
  @JsonIgnore
  public String getLocalizedMessage() {
    return super.getLocalizedMessage();
  }

  @Override
  @JsonIgnore
  public synchronized Throwable getCause() {
    return super.getCause();
  }

  @Override
  @JsonIgnore
  public Throwable getRootCause() {
    return super.getRootCause();
  }

  @Override
  @JsonIgnore
  public Throwable getMostSpecificCause() {
    return super.getMostSpecificCause();
  }

  @Override
  @JsonIgnore
  public StackTraceElement[] getStackTrace() {
    return super.getStackTrace();
  }
}
