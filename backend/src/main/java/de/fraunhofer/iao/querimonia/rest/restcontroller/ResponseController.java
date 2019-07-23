package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.rest.manager.ComplaintManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for generating, receiving and deleting responses.
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
   * @param complaintId the Id of the complaint to respond to
   *
   * @return the new response
   */
  @GetMapping("api/responses/{complaintId}")
  public ResponseEntity<?> getResponse(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() ->
        complaintManager.getComplaint(complaintId).getResponseSuggestion());
  }

  @PatchMapping("api/responses/{complaintId}/refresh")
  public ResponseEntity<?> refreshResponse(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.refreshResponse(complaintId));
  }
}
