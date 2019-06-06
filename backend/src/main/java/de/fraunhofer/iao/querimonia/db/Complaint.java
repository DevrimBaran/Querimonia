package de.fraunhofer.iao.querimonia.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseSuggestion;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class is represents a complaint. It contains the complaint text, the preview text, the
 * subject, the sentiment and the date of the complaint.
 */
@Entity
@JsonPropertyOrder({
    "complaintId",
    "text",
    "preview",
    "receiveDate",
    "receiveTime",
    "subject",
    "probableSubject",
    "sentiment",
    "probableSentiment",
    "entities"
})
public class Complaint {

  /**
   * The primary key for the complaints in the database.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int complaintId;

  /**
   * The complaint message, is limited to 5000 characters.
   */
  @Column(length = 10000)
  private String text;

  /**
   * A preview of the complaint message, should be the first two lines.
   */
  @Column(length = 500)
  private String preview;

  /**
   * This map contains the possible sentiment of the complaint message mapped to their
   * probabilities according to the machine learning algorithms.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "sentiment_table", joinColumns = @JoinColumn(name = "complaintId"))
  @MapKeyColumn(name = "sentiment")
  @Column(name = "probability")
  private Map<String, Double> sentiment;

  /**
   * This map contains the possible subject of the complaint message mapped to their
   * probabilities according to the machine learning algorithms.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "subject_table", joinColumns = @JoinColumn(name = "complaintId"))
  @MapKeyColumn(name = "subject")
  @Column(name = "probability")
  private Map<String, Double> subject;

  /**
   * The list of all named entities in the complaint text.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "entity_table", joinColumns = @JoinColumn(name = "complaintId"))
  private List<NamedEntity> entities;

  /**
   * The response for the complaint.
   */
  @JoinColumn(name = "complaint_id")
  @JsonIgnore
  private ResponseSuggestion responseSuggestion;

  /**
   * A list of all words in the complaint text, which are not stop words, mapped to their
   * absolute count.
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
   * Creates a new complaint. This constructor is only used for JSON deserialization.
   * Use a {@link ComplaintFactory} to create complaints instead.
   */
  @JsonCreator
  @SuppressWarnings("unused")
  public Complaint(@JsonProperty String text,
                   @JsonProperty String preview,
                   @JsonProperty Map<String, Double> sentiment,
                   @JsonProperty Map<String, Double> subject,
                   @JsonProperty LocalDate receiveDate,
                   @JsonProperty LocalTime receiveTime,
                   @JsonProperty List<NamedEntity> entities) {
    this.text = text;
    this.preview = preview;
    this.sentiment = sentiment;
    this.subject = subject;
    this.receiveDate = receiveDate;
    this.receiveTime = receiveTime;
    this.entities = entities;
  }

  /**
   * Constructor for the complaint factory.
   */
  Complaint(String text,
            String preview,
            Map<String, Double> sentiment,
            Map<String, Double> subject,
            List<NamedEntity> entities,
            LocalDate receiveDate,
            LocalTime receiveTime,
            ResponseSuggestion responseSuggestion,
            Map<String, Integer> wordList) {
    this.text = text;
    this.preview = preview;
    this.sentiment = sentiment;
    this.subject = subject;
    this.entities = entities;
    this.responseSuggestion = responseSuggestion;
    this.receiveDate = receiveDate;
    this.receiveTime = receiveTime;
    this.wordList = wordList;
  }

  /**
   * Empty default constructor (only used for hibernate).
   */
  @SuppressWarnings("unused")
  public Complaint() {

  }

  /**
   * Returns the sentiment with the highest probability.
   *
   * @return the sentiment with the highest probability or an empty optional, if
   * no sentiment is given.
   */
  @Transient
  @JsonProperty("probableSentiment")
  public Optional<String> getBestSentiment() {
    return ComplaintUtility.getEntryWithHighestProbability(sentiment);
  }

  @Transient
  @JsonProperty("probableSubject")
  public Optional<String> getBestSubject() {
    return ComplaintUtility.getEntryWithHighestProbability(subject);
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

  public Map<String, Double> getSentiment() {
    return sentiment;
  }

  public LocalDate getReceiveDate() {
    return receiveDate;
  }

  public LocalTime getReceiveTime() {
    return receiveTime;
  }

  public Map<String, Double> getSubject() {
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


}
