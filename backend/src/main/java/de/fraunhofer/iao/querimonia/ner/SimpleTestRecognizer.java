package de.fraunhofer.iao.querimonia.ner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a very simple recognizer which extracts the bus line and the date out of a example
 * sentence.
 */
public class SimpleTestRecognizer {

  /**
   * This regex accepts bus lines. Bus lines are numbers with three digits.
   */
  private static final Pattern LINE_PATTERN = Pattern.compile(" [0-9]{3} ");

  /**
   * This regex accepts dates of the format dd.mm.yyyy
   */
  private static final Pattern DATE_PATTERN = Pattern.compile(" (0\\d|[12]\\d|3[01])\\."
      + "(0\\d|1[012])(\\.\\d{4}) ");

  /**
   * Finds the bus line and the date in a String. The string should be in a similar structure
   * like the given example.
   *
   * @param text the text which the entities should be extracted from.
   * @return the text with the annotations.
   */
  public AnnotatedText annotateText(String text) {
    AnnotatedTextBuilder annotatedTextBuilder = new AnnotatedTextBuilder(text);

    // initialize matchers for the regular expressions
    Matcher lineMatcher = LINE_PATTERN.matcher(text);
    Matcher dateMatcher = DATE_PATTERN.matcher(text);

    // add entities if the matchers match
    // note: for the indices is a addition / subtraction of 1 necessary because the regex
    // also matches the
    // spaces before and after the entity
    while (lineMatcher.find()) {
      annotatedTextBuilder.addEntity("line", lineMatcher.start() + 1, lineMatcher.end() - 1);
    }

    while (dateMatcher.find()) {
      annotatedTextBuilder.addEntity("date", dateMatcher.start() + 1, dateMatcher.end() - 1);
    }

    return annotatedTextBuilder.createAnnotatedText();
  }
}
