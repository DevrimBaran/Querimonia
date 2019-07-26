package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.rest.manager.ComplaintManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
