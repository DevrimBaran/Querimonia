package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.response.ResponseComponent;
import de.fraunhofer.iao.querimonia.response.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.response.ResponseSuggestionFactory;
import de.fraunhofer.iao.querimonia.response.ResponseTemplateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for generating, receiving and deleting responses.
 *
 * @author Simon Weiler
 */
@RestController
public class ResponseController {

  @Autowired
  ResponseRepository responseRepository;

  @Autowired
  TemplateRepository templateRepository;

  @Autowired
  ComplaintRepository complaintRepository;

  /**
   * Generate a new response for the complaint with the given id.
   *
   * @param id the id of the complaint to respond to
   * @return the new response
   */
  @PostMapping("api/responses/new/{id}")
  public ResponseSuggestion generateResponse(@PathVariable int id) {
    Complaint complaint = complaintRepository.findById(id).orElse(null);

    // Only respond to existing complaints
    if (complaint == null) {
      return null;
    }

    ResponseTemplateManager responseTemplateManager = ResponseTemplateManager.instantiate();
    // TODO fix
    ResponseComponent responseTemplate =
        responseTemplateManager.getTemplateBySubject(templateRepository, complaint.getSubject().keySet()
        .stream().findAny().orElse("Sonstiges"));
    ResponseSuggestion responseSuggestion =
        ResponseSuggestionFactory.createResponseSuggestionWithDate(complaint, responseTemplate);
    responseRepository.save(responseSuggestion);
    return responseSuggestion;
  }

  /**
   * Find the response with the given id.
   *
   * @param id an id of a response
   * @return the response with the given id
   */
  @GetMapping("api/responses/{id}")
  public ResponseSuggestion getResponse(@PathVariable int id) {
    return responseRepository.findById(id).orElse(null);
  }

  /**
   * Delete the response with the given ID.
   *
   * @param id the ID of the response to delete
   */
  @DeleteMapping("api/responses/{id}")
  public void deleteResponse(@PathVariable int id) {
    responseRepository.deleteById(id);
  }
}
