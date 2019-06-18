package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;

import java.util.List;

public class SentimentRule implements Rule {

  private String sentiment;

  public SentimentRule(String sentiment) {
    this.sentiment = sentiment;
  }

  @Override
  public boolean isRespected(Complaint complaint,
                             List<CompletedResponseComponent> currentResponseState) {
    return isPotentiallyRespected(complaint);
  }

  @Override
  public boolean isPotentiallyRespected(Complaint complaint) {
    return complaint.getBestSentiment()
        .map(sentiment::equals)
        .orElse(false);
  }
}
