package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.nlp.response.ComplaintData;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;

import java.util.List;

public interface Rule {

  boolean isRespected(ComplaintData complaint,
                      List<CompletedResponseComponent> currentResponseState);

  boolean isPotentiallyRespected(ComplaintData complaint);
}
