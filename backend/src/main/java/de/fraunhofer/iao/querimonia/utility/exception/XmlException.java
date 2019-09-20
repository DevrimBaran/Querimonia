package de.fraunhofer.iao.querimonia.utility.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.xml.sax.SAXParseException;

/**
 * A exception that gets thrown on XML parse errors.
 */
public class XmlException extends QuerimoniaException {

  @JsonProperty("line")
  private final int line;
  @JsonProperty("column")
  private final int column;

  public XmlException(HttpStatus status, String message, SAXParseException exception) {
    super(status, message, exception, "XML-Fehler");
    this.line = exception.getLineNumber();
    this.column = exception.getColumnNumber() - 1;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }
}
