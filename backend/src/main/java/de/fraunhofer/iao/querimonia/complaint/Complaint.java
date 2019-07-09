package de.fraunhofer.iao.querimonia.complaint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is represents a complaint. It contains the complaint text, the preview text, the
 * subject, the sentiment and the date of the complaint.
 */
@Entity
@JsonPropertyOrder(value = {
    "id",
    "text",
    "preview",
    "state",
    "configuration",
    "receiveDate",
    "receiveTime",
    "subject",
    "sentiment",
    "entities"
})
public class Complaint {

  /**
   * The primary key for the complaints in the database.
   */
  @Id
  @JsonProperty("id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int complaintId;

  /**
   * The complaint message, is limited to 10000 characters.
   */
  @Column(length = 10000)
  private String text;

  /**
   * A preview of the complaint message, should be the first two lines.
   */
  @Column(length = 500)
  private String preview;

  /**
   * The state of the complaint. A complaint is either new, in progress or closed.
   */
  @Enumerated(EnumType.ORDINAL)
  private ComplaintState state;

  /**
   * The category of the complaint.
   */
  @OneToOne(cascade = CascadeType.ALL)
  private ComplaintProperty subject;

  /**
   * The sentiment of the complaint.
   */
  @OneToOne(cascade = CascadeType.ALL)
  private ComplaintProperty sentiment;

  /**
   * The list of all named entities in the complaint text.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "entity_table", joinColumns = @JoinColumn(name = "complaintId"))
  private List<NamedEntity> entities;

  /**
   * The response for the complaint.
   */
  @OneToOne(cascade = CascadeType.ALL)
  private ResponseSuggestion responseSuggestion;

  /**
   * A list of all words in the complaint text, which are not stop words, mapped to their absolute
   * count.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "word_list_table", joinColumns = @JoinColumn(name = "complaintId"))
  @MapKeyColumn(name = "words")
  @Column(name = "count")
  @JsonIgnore
  private Map<String, Integer> wordList;

  /**
   * The date when the complaint was received.
   */
  private LocalDate receiveDate;
  private LocalTime receiveTime;

  /**
   * The configuration which was used to analyze the configuration.
   */
  @ManyToOne(cascade = CascadeType.MERGE)
  private Configuration configuration;

  /**
   * Creates a new complaint. This constructor is only used for JSON deserialization. Use a {@link
   * ComplaintFactory} to create complaints instead.
   */
  @JsonCreator
  public Complaint(@JsonProperty String text,
                   @JsonProperty String preview,
                   @JsonProperty ComplaintState state,
                   @JsonProperty ComplaintProperty sentiment,
                   @JsonProperty ComplaintProperty subject,
                   @JsonProperty LocalDate receiveDate,
                   @JsonProperty LocalTime receiveTime,
                   @JsonProperty List<NamedEntity> entities,
                   @JsonProperty Configuration configuration) {
    this.text = text;
    this.preview = preview;
    this.sentiment = sentiment;
    this.subject = subject;
    this.receiveDate = receiveDate;
    this.receiveTime = receiveTime;
    this.entities = entities;
    this.state = state;
    this.configuration = configuration;
  }

  /**
   * Constructor for the complaint factory.
   */
  Complaint(String text,
            String preview,
            LocalDate receiveDate,
            LocalTime receiveTime) {
    this.text = text;
    this.state = ComplaintState.NEW;
    this.preview = preview;
    this.sentiment = new ComplaintProperty();
    this.subject = new ComplaintProperty();
    this.entities = new ArrayList<>();
    this.responseSuggestion = new ResponseSuggestion();
    this.receiveDate = receiveDate;
    this.receiveTime = receiveTime;
    this.wordList = new HashMap<>();
  }

  /**
   * Empty default constructor (only used for hibernate).
   */
  @SuppressWarnings("unused")
  public Complaint() {

  }

  public int getComplaintId() {
    return complaintId;
  }

  public String getText() {
    return text;
  }

  public String getPreview() {
    return preview;
  }

  public ComplaintProperty getSentiment() {
    return sentiment;
  }

  public LocalDate getReceiveDate() {
    return receiveDate;
  }

  public LocalTime getReceiveTime() {
    return receiveTime;
  }

  public ComplaintProperty getSubject() {
    return subject;
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

  @JsonIgnore
  public Map<String, Integer> getWordList() {
    return wordList;
  }

  @JsonIgnore
  public ResponseSuggestion getResponseSuggestion() {
    return responseSuggestion;
  }

  public ComplaintState getState() {
    return state;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public Complaint setState(ComplaintState state) {
    this.state = state;
    return this;
  }

  public Complaint setEntities(
      List<NamedEntity> entities) {
    this.entities = entities;
    return this;
  }

  public Complaint setResponseSuggestion(ResponseSuggestion responseSuggestion) {
    this.responseSuggestion = responseSuggestion;
    return this;
  }

  public Complaint setWordList(Map<String, Integer> wordList) {
    this.wordList = wordList;
    return this;
  }

  public Complaint setConfiguration(Configuration configuration) {
    this.configuration = configuration;
    return this;
  }

  /**
   * Checks if two complaints are considered equal. A complaint is equal to another if their
   * text, state, subject, sentiment, entities and their response is equal.
   *
   * @param o the other complaint. Must be a complaint object.
   * @return true, if the given object is a complaint object and the two complaint objects are
   * equal.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Complaint complaint = (Complaint) o;
    return text.equals(complaint.text)
        && state == complaint.state
        && Objects.equals(subject, complaint.subject)
        && Objects.equals(sentiment, complaint.sentiment)
        && Objects.equals(entities, complaint.entities)
        && Objects.equals(responseSuggestion, complaint.responseSuggestion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, state, subject, sentiment, entities, responseSuggestion);
  }

  @Override
  public String toString() {
    return "Complaint{"
        + "complaintId=" + complaintId
        + ", text='" + text + '\''
        + ", preview='" + preview + '\''
        + ", state=" + state
        + ", subject=" + subject
        + ", sentiment=" + sentiment
        + ", entities=" + entities
        + ", responseSuggestion=" + responseSuggestion
        + ", wordList=" + wordList
        + ", receiveDate=" + receiveDate
        + ", receiveTime=" + receiveTime
        + '}';
  }
}
