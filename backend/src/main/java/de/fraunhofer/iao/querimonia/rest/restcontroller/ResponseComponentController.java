package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import de.fraunhofer.iao.querimonia.rest.manager.ResponseComponentManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * REST Controller for creating, viewing and deleting Response Templates. Serves as the REST API for
 * the ResponseTemplateManager.
 *
 * @author Simon Weiler
 * @author Baran Demir
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ResponseComponentController {

  private final TemplateRepository templateRepository;
  private final ResponseComponentManager responseComponentManager;

  public ResponseComponentController(TemplateRepository templateRepository) {
    this.templateRepository = templateRepository;
    responseComponentManager = new ResponseComponentManager();
  }


  /**
   * Add a new component to the repository.
   *
   * @return the created component
   */
  @PostMapping("api/templates")
  public ResponseEntity<?> addTemplate(
          @RequestBody ResponseComponent responseComponent) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
                    .addTemplate(templateRepository, responseComponent));
  }


  /**
   * Add a set of default templates to the repository.
   *
   * @return the list of default templates
   */
  @PostMapping("api/templates/default")
  public ResponseEntity<?> addDefaultTemplates() {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
            .addDefaultTemplates(templateRepository));
  }

  /**
   * Pagination for templates (sort_by, page, count).
   *
   * @param count  Counter for the templates.
   * @param page   Page number.
   * @param sortBy Sorts by name ascending or descending, priority ascending and descending.
   * @return Returns a list of sorted templates.
   */
  @GetMapping("api/templates")
  public ResponseEntity<?> getAllTemplates(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("keywords") Optional<String[]> keyWords
  ) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
            .getAllTemplates(templateRepository, count, page, sortBy, keyWords));
  }


  /**
   * Find the component with the given ID.
   *
   * @param id the ID to look for
   * @return the response component with the given ID
   */
  @GetMapping("api/templates/{id}")
  public ResponseEntity<?> getTemplateByID(@PathVariable int id) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
            .getTemplateByID(templateRepository, id));
  }

  /**
   * Delete the component with the given ID.
   *
   * @param id the ID of the component to delete
   */
  @DeleteMapping("api/templates/{id}")
  public ResponseEntity<?> deleteTemplate(@PathVariable int id) {
    return ControllerUtility.tryAndCatch(() -> templateRepository.deleteById(id));
  }

  /**
   * Deletes all templates from the database.
   */
  @DeleteMapping("api/templates/all")
  public ResponseEntity<?> deleteAllTemplates() {
    return ControllerUtility.tryAndCatch((Runnable) templateRepository::deleteAll);
  }

  /**
   * Updates templates by ID that are already in the database.
   *
   * @param templateId        Is the component ID.
   * @param responseComponent Is the component itself.
   */
  @PutMapping("api/templates/{templateId}")
  public ResponseEntity<?> updateTemplate(@PathVariable int templateId,
                             @RequestBody ResponseComponent responseComponent) {
    return ControllerUtility.tryAndCatch(() -> {
      responseComponent.setComponentId(templateId);
      templateRepository.save(responseComponent); });
  }
}
