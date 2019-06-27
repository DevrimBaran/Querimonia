package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Parameter object for response generation.
 */
public class ComplaintData {
  private final String text;
  private final Map<String, Double> subjectMap;
  private final Map<String, Double> sentimentMap;
  private final List<NamedEntity> entities;
  private final LocalDateTime uploadTime;

  /**
   * Creates new ComplaintData object.
   *
   * @param text         the complaint text.
   * @param subjectMap   contains the subjects of the complaint mapped to their probabilities.
   * @param sentimentMap contains the sentiments of the complaint mapped to their probabilities.
   * @param entities     the found named entities in the text, also including upload date and time.
   * @param uploadTime   the time when the complaint was uploaded.
   */
  public ComplaintData(String text, Map<String, Double> subjectMap,
                       Map<String, Double> sentimentMap, List<NamedEntity> entities,
                       LocalDateTime uploadTime) {
    this.text = text;
    this.subjectMap = subjectMap;
    this.sentimentMap = sentimentMap;
    this.entities = entities;
    this.uploadTime = uploadTime;
  }

  public String getText() {
    return text;
  }

  public Map<String, Double> getSubjectMap() {
    return subjectMap;
  }

  public Map<String, Double> getSentimentMap() {
    return sentimentMap;
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

  public LocalDateTime getUploadTime() {
    return uploadTime;
  }
}
