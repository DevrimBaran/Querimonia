package de.fraunhofer.iao.querimonia.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * The standard exception for Querimonia that extends the ResponseStatusException.
 */
public class QuerimoniaException extends ResponseStatusException {

  @JsonProperty
  private LocalDateTime timestamp;
  @JsonProperty
  private int statusCode;
  @JsonProperty
  private String message;
  @JsonProperty
  private String title;

  // to remove suppressed field from super class
  @JsonIgnore
  private Object suppressed;

  public QuerimoniaException(HttpStatus status, Throwable cause, String title) {
    this(status, cause.getMessage(), cause, title);
  }

  public QuerimoniaException(HttpStatus status, String message, Throwable cause,
                             String title) {
    super(status, message, cause);
    this.title = title;
    this.statusCode = status.value();
    this.timestamp = LocalDateTime.now();
    this.message = message;
  }

  public QuerimoniaException(HttpStatus status, String message, String title) {
    this(status, message, new RuntimeException(message), title);
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

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
