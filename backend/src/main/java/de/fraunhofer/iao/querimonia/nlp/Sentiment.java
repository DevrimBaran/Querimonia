package de.fraunhofer.iao.querimonia.nlp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iao.querimonia.complaint.Complaint;
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

import java.util.Collections;
import java.util.HashMap;

import static javax.persistence.CascadeType.ALL;

/**
 * This class contains the results of the tendency and emotion analysis.
 */
@Entity
public class Sentiment implements Comparable<Sentiment> {

  /**
   * Fallback sentiment when no sentiment is available.
   */
  public static final Sentiment DEFAULT_SENTIMENT
      = new Sentiment(ComplaintProperty.DEFAULT_PROPERTY, 0.0);

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private long id;

  @OneToOne(cascade = ALL)
  @NonNull
  private ComplaintProperty emotion = ComplaintProperty.DEFAULT_PROPERTY;
  private double tendency;

  @JsonCreator
  public Sentiment(@NonNull ComplaintProperty emotion, double tendency) {
    this.emotion = emotion;
    this.tendency = tendency;
  }

  public Sentiment() {
    // for hibernate
  }

  @NonNull
  public ComplaintProperty getEmotion() {
    return emotion;
  }

  public double getTendency() {
    return tendency;
  }

  public Sentiment withEmotion(ComplaintProperty emotionProperty) {
    return new Sentiment(emotionProperty, this.tendency);
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
