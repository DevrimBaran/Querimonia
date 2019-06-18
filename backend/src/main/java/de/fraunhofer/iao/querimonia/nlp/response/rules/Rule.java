package de.fraunhofer.iao.querimonia.nlp.response.rules;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;

import java.util.List;

public interface Rule {

  boolean isRespected(Complaint complaint, List<CompletedResponseComponent> currentResponseState);

  boolean isPotentiallyRespected(Complaint complaint);
}
