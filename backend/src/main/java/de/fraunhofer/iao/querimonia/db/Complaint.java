package de.fraunhofer.iao.querimonia.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.ResponseSuggestion;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is represents a complaint. It contains the complaint text, the preview text, the
 * subject, the sentiment and the date of the complaint.
 */
@Entity
@JsonPropertyOrder( {
    "complaintId",
    "text",
    "preview",
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
   * The date when the complaint was received.
   */
  private LocalDate receiveDate;
  private LocalTime receiveTime;

  /**
   * Creates a new complaint. This constructor is only used for JSON deserialization.
   * Use a {@link ComplaintFactory} to create complaints instead.
   */
  @JsonCreator
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
            ResponseSuggestion responseSuggestion) {
    this.text = text;
    this.preview = preview;
    this.sentiment = sentiment;
    this.subject = subject;
    this.entities = entities;
    this.responseSuggestion = responseSuggestion;
    this.receiveDate = receiveDate;
    this.receiveTime = receiveTime;
  }

  /**
   * Empty default constructor (only used for hibernate).
   */
  public Complaint() {

  }

  /**
   * This methods returns the value of a named entity that occurs in this complaint.
   *
   * @param entityLabel the label of the named entity, like "date".
   * @return the value of the named entity or an empty optional if the entity is not present.
   * If there are multiple occurrences, it will return the first one.
   */
  @Transient
  @JsonIgnore
  public Optional<String> getValueOfEntity(String entityLabel) {
    return getAllValuesOfEntity(entityLabel).stream().findFirst();
  }

  /**
   * Finds all entities with the given label in the complaint text and returns their value as a
   * list.
   *
   * @param entityLabel the entity label to look for, like "date".
   * @return a list of all values of the named entities with the given label.
   */
  @Transient
  @JsonIgnore
  public List<String> getAllValuesOfEntity(String entityLabel) {
    return entities.stream()
        // only use entities that label match the given one.
        .filter(namedEntity -> namedEntity.getLabel().equalsIgnoreCase(entityLabel))
        // find their value in the text
        .map(namedEntity -> text.substring(namedEntity.getStartIndex(), namedEntity.getEndIndex()))
        .collect(Collectors.toList());
  }

  /**
   * Creates a map that maps all the entity labels to their values in the text.
   */
  @Transient
  @JsonIgnore
  public Map<String, String> getEntityValueMap() {
    HashMap<String, String> result = new HashMap<>();
    entities.stream()
        .map(NamedEntity::getLabel)
        .forEach(label -> result.put(label,
            getValueOfEntity(label).orElseThrow(IllegalStateException::new)));

    // add also upload time and date
    // TODO date format
    result.putIfAbsent("upload_date", receiveDate.toString());
    result.putIfAbsent("upload_time", receiveTime.toString());
    return result;
  }

  @Transient
  @JsonIgnore
  public Optional<String> getBestSentiment() {
    return ComplaintFilter.getEntryWithHighestProbability(sentiment);
  }

  @Transient
  @JsonIgnore
  public Optional<String> getBestSubject() {
    return ComplaintFilter.getEntryWithHighestProbability(subject);
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
  public ResponseSuggestion getResponseSuggestion() {
    return responseSuggestion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Complaint complaint = (Complaint) o;
    return complaintId == complaint.complaintId
        && Objects.equals(text, complaint.text)
        && Objects.equals(preview, complaint.preview)
        && Objects.equals(sentiment, complaint.sentiment)
        && Objects.equals(subject, complaint.subject)
        && Objects.equals(receiveDate, complaint.receiveDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(complaintId, text, preview, sentiment, receiveDate);
  }

}
