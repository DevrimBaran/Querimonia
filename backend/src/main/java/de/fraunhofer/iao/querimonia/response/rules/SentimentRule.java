package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * A rule that checks if the sentiment value of a complaint is in a certain range or if the
 * complaint contains a specific emotion.
 */
public class SentimentRule implements Rule {

  private final double min;
  private final double max;
  @Nullable
  private final String emotion;

  public SentimentRule(double min, double max, @Nullable String emotion) {
    this.min = min;
    this.max = max;
    this.emotion = emotion;
  }

  @Override
  public boolean isRespected(ComplaintBuilder complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintBuilder complaint) {
    return complaint.getSentiment().getTendency() >= min
        && complaint.getSentiment().getTendency() <= max
        && (emotion == null || complaint.getSentiment().getEmotion().getValue().matches(emotion));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SentimentRule that = (SentimentRule) o;

    return new EqualsBuilder()
        .append(min, that.min)
        .append(max, that.max)
        .append(emotion, that.emotion)
        .isEquals();
  }
}
