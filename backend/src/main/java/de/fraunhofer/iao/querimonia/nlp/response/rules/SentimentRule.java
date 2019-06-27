package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintUtility;
import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.generation.CompletedResponseComponent;

import java.util.List;

public class SentimentRule implements Rule {

  private String sentiment;

  public SentimentRule(String sentiment) {
    this.sentiment = sentiment;
  }

  @Override
  public boolean isRespected(ComplaintData complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(ComplaintData complaint) {
    return ComplaintUtility.getEntryWithHighestProbability(complaint.getSentimentMap())
        .map(sentiment::equals)
        .orElse(false);
  }
}
