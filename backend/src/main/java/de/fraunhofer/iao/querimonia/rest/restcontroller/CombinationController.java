package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.combination.LineStopCombination;
import de.fraunhofer.iao.querimonia.db.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.db.manager.LineStopCombinationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This controller is used for combinations.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CombinationController {

  private final ComplaintManager complaintManager;
  private final LineStopCombinationManager lineStopCombinationManager;

  public CombinationController(
      ComplaintManager complaintManager,
      LineStopCombinationManager lineStopCombinationManager) {
    this.complaintManager = complaintManager;
    this.lineStopCombinationManager = lineStopCombinationManager;
  }

  /**
   * Returns the combinations of a complaint.
   *
   * @param complaintId the id of the complaint.
   *
   * @return a response entity with the combinations of success or else a response entity with
   *     the querimonia exception.
   */
  @GetMapping("api/combinations/{complaintId}")
  public ResponseEntity<?> getCombinationsOfComplaint(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.getCombinations(complaintId));
  }

  /**
   * Returns all combinations of the database.
   *
   * @return a response entity with the combinations of success or else a response entity with
   *     the querimonia exception.
   */
  @GetMapping("api/combinations")
  public ResponseEntity<?> getAllCombinations() {
    return ControllerUtility.tryAndCatch(() -> lineStopCombinationManager.getAllCombinations());
  }

  /**
   * Adds the combinations to the database.
   *
   * @param lineStopCombinations the list of the combinations.
   *
   * @return a response entity with status code 204 on success or else with the querimonia
   *     exception as response body.
   */
  @PostMapping("api/combinations")
  public ResponseEntity<?> addCombinations(
      @RequestBody List<LineStopCombination> lineStopCombinations) {
    return ControllerUtility.tryAndCatch(
        () -> lineStopCombinationManager.addLineStopCombinations(lineStopCombinations));
  }
}
