package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseComponent;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseTemplateManager;
import de.fraunhofer.iao.querimonia.rest.restobjects.ResponseComponentUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * REST Controller for creating, viewing and deleting Response Templates. Serves as the REST API for
 * the ResponseTemplateManager.
 *
 * @author Simon Weiler
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ResponseTemplateController {

  private static final ResponseStatusException NOT_FOUNT_EXCEPTION
      = new ResponseStatusException(HttpStatus.NOT_FOUND, "Template does not exist!");
  private final TemplateRepository templateRepository;
  private ResponseTemplateManager responseTemplateManager;

  public ResponseTemplateController(TemplateRepository templateRepository) {
    this.templateRepository = templateRepository;
  }

  private void initialize() {
    responseTemplateManager = ResponseTemplateManager.instantiate();
  }

  /**
   * Add a new template to the repository.
   *
   * @return the created template
   */
  @PostMapping("api/templates")
  public ResponseComponent addTemplate(@RequestBody ResponseComponent responseComponent) {
    initialize();
    return responseTemplateManager.addTemplate(templateRepository, responseComponent);
  }

  @GetMapping("api/templates")
  public List<ResponseComponent> getAllTemplates() {
    return StreamSupport.stream(templateRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  /**
   * Find a template with the given subject.
   *
   * @param subject the subject to look for
   * @return a response template with the given subject
   */
  @GetMapping("api/templates/subjects/{subject}")
  public ResponseComponent getTemplateBySubject(@PathVariable String subject) {
    initialize();
    return responseTemplateManager.getTemplateBySubject(templateRepository, subject);
  }

  /**
   * Find the template with the given ID.
   *
   * @param id the ID to look for
   * @return the response template with the given ID
   */
  @GetMapping("api/templates/{id}")
  public ResponseComponent getTemplateByID(@PathVariable int id) {
    initialize();
    return responseTemplateManager.getTemplateByID(templateRepository, id);
  }

  /**
   * Delete the template with the given ID.
   *
   * @param id the ID of the template to delete
   */
  @DeleteMapping("api/templates/{id}")
  public void deleteTemplate(@PathVariable int id) {
    initialize();
    responseTemplateManager.deleteTemplateByID(templateRepository, id);
  }

  /**
   * Deletes all templates from the database.
   */
  @DeleteMapping("api/templates/all")
  public void deleteAllTemplates() {
    templateRepository.deleteAll();
  }

  /**
   * Updates a component in the database.
   *
   * @param templateId the id of the template that should be updated as path variable.
   * @param request    the update request.
   * @return the updated response component.
   */
  @PatchMapping("api/templates/{templateId}")
  public ResponseComponent updateComponent(@PathVariable int templateId,
                                           @RequestBody ResponseComponentUpdateRequest request) {
    ResponseComponent componentToUpdate = templateRepository.findById(templateId)
        .orElseThrow(() -> NOT_FOUNT_EXCEPTION);
    request.getSubject().ifPresent(componentToUpdate::setSubject);
    request.getResponsePart().ifPresent(componentToUpdate::setResponsePart);
    request.getSuccessorParts().ifPresent(componentToUpdate::setSuccessorParts);
    request.getTemplateText().ifPresent(componentToUpdate::setTemplateText);

    templateRepository.save(componentToUpdate);
    return componentToUpdate;
  }
}
