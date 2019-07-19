package de.fraunhofer.iao.querimonia.complaint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.Sentiment;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.NonNull;
import tec.uom.lib.common.function.Identifiable;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is represents a complaint.
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
    "sentiment",
    "entities"
})
public class Complaint implements Identifiable<Long> {

  /**
   * The primary key for the complaints in the database.
   */
  @Id
  @JsonProperty("id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private long complaintId;

  /**
   * The complaint message, is limited to 10000 characters.
   */
  @Column(length = 10000, nullable = false)
  @NonNull
  private String text = "";

  /**
   * A preview of the complaint message, should be the first two lines.
   */
  @Column(length = 500, nullable = false)
  @NonNull
  private String preview = "";

  /**
   * The state of the complaint. A complaint is either new, in progress or closed.
   */
  @Enumerated(EnumType.ORDINAL)
  @Column(nullable = false)
  @NonNull
  private ComplaintState state = ComplaintState.NEW;

  /**
   * Additional properties for the complaint like the category, that get found by classifiers.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "complaint_id")
  @NonNull
  private List<ComplaintProperty> properties = Collections.emptyList();

  /**
   * A value that represents the sentiment of the text. Negative values represent negative
   * sentiment, positive value means positive sentiment.
   */
  @OneToOne(cascade = CascadeType.ALL)
  @NonNull
  private Sentiment sentiment = new Sentiment();

  /**
   * The list of all named entities in the complaint text.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "complaint_id")
  @JsonIgnore
  @NonNull
  private List<NamedEntity> entities = Collections.emptyList();

  /**
   * The response for the complaint.
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JsonIgnore
  @NonNull
  private ResponseSuggestion responseSuggestion = new ResponseSuggestion();

  /**
   * A list of all words in the complaint text, which are not stop words, mapped to their absolute
   * count.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "word_list_table", joinColumns = @JoinColumn(name = "complaintId"))
  @MapKeyColumn(name = "words")
  @Column(name = "count")
  @NonNull
  @JsonIgnore
  private Map<String, Integer> wordList = new HashMap<>();

  /**
   * The date when the complaint was received.
   */
  @Column(nullable = false)
  @NonNull
  private LocalDate receiveDate = LocalDate.now();
  @Column(nullable = false)
  @NonNull
  private LocalTime receiveTime = LocalTime.now();

  /**
   * The configuration which was used to analyze the configuration.
   */
  @ManyToOne(cascade = CascadeType.MERGE)
  @NonNull
  private Configuration configuration = Configuration.FALLBACK_CONFIGURATION;

  /**
   * Constructor for builder.
   */
  Complaint(
      long id,
      @NonNull String text,
      @NonNull String preview,
      @NonNull ComplaintState state,
      @NonNull List<ComplaintProperty> properties,
      @NonNull Sentiment sentiment,
      @NonNull List<NamedEntity> entities,
      @NonNull ResponseSuggestion responseSuggestion,
      @NonNull Map<String, Integer> wordList,
      @NonNull LocalDate receiveDate,
      @NonNull LocalTime receiveTime,
      @NonNull Configuration configuration) {
    this.complaintId = id;
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
   * Empty default constructor (only used for hibernate).
   */
  @SuppressWarnings("unused")
  public Complaint() {

  }

  @JsonIgnore
  public ComplaintProperty getSubject() {
    return ComplaintUtility.getPropertyOfComplaint(this, "Kategorie");
  }

  @JsonProperty("id")
  @Override
  public Long getId() {
    return complaintId;
  }

  @NonNull
  public String getText() {
    return text;
  }

  @NonNull
  public String getPreview() {
    return preview;
  }

  @JsonIgnore
  public ComplaintProperty getEmotion() {
    return sentiment.getEmotion();
  }

  @NonNull
  public LocalDate getReceiveDate() {
    return receiveDate;
  }

  @NonNull
  public LocalTime getReceiveTime() {
    return receiveTime;
  }

  @NonNull
  public List<NamedEntity> getEntities() {
    return entities;
  }

  @NonNull
  @JsonIgnore
  public Map<String, Integer> getWordList() {
    return wordList;
  }

  @NonNull
  @JsonIgnore
  public ResponseSuggestion getResponseSuggestion() {
    return responseSuggestion;
  }

  @NonNull
  public ComplaintState getState() {
    return state;
  }

  public Complaint withState(ComplaintState state) {
    return new ComplaintBuilder(this)
        .setState(state)
        .createComplaint();
  }

  @NonNull
  public Configuration getConfiguration() {
    return configuration;
  }

  public Complaint withConfiguration(Configuration configuration) {
    return new ComplaintBuilder(this)
        .setConfiguration(configuration)
        .createComplaint();
  }

  @NonNull
  public List<ComplaintProperty> getProperties() {
    return properties;
  }

  @NonNull
  public Sentiment getSentiment() {
    return sentiment;
  }

  /**
   * Checks if two complaints are considered equal. A complaint is equal to another if their
   * text, state, subject, emotion, entities and their response is equal.
   *
   * @param o the other complaint. Must be a complaint object.
   *
   * @return true, if the given object is a complaint object and the two complaint objects are
   *     equal.
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
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("complaintId", complaintId)
        .append("text", text)
        .append("preview", preview)
        .append("state", state)
        .append("properties", properties)
        .append("sentiment", sentiment)
        .append("entities", entities)
        .append("responseSuggestion", responseSuggestion)
        .append("wordList", wordList)
        .append("receiveDate", receiveDate)
        .append("receiveTime", receiveTime)
        .append("configuration", configuration)
        .toString();
  }
}
