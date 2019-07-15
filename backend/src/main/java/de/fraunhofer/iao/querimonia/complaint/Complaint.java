package de.fraunhofer.iao.querimonia.complaint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is represents a complaint. It contains the complaint text, the preview text, the
 * subject, the emotion and the date of the complaint.
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
    "emotion",
    "entities"
})
public class Complaint {

  /**
   * The primary key for the complaints in the database.
   */
  @Id
  @JsonProperty("id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long complaintId;

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
   * Additional properties for the complaint like the category, that get found by classifiers.
   */
  @OneToMany(cascade = CascadeType.ALL)
  private List<ComplaintProperty> properties;

  /**
   * A value that represents the sentiment of the text. Negative values represent negative
   * sentiment, positive value means positive sentiment.
   */
  private double sentiment;

  /**
   * The list of all named entities in the complaint text.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JsonIgnore
  private List<NamedEntity> entities;

  /**
   * The response for the complaint.
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JsonIgnore
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

  public Complaint(String text, String preview,
                   ComplaintState state,
                   List<ComplaintProperty> properties, double sentiment,
                   List<NamedEntity> entities,
                   ResponseSuggestion responseSuggestion,
                   Map<String, Integer> wordList, LocalDate receiveDate,
                   LocalTime receiveTime,
                   Configuration configuration) {
    this.text = text;
    this.preview = preview;
    this.state = state;
    this.properties = properties;
    this.sentiment = sentiment;
    this.entities = entities;
    this.responseSuggestion = responseSuggestion;
    this.wordList = wordList;
    this.receiveDate = receiveDate;
    this.receiveTime = receiveTime;
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
    this.entities = new ArrayList<>();
    this.configuration = Configuration.FALLBACK_CONFIGURATION;
    this.responseSuggestion = new ResponseSuggestion();
    this.receiveDate = receiveDate;
    this.receiveTime = receiveTime;
    this.wordList = new HashMap<>();
    this.properties = new ArrayList<>();
  }

  /**
   * Empty default constructor (only used for hibernate).
   */
  @SuppressWarnings("unused")
  public Complaint() {

  }

  @JsonIgnore
  public ComplaintProperty getSubject() {
    return ComplaintUtility.getPropertyOfComplaint(this, "Kategorie");
  }

  public long getComplaintId() {
    return complaintId;
  }

  public String getText() {
    return text;
  }

  public String getPreview() {
    return preview;
  }

  @JsonIgnore
  public ComplaintProperty getEmotion() {
    return ComplaintUtility.getPropertyOfComplaint(this, "Emotion");
  }

  public LocalDate getReceiveDate() {
    return receiveDate;
  }

  public LocalTime getReceiveTime() {
    return receiveTime;
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

  public Complaint setEntities(List<NamedEntity> entities) {
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

  public List<ComplaintProperty> getProperties() {
    return properties;
  }

  public Complaint setProperties(
      List<ComplaintProperty> properties) {
    this.properties = properties;
    return this;
  }

  public double getSentiment() {
    return sentiment;
  }

  public Complaint setSentiment(double sentiment) {
    this.sentiment = sentiment;
    return this;
  }

  /**
   * Checks if two complaints are considered equal. A complaint is equal to another if their
   * text, state, subject, emotion, entities and their response is equal.
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

    return new EqualsBuilder()
        .append(sentiment, complaint.sentiment)
        .append(text, complaint.text)
        .append(state, complaint.state)
        .append(properties, complaint.properties)
        .append(entities, complaint.entities)
        .append(responseSuggestion, complaint.responseSuggestion)
        .append(receiveDate, complaint.receiveDate)
        .append(receiveTime, complaint.receiveTime)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(text)
        .append(state)
        .append(properties)
        .append(sentiment)
        .append(entities)
        .append(responseSuggestion)
        .append(receiveDate)
        .append(receiveTime)
        .toHashCode();
  }
}
