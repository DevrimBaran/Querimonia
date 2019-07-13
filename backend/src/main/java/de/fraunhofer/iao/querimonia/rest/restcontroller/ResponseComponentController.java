package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.ResponseComponentRepository;
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

  private final ResponseComponentRepository componentRepository;
  private final ResponseComponentManager responseComponentManager;

  public ResponseComponentController(ResponseComponentRepository componentRepository) {
    this.componentRepository = componentRepository;
    responseComponentManager = new ResponseComponentManager();
  }


  /**
   * Add a new component to the repository.
   *
   * @return the created component
   */
  @PostMapping("api/templates")
  public ResponseEntity<?> addComponent(@RequestBody ResponseComponent responseComponent) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
        .addTemplate(componentRepository, responseComponent));
  }


  /**
   * Add a set of default components to the repository.
   *
   * @return the list of default components
   */
  @PostMapping("api/templates/default")
  public ResponseEntity<?> addDefaultComponents() {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
        .addDefaultTemplates(componentRepository));
  }

  /**
   * Pagination for components (sort_by, page, count).
   *
   * @param count    Counter for the templates.
   * @param page     Page number.
   * @param keyWords Keywords of the template texts.
   * @param sortBy   Sorts by name ascending or descending, priority ascending and descending.
   *
   * @return Returns a list of sorted templates.
   */
  @GetMapping("api/templates")
  public ResponseEntity<?> getAllComponents(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("keywords") Optional<String[]> keyWords
  ) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
        .getAllTemplates(componentRepository, count, page, sortBy, keyWords));
  }

  @GetMapping("api/templates/count")
  public ResponseEntity<?> getComponentCount(
      @RequestParam("keywords") Optional<String[]> keyWords) {
    return ControllerUtility.tryAndCatch(() ->
        responseComponentManager.getAllTemplates(componentRepository, Optional.empty(),
            Optional.empty(), Optional.empty(), keyWords).size());
  }


  /**
   * Find the component with the given ID.
   *
   * @param id the ID to look for
   *
   * @return the response component with the given ID
   */
  @GetMapping("api/templates/{id}")
  public ResponseEntity<?> getComponentByID(@PathVariable int id) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
        .getTemplateByID(componentRepository, id));
  }

  /**
   * Delete the component with the given ID.
   *
   * @param id the ID of the component to delete
   */
  @DeleteMapping("api/templates/{id}")
  public ResponseEntity<?> deleteComponent(@PathVariable long id) {
    return ControllerUtility.tryAndCatch(() -> componentRepository.deleteById(id));
  }

  /**
   * Deletes all templates from the database.
   */
  @DeleteMapping("api/templates/all")
  public ResponseEntity<?> deleteAllTemplates() {
    return ControllerUtility.tryAndCatch((Runnable) componentRepository::deleteAll);
  }

  /**
   * Updates components by ID that are already in the database.
   *
   * @param componentId       Is the component ID.
   * @param responseComponent Is the component itself.
   */
  @PutMapping("api/templates/{componentId}")
  public ResponseEntity<?> updateTemplate(@PathVariable long componentId,
                                          @RequestBody ResponseComponent responseComponent) {
    return ControllerUtility.tryAndCatch(() -> {
      responseComponent.setComponentId(componentId);
      componentRepository.save(responseComponent);
      return responseComponent;
    });
  }
}
