package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Parameter object for response generation. Contains the information of a {@link Complaint}.
 */
public class ComplaintData {
  private final String text;
  private final ComplaintProperty subject;
  private final ComplaintProperty sentiment;
  private final List<NamedEntity> entities;
  private final LocalDateTime uploadTime;

  /**
   * Creates new ComplaintData object.
   *
   * @param text         the complaint text.
   * @param subject   contains the subjects of the complaint mapped to their probabilities.
   * @param sentiment contains the sentiments of the complaint mapped to their probabilities.
   * @param entities     the found named entities in the text, also including upload date and time.
   * @param uploadTime   the time when the complaint was uploaded.
   */
  public ComplaintData(String text, ComplaintProperty subject,
                       ComplaintProperty sentiment, List<NamedEntity> entities,
                       LocalDateTime uploadTime) {
    this.text = text;
    this.subject = subject;
    this.sentiment = sentiment;
    this.entities = entities;
    this.uploadTime = uploadTime;
  }

  /**
   * Creates a complaint data object from a complaint.
   *
   * @param complaint the complaint, this data object contains the same information as the
   *                  complaint.
   */
  public ComplaintData(Complaint complaint) {
    this.text = complaint.getText();
    this.subject = ComplaintUtility.getSubjectOfComplaint(complaint);
    this.sentiment = complaint.getEmotion();
    this.entities = complaint.getEntities();
    this.uploadTime = LocalDateTime.of(complaint.getReceiveDate(), complaint.getReceiveTime());
  }

  public String getText() {
    return text;
  }

  public ComplaintProperty getSubject() {
    return subject;
  }

  public ComplaintProperty getSentiment() {
    return sentiment;
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

  public LocalDateTime getUploadTime() {
    return uploadTime;
  }
}
