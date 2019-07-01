package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for generating, receiving and deleting responses.
 *
 * @author Simon Weiler
 */
// TODO add patch endpoint and actions to responses
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ResponseController {

  private final ComplaintRepository complaintRepository;

  public ResponseController(ComplaintRepository complaintRepository) {
    this.complaintRepository = complaintRepository;
  }

  /**
   * Returns the response of the complaint with the given id.
   *
   * @param complaintId the Id of the complaint to respond to
   * @return the new response
   */
  @GetMapping("api/responses/{complaintId}")
  public List<CompletedResponseComponent> getResponse(@PathVariable int complaintId) {
    Optional<Complaint> complaint = complaintRepository.findById(complaintId);

    // Only respond to existing complaints
    if (!complaint.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint does not exist");
    }

    return complaint.get().getResponseSuggestion().getResponseComponents();
  }

  /**
   * Returns the response as plain text of the complaint with the given id.
   *
   * @param complaintId the Id of the complaint to respond to
   * @return the response as plain string
   */
  @GetMapping(("api/responses/plain/{complaintId}"))
  public String getPlainResponse(@PathVariable int complaintId) {
    return getResponse(complaintId)
        .stream()
        .map(completedResponseComponent -> completedResponseComponent.getAlternatives()
            .get(0).getCompletedText())
        .collect(Collectors.joining());
  }

}
