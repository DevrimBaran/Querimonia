package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseComponent;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseTemplateManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

}
