package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for generating and retrieving responses.
 *
 * @author Simon Weiler
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ResponseController {

  private final ComplaintManager complaintManager;

  public ResponseController(ComplaintManager complaintManager) {
    this.complaintManager = complaintManager;
  }

  /**
   * Returns the response of the complaint with the given id.
   *
   * @param complaintId id of the complaint
   *
   * @return response suggestion of the complaint
   */
  @GetMapping("api/responses/{complaintId}")
  public ResponseEntity<?> getResponse(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() ->
        complaintManager.getComplaint(complaintId).getResponseSuggestion());
  }

  /**
   * Refreshes the response for a complaint.
   *
   * @param complaintId id of the complaint
   *
   * @return the new response suggestion for the complaint
   */
  @PatchMapping("api/responses/{complaintId}/refresh")
  public ResponseEntity<?> refreshResponse(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.refreshResponse(complaintId));
  }

  /**
   * Returns the response of the complaint with the given id.
   *
   * @param complaintId id of the complaint
   *
   * @return response suggestion of the complaint
   */
  @GetMapping("api/complaints/{complaintId}/response")
  public ResponseEntity<?> getResponseNew(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() ->
        complaintManager.getComplaint(complaintId).getResponseSuggestion());
  }

  /**
   * Refreshes the response for a complaint.
   *
   * @param complaintId id of the complaint
   *
   * @return the new response suggestion for the complaint
   */
  @PatchMapping("api/complaints/{complaintId}/response/refresh")
  public ResponseEntity<?> refreshResponseNew(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.refreshResponse(complaintId));
  }

}
