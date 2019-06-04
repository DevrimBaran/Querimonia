package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseComponent;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseTemplateManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST Controller for creating, viewing and deleting Response Templates.
 * Serves as the REST API for the ResponseTemplateManager.
 *
 * @author Simon Weiler
 */
@RestController
public class ResponseTemplateController {

  private ResponseTemplateManager responseTemplateManager;

  private final TemplateRepository templateRepository;

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
}
