package de.fraunhofer.iao.querimonia.nlp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import static javax.persistence.CascadeType.ALL;

/**
 * This class contains the results of the tendency and emotion analysis.
 */
@Entity
public class Sentiment implements Comparable<Sentiment> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private long id;

  @OneToOne(cascade = ALL)
  @NonNull
  private ComplaintProperty emotion = ComplaintProperty.getDefaultProperty("Emotion");
  private double tendency;

  @JsonCreator
  public Sentiment(
      @JsonProperty("emotion")
      @NonNull
          ComplaintProperty emotion,
      @JsonProperty("tendency")
          double tendency
  ) {
    this.emotion = emotion;
    this.tendency = tendency;
  }

  @SuppressWarnings("unused")
  private Sentiment() {
    // for hibernate
  }

  /**
   * Fallback sentiment when no sentiment is available.
   */
  public static Sentiment getDefaultSentiment() {
    return new Sentiment(ComplaintProperty.getDefaultProperty("Emotion"), 0.0);
  }

  /**
   * Returns the emotion property of the complaint text.
   *
   * @return the emotion property of the complaint.
   */
  @NonNull
  public ComplaintProperty getEmotion() {
    return emotion;
  }

  /**
   * Returns the tendency of the complaint. The tendency is a value between -1 and 1. Positive
   * values mean positive sentiment, where greater values also imply a more positive sentiment.
   * Negative values mean negative sentiment, where smaller values also imply a more negative
   * sentiment. If the value is -2, then there is no sentiment value available.
   *
   * @return the tendency of the complaint.
   */
  public double getTendency() {
    return tendency;
  }

  /**
   * Returns a copy of this sentiment object with the emotion set to the given value.
   *
   * @param emotionProperty the emotion that the copy should have.
   *
   * @return a copy of this sentiment object with the emotion set to the given value.
   */
  public Sentiment withEmotion(ComplaintProperty emotionProperty) {
    return new Sentiment(emotionProperty, this.tendency);
  }

  /**
   * Returns a copy of this sentiment object with the tendency set to the given value.
   *
   * @param tendency the tendency that the copy should have.
   *
   * @return a copy of this sentiment object with the tendency set to the given value.
   */
  public Sentiment withTendency(double tendency) {
    return new Sentiment(this.emotion, tendency);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Sentiment sentiment1 = (Sentiment) o;

    return new EqualsBuilder()
        .append(tendency, sentiment1.tendency)
        .append(emotion, sentiment1.emotion)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(emotion)
        .append(tendency)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("id", id)
        .append("emotion", emotion)
        .append("tendency", tendency)
        .toString();
  }

  @Override
  public int compareTo(@NotNull Sentiment o) {
    return Double.compare(this.tendency, o.tendency);
  }
}
