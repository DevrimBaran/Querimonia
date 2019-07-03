package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple wrapper class for Strings. This is necessary to allow the JSON syntax in the
 * POST-requests.
 */
public class TextInput {

  private final String text;

  @JsonCreator
  public TextInput(@JsonProperty String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
