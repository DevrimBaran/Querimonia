package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

@SuppressWarnings("ALL")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {
                        "Startposition",
                        "ContextBefore",
                        "Endposition",
                        "Text",
                        "Typ",
                        "ContextAfter"
                    })
public class FoundEntity {

  private int startposition;
  private String contextBefore;
  private int endposition;
  private String text;
  private LinkedHashMap<String, Double> typ;
  private String contextAfter;
  /**
   * Temporary FoundEntity.
   *
   * @param startposition Gets the startsposition of the entity.
   * @param contextBefore Gets the information which is before the entity.
   * @param endposition   Gets the endposition of the entity.
   * @param text          Gets he entity text.
   * @param typ           Gets the entity type.
   * @param contextAfter  Gets the text after the entity.
   */
  @JsonCreator
  public FoundEntity(@JsonProperty("Startposition") int startposition,
                     @JsonProperty("ContextBefore") String contextBefore,
                     @JsonProperty("Endposition") int endposition,
                     @JsonProperty("Text") String text,
                     @JsonProperty("Typ") LinkedHashMap<String, Double> typ,
                     @JsonProperty("ContextAfter") String contextAfter) {
    this.startposition = startposition;
    this.contextBefore = contextBefore;
    this.endposition = endposition;
    this.text = text;
    this.typ = typ;
    this.contextAfter = contextAfter;

  }

  public int getStartposition() {
    return startposition;
  }

  public void setStartposition(int startposition) {
    this.startposition = startposition;
  }

  public String getContextBefore() {
    return contextBefore;
  }

  public void setContextBefore(String contextBefore) {
    this.contextBefore = contextBefore;
  }

  public int getEndposition() {
    return endposition;
  }

  public void setEndposition(int endposition) {
    this.endposition = endposition;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public LinkedHashMap<String, Double> getTyp() {
    return typ;
  }

  public void setTyp(LinkedHashMap<String, Double> typ) {
    this.typ = typ;
  }

  public String getContextAfter() {
    return contextAfter;
  }

  public void setContextAfter(String contextAfter) {
    this.contextAfter = contextAfter;
  }

  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return text;
  }
}