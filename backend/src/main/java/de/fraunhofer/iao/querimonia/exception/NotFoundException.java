package de.fraunhofer.iao.querimonia.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception that indicates that a element in a database does not exist.
 */
public class NotFoundException extends QuerimoniaException {

  public NotFoundException(int id) {
    this("Es existiert kein Element mit ID " + id + "!", id);
  }

  public NotFoundException(String message, int id) {
    super(HttpStatus.NOT_FOUND, message, "Fehlendes Element");
    this.id = id;
  }

  private final int id;

  public int getId() {
    return id;
  }
}
