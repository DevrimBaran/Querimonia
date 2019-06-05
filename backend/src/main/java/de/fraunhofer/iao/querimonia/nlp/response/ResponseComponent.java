package de.fraunhofer.iao.querimonia.nlp.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a template for a part of the response.
 * A response consists of a list of these response components.
 */
@Entity
public class ResponseComponent {

  /**
   * Unique componentId for the template to store in the database.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int componentId;

  /**
   * The template text that may have placeholders.
   */
  @Column(length = 5000)
  private String templateText;

  /**
   * The complaint category to this component fits.
   */
  @Nullable
  private String subject;

  /**
   * A unique identifier that represents where to component should be set in the full response,
   * for example "begin", "introduction", "end"...
   */
  private String responsePart;

  /**
   * This list contains all the possible response parts that may follow up this component. This
   * is used for response generation.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  private List<String> successorParts;

  /**
   * Basic constructor for JSON deserialization.
   */
  @JsonCreator
  public ResponseComponent(@JsonProperty("templateText") String templateText,
                           @JsonProperty("subject") String subject,
                           @JsonProperty("responsePart") String responsePart,
                           @JsonProperty("successorParts") ArrayList<String> successorParts) {
    this.templateText = templateText;
    this.subject = subject;
    this.responsePart = responsePart;
    this.successorParts = successorParts;
  }

  public ResponseComponent() {

  }

  public List<String> getSuccessorParts() {
    return successorParts;
  }

  public int getComponentId() {
    return componentId;
  }

  public String getTemplateText() {
    return templateText;
  }

  public Optional<String> getSubject() {
    return Optional.ofNullable(subject);
  }

  public String getResponsePart() {
    return responsePart;
  }
}