package de.fraunhofer.iao.querimonia.nlp.response.template;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a slice of the response component that is either raw text or a placeholder. The
 * complaint text gets sliced in text and placeholders.
 */
public class ResponseSlice {

  private boolean isPlaceholder;
  // this is either the placeholder name or the raw text when the slice is no placeholder.
  private String content;

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
   * Slices the template text into text and placeholder parts.
   *
   * @param text the text that should be sliced.
   */
  public static List<ResponseSlice> createSlices(String text) {
    List<ResponseSlice> slices = new ArrayList<>();
    // the current position in the text
    int templatePosition = 0;

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
        // check if closing bracket is there
        if (endPosition == -1) {
          throw new IllegalArgumentException("Illegal template format");
        }
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
