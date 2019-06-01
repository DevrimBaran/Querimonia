package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;

import de.fraunhofer.iao.querimonia.response.ResponseSuggestion;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST Controller for generating, receiving and deleting responses.
 *
 * @author Simon Weiler
 */
@RestController
public class ResponseController {

  private final ComplaintRepository complaintRepository;

  public ResponseController(ComplaintRepository complaintRepository) {
    this.complaintRepository = complaintRepository;
  }

  /**
   * Generate a new response for the complaint with the given complaintId.
   *
   * @param complaintId the Id of the complaint to respond to
   * @return the new response
   */
  @GetMapping("api/responses/{complaintId}")
  public ResponseSuggestion generateResponse(@PathVariable int complaintId) {
    Optional<Complaint> complaint = complaintRepository.findById(complaintId);

    // Only respond to existing complaints
    if (!complaint.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint does not exist");
    }

    return complaint.get().getResponseSuggestion();
  }

}
