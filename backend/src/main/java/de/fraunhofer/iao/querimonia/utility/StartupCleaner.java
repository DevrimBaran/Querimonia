package de.fraunhofer.iao.querimonia.utility;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.utility.log.LogCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * This class is a service that runs on startup. It is used to find all complaints with state
 * {@link ComplaintState#ANALYSING} and puts them to the error state with a log message that the
 * analysis couldn't be finished successfully.
 */
@Service
public class StartupCleaner {

  private final ComplaintManager complaintManager;

  @Autowired
  public StartupCleaner(ComplaintManager complaintManager) {
    this.complaintManager = complaintManager;
  }

  @PostConstruct
  public void clearAnalyzingComplaints() {
    var toClearUp = complaintManager.getComplaintsWithState(ComplaintState.ANALYSING);
    toClearUp.stream()
        .map(ComplaintBuilder::new)
        .peek(complaintBuilder -> complaintBuilder.setState(ComplaintState.ERROR))
        .peek(complaintBuilder -> complaintBuilder.appendLogItem(LogCategory.ERROR, "Analyse "
            + "wurde unterbrochen"))
        .map(ComplaintBuilder::createComplaint)
        .forEach(complaintManager::storeComplaint);
  }
}
