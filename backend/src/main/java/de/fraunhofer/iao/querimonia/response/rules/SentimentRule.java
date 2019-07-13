package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;

import java.util.List;

public class SentimentRule implements Rule {

  private final double min;
  private final double max;

  public SentimentRule(double min, double max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    return complaint.getSentiment() >= min && complaint.getSentiment() <= max;
  }
}
