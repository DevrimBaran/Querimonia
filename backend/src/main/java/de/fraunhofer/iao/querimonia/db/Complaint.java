package de.fraunhofer.iao.querimonia.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

/**
 * This class is represents a complaint. It contains the complaint text, the preview text, the
 * subjects, the sentiments and the date of the complaint.
 */
@Entity
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
  @Column(length = 5000)
  private String text;

  /**
   * A preview of the complaint message, should be the first two lines.
   */
  @Column(length = 500)
  private String preview;

  /**
   * This map contains the possible sentiments of the complaint message mapped to their
   * probabilities according to the machine learning algorithms.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "sentiment_table", joinColumns = @JoinColumn(name = "complaintId"))
  @MapKeyColumn(name = "sentiment")
  @Column(name = "probability")
  private Map<String, Double> sentiments;

  /**
   * This map contains the possible subjects of the complaint message mapped to their
   * probabilities according to the machine learning algorithms.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "subject_table", joinColumns = @JoinColumn(name = "complaintId"))
  @MapKeyColumn(name = "subject")
  @Column(name = "probability")
  private Map<String, Double> subjects;

  /**
   * The list of all named entities in the complaint text.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "entity_table", joinColumns = @JoinColumn(name = "complaintId"))
  private List<NamedEntity> entities;

  /**
   * The date when the complaint was received.
   */
  private LocalDateTime receiveDate;

  /**
   * Creates a new complaint. This constructor is for JSON deserialization.
   */
  @JsonCreator
  public Complaint(@JsonProperty String text,
                   @JsonProperty String preview,
                   @JsonProperty Map<String, Double> sentiments,
                   @JsonProperty Map<String, Double> subjects,
                   @JsonProperty LocalDateTime receiveDate,
                   @JsonProperty List<NamedEntity> entities) {
    this.text = text;
    this.preview = preview;
    this.sentiments = sentiments;
    this.subjects = subjects;
    this.receiveDate = receiveDate;
    this.entities = entities;
  }

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

  public Map<String, Double> getSentiments() {
    return sentiments;
  }

  public LocalDateTime getReceiveDate() {
    return receiveDate;
  }

  public Map<String, Double> getSubjects() {
    return subjects;
  }

  public List<NamedEntity> getEntities() {
    return entities;
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
        && Objects.equals(sentiments, complaint.sentiments)
        && Objects.equals(subjects, complaint.subjects)
        && Objects.equals(receiveDate, complaint.receiveDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(complaintId, text, preview, sentiments, receiveDate);
  }
}
