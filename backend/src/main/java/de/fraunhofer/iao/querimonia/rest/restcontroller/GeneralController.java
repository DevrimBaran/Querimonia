package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.manager.CombinationManager;
import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.manager.ResponseComponentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller contains general endpoints that dont belong to any category.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GeneralController {

  private static final Logger logger = LoggerFactory.getLogger(GeneralController.class);

  private final ComplaintManager complaintManager;
  private final ConfigurationManager configurationManager;
  private final CombinationManager combinationManager;
  private final ResponseComponentManager responseComponentManager;
  @Autowired SimpMessagingTemplate template;
  /**
   * Creates a new controller. This is called by spring.
   */
  @Autowired
  public GeneralController(
      ComplaintManager complaintManager,
      ConfigurationManager configurationManager,
      CombinationManager combinationManager,
      ResponseComponentManager componentManager
  ) {
    this.complaintManager = complaintManager;
    this.configurationManager = configurationManager;
    this.combinationManager = combinationManager;
    this.responseComponentManager = componentManager;
  }

  /**
   * Resets the system. The database gets cleared and default combinations, components and
   * configurations get added to the system.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 204 on success</li>
   *       <li>status code 500 and the exception as body on an unexpected server error</li>
   *     </ul>
   */
  @PostMapping("api/reset")
  public ResponseEntity<?> reset() {
    return ControllerUtility.tryAndCatch(() -> {
      logger.info("Starting system reset...");
      complaintManager.deleteAllComplaints();
      configurationManager.deleteAllConfigurations();
      combinationManager.deleteAllCombinations();
      responseComponentManager.deleteAllComponents();

      responseComponentManager.addDefaultComponents();
      combinationManager.addDefaultCombinations();
      configurationManager.addDefaultConfigurations();
    });
  }

  @PostMapping("api/sendMessageTest")
  public void send() {
    template.convertAndSend("/complaints/state","Test message");
  }

}
