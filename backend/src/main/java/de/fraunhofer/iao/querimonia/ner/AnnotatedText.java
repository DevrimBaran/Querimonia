package de.fraunhofer.iao.querimonia.ner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.springframework.lang.Nullable;

/**
 * This POJO represents a text with annotated named entities and a response to the complaint.
 * An instance of this class can be created using a {@link AnnotatedTextBuilder}.
 */
public class AnnotatedText {

  private String text;
  @Nullable
  private String answer;
  private List<NamedEntity> entities;

  /**
   * Constructs a new instance. This constructor is package private, for instance creation use the
   * {@link AnnotatedTextBuilder}.
   */
  @JsonCreator
  AnnotatedText(@JsonProperty String text, @Nullable @JsonProperty String answer,
                @JsonProperty List<NamedEntity> entities) {
    this.text = text;
    this.answer = answer;
    this.entities = entities;
  }

  public String getText() {
    return text;
  }

  @Nullable
  public String getAnswer() {
    return answer;
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

}
