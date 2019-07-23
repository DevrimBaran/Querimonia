package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import de.fraunhofer.iao.querimonia.rest.manager.ResponseComponentManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


/**
 * REST Controller for creating, viewing and deleting Response Components. Serves as the REST API
 * for
 * the ResponseComponentManager.
 *
 * @author Simon Weiler
 * @author Baran Demir
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ResponseComponentController {

  private final ResponseComponentManager responseComponentManager;

  public ResponseComponentController(ResponseComponentManager responseComponentManager) {
    this.responseComponentManager = responseComponentManager;
  }


  /**
   * Add a new component to the repository.
   *
   * @return the created component
   */
  @PostMapping("api/components")
  public ResponseEntity<?> addComponent(@RequestBody ResponseComponent responseComponent) {
    return ControllerUtility.tryAndCatch(() ->
        responseComponentManager.addComponent(responseComponent));
  }


  /**
   * Add a set of default components to the repository.
   *
   * @return the list of default components
   */
  @PostMapping("api/components/default")
  public ResponseEntity<?> addDefaultComponents() {
    return ControllerUtility.tryAndCatch(responseComponentManager::addDefaultComponents);
  }

  /**
   * Pagination for components (sort_by, page, count).
   *
   * @param count    Counter for the components.
   * @param page     Page number.
   * @param keyWords Keywords of the component texts.
   * @param sortBy   Sorts by name ascending or descending, priority ascending and descending.
   *
   * @return Returns a list of sorted components.
   */
  @GetMapping("api/components")
  public ResponseEntity<?> getAllComponents(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("keywords") Optional<String[]> keyWords
  ) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
        .getAllComponents(count, page, sortBy, keyWords));
  }

  /**
   * Returns the number of saved components.
   * @param keyWords if given, only counts components containing these keywords
   * @return number of saved components
   */
  @GetMapping("api/components/count")
  public ResponseEntity<?> getComponentCount(
      @RequestParam("keywords") Optional<String[]> keyWords) {
    return ControllerUtility.tryAndCatch(() ->
        responseComponentManager.getAllComponents(Optional.empty(),
            Optional.empty(), Optional.empty(), keyWords).size());
  }


  /**
   * Find the component with the given ID.
   *
   * @param id the ID to look for
   *
   * @return the response component with the given ID
   */
  @GetMapping("api/components/{id}")
  public ResponseEntity<?> getComponentByID(@PathVariable int id) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
        .getComponentByID(id));
  }

  /**
   * Delete the component with the given ID.
   *
   * @param id the ID of the component to delete
   */
  @DeleteMapping("api/components/{id}")
  public ResponseEntity<?> deleteComponent(@PathVariable long id) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager.deleteComponent(id));
  }

  /**
   * Deletes all components from the database.
   */
  @DeleteMapping("api/components/all")
  public ResponseEntity<?> deleteAllComponents() {
    return ControllerUtility.tryAndCatch(responseComponentManager::deleteAllComponents);
  }

  /**
   * Updates components by ID that are already in the database.
   *
   * @param componentId       ID of the component to update
   * @param responseComponent updated version of the component
   */
  @PutMapping("api/components/{componentId}")
  public ResponseEntity<?> updateComponent(@PathVariable long componentId,
                                           @RequestBody ResponseComponent responseComponent) {
    return ControllerUtility.tryAndCatch(
        () -> responseComponentManager.updateComponent(componentId, responseComponent));
  }
}
