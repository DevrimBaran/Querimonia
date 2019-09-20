package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Combination;
import de.fraunhofer.iao.querimonia.manager.CombinationManager;
import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import org.springframework.http.HttpStatus;
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
  private final CombinationManager combinationManager;

  public CombinationController(
      ComplaintManager complaintManager,
      CombinationManager combinationManager) {
    this.complaintManager = complaintManager;
    this.combinationManager = combinationManager;
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
    return ControllerUtility.tryAndCatch(combinationManager::getAllCombinations);
  }

  /**
   * Adds the combinations to the database.
   *
   * @param lineStopCombinations the list of the combinations.
   *
   * @return a response entity with status code 201 on success or else with the querimonia
   *     exception as response body.
   */
  @PostMapping("api/combinations")
  public ResponseEntity<?> addCombinations(
      @RequestBody List<Combination> lineStopCombinations) {
    return ControllerUtility.tryAndCatch(
        () -> combinationManager.addLineStopCombinations(lineStopCombinations),
        HttpStatus.CREATED);
  }

  /**
   * Deletes all combinations of the database.
   *
   * @return a response entity with status code 204 on success or else with status code 500 and
   * the querimonia exception as response body.
   */
  @DeleteMapping("api/combinations/all")
  public ResponseEntity<?> deleteAllCombinations() {
    return ControllerUtility.tryAndCatch(combinationManager::deleteAllCombinations);
  }

  /**
   * Adds example combinations to the database.
   *
   * @return a response entity containing the added combinations with status code 200 on success
   *     or the exception with status code 500 on an unexpected error.
   */
  @PostMapping("api/combinations/default")
  public ResponseEntity<?> addDefaultCombinations() {
    return ControllerUtility.tryAndCatch(combinationManager::addDefaultCombinations);
  }
}
