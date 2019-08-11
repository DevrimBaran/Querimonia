package de.fraunhofer.iao.querimonia.utility.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception that indicates that a element in a database does not exist.
 */
public class NotFoundException extends QuerimoniaException {

  public NotFoundException(long id) {
    this("Es existiert kein Element mit ID " + id + "!", id);
  }

  public NotFoundException(String message, long id) {
    super(HttpStatus.NOT_FOUND, message, "Fehlendes Element");
    this.id = id;
  }

  private final long id;

  public long getId() {
    return id;
  }
}
