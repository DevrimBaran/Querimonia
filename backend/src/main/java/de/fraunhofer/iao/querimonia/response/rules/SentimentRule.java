package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.springframework.lang.Nullable;

import java.util.List;

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
}
