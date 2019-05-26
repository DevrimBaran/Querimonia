package de.fraunhofer.iao.querimonia.rest.restobjects.textominado;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("ALL")
public class TextominadoText {

  private String text;
  private String textID;
  private String language;

  @JsonCreator
  public TextominadoText(@JsonProperty("text") String text,
                         @JsonProperty("textID") String textID,
                         @JsonProperty("language") String language) {
    this.text = text;
    this.textID = textID;
    this.language = language;
  }

  public String getText() {
    return this.text;
  }

  public String getTextID() {
    return this.textID;
  }

  public String getLanguage() {
    return this.language;
  }

}
