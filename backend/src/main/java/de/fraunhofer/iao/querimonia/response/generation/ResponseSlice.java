package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a slice of the response component that is either raw text or a placeholder. The
 * complaint text gets sliced in text and placeholders.
 */
public class ResponseSlice {

  private final boolean isPlaceholder;
  // this is either the placeholder name or the raw text when the slice is no placeholder.
  private final String content;

  private ResponseSlice(boolean isPlaceholder, String content) {
    this.isPlaceholder = isPlaceholder;
    this.content = content;
  }

  public boolean isPlaceholder() {
    return isPlaceholder;
  }

  public String getContent() {
    return content;
  }

  /**
   * Slices the component text into text and placeholder parts.
   *
   * @param text the text that should be sliced.
   */
  public static List<ResponseSlice> createSlices(String text) {
    List<ResponseSlice> slices = new ArrayList<>();
    // the current position in the text
    int templatePosition = 0;

    // check if template text has correct placeholder formatting
    if (!text.matches("[^${}]+(\\$\\{\\w*}[^${}]*)*")) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Text der Komponente ist falsch "
          + "formatiert: " + text, "Ungültiger Textbaustein");
    }


    // split on every placeholder
    String[] parts = text.split("\\$\\{\\w*}");

    for (String currentPart : parts) {
      templatePosition += currentPart.length();
      // add raw text slice
      slices.add(new ResponseSlice(false, currentPart));

      String remainingText = text.substring(templatePosition);
      if (remainingText.length() >= 2) {
        // find closing bracket
        int endPosition = remainingText.indexOf('}');
        // find entity for placeholder
        String entityLabel = remainingText.substring(2, endPosition);
        // add label slice
        slices.add(new ResponseSlice(true, entityLabel));

        templatePosition = templatePosition + 3 + entityLabel.length();

      } else {
        break;
      }
    }
    return slices;
  }
}
