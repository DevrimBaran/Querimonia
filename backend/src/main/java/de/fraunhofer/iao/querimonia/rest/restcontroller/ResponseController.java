package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.CompletedResponseComponentRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.rest.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
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

  private final ComplaintManager complaintManager;

  public ResponseController(FileStorageService fileStorageService,
                            ComplaintRepository complaintRepository,
                            TemplateRepository templateRepository,
                            CompletedResponseComponentRepository
                                completedResponseComponentRepository) {
    this.complaintManager = new ComplaintManager(fileStorageService, complaintRepository,
        templateRepository, completedResponseComponentRepository);
  }

  /**
   * Returns the response of the complaint with the given id.
   *
   * @param complaintId the Id of the complaint to respond to
   * @return the new response
   */
  @GetMapping("api/responses/{complaintId}")
  public ResponseEntity<ResponseSuggestion> getResponse(@PathVariable int complaintId) {
    Complaint complaint = complaintManager.getComplaint(complaintId);

    return new ResponseEntity<>(complaint.getResponseSuggestion(), HttpStatus.OK);
  }

  /**
   * Returns the response as plain text of the complaint with the given id.
   *
   * @param complaintId the Id of the complaint to respond to
   * @return the response as plain string
   */
  @GetMapping("api/responses/plain/{complaintId}")
  public String getPlainResponse(@PathVariable int complaintId) {
    return Objects.requireNonNull(getResponse(complaintId)
        .getBody())
        .getResponseComponents()
        .stream()
        .map(completedResponseComponent -> completedResponseComponent.getAlternatives()
            .get(0).getCompletedText())
        .collect(Collectors.joining());
  }

  @PatchMapping("api/responses/{complaintId}/refresh")
  public ResponseEntity<ResponseSuggestion> refreshResponse(@PathVariable int complaintId) {
    return new ResponseEntity<>(complaintManager.refreshResponse(complaintId), HttpStatus.OK);
  }
}
