package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple wrapper class for Strings. This is necessary to allow the JSON syntax in the
 * POST-requests.
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class TextInput {

  private String text;

  @JsonCreator
  public TextInput(@JsonProperty String text) {
    this.text = text;
  }

  @SuppressWarnings("unused")
  public TextInput() {
    // for xml creation.
  }

  public String getText() {
    return text;
  }
}
