package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.manager.CombinationManager;
import de.fraunhofer.iao.querimonia.db.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.db.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.db.manager.ResponseComponentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller contains general endpoints that dont belong to any category.
 */
@RestController
public class GeneralController {

  private final ComplaintManager complaintManager;
  private final ConfigurationManager configurationManager;
  private final CombinationManager combinationManager;
  private final ResponseComponentManager responseComponentManager;

  /**
   * Creates a new controller. This is called by spring.
   */
  @Autowired
  public GeneralController(
      ComplaintManager complaintManager,
      ConfigurationManager configurationManager,
      CombinationManager combinationManager,
      ResponseComponentManager componentManager) {
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
      complaintManager.deleteAllComplaints();
      configurationManager.deleteAllConfigurations();
      combinationManager.deleteAllCombinations();
      responseComponentManager.deleteAllComponents();

      configurationManager.addDefaultConfigurations();
      responseComponentManager.addDefaultComponents();
      combinationManager.addDefaultCombinations();
    });
  }

}
