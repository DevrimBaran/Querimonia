package de.fraunhofer.iao.querimonia;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import de.fraunhofer.iao.querimonia.utility.log.LogCategory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.transaction.Transactional;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * The main class of the backend, which starts the server.
 */
@CrossOrigin(methods = {POST, PUT, PATCH, GET, DELETE})
@SpringBootApplication
@EnableConfigurationProperties(value = {FileStorageProperties.class})
@Transactional
public class Launcher {

  public static void main(String[] args) {
    var context = SpringApplication.run(Launcher.class, args);
    clearAnalyzingComplaints(context.getBean(ComplaintManager.class));
  }

  private static void clearAnalyzingComplaints(ComplaintManager complaintManager) {
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
