package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Parameter object for response generation. Contains the information of a {@link Complaint}.
 */
public class ComplaintData {
  private final String text;
  private final List<ComplaintProperty> properties;
  private final ComplaintProperty emotion;
  private final List<NamedEntity> entities;
  private final LocalDateTime uploadTime;
  private final double sentiment;

  /**
   * Creates new ComplaintData object.
   *
   * @param text       the complaint text.
   * @param properties all properties of the complaint.
   * @param emotion    contains the sentiments of the complaint mapped to their probabilities.
   * @param entities   the found named entities in the text, also including upload date and time.
   * @param uploadTime the time when the complaint was uploaded.
   * @param sentiment  the sentiment of the text
   */
  public ComplaintData(String text, List<ComplaintProperty> properties,
                       ComplaintProperty emotion, List<NamedEntity> entities,
                       LocalDateTime uploadTime, double sentiment) {
    this.text = text;
    this.properties = properties;
    this.emotion = emotion;
    this.entities = entities;
    this.uploadTime = uploadTime;
    this.sentiment = sentiment;
  }

  /**
   * Creates a complaint data object from a complaint.
   *
   * @param complaint the complaint, this data object contains the same information as the
   *                  complaint.
   */
  public ComplaintData(Complaint complaint) {
    this.text = complaint.getText();
    this.properties = complaint.getProperties();
    this.emotion = complaint.getEmotion();
    this.entities = complaint.getEntities();
    this.uploadTime = LocalDateTime.of(complaint.getReceiveDate(), complaint.getReceiveTime());
    this.sentiment = complaint.getSentiment();
  }

  public List<ComplaintProperty> getProperties() {
    return properties;
  }

  public String getText() {
    return text;
  }

  public ComplaintProperty getSubject() {
    return ComplaintUtility.getPropertyOfComplaint(this, "Kategorie");
  }

  public ComplaintProperty getEmotion() {
    return emotion;
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

  public double getSentiment() {
    return sentiment;
  }

  public LocalDateTime getUploadTime() {
    return uploadTime;
  }
}
