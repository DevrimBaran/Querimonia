package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.manager.ResponseComponentManager;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST Controller for creating, viewing and deleting Response Components. Serves as the REST API
 * fo the {@link ResponseComponentManager}.
 *
 * <p>For more information about the endpoints, also see
 * <a href="https://querimonia.iao.fraunhofer.de/inf/swagger-ui/#/components">the swagger
 * documentation.</a></p>
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ResponseComponentController {

  private final ResponseComponentManager responseComponentManager;

  /**
   * This constructor is only called by spring.
   *
   * @param responseComponentManager the manager for the components.
   */
  @Autowired
  ResponseComponentController(ResponseComponentManager responseComponentManager) {
    this.responseComponentManager = responseComponentManager;
  }

  /**
   * Add a new component to the database.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 on success with the new component as body.</li>
   *       <li>status code 400 when the rules or complaint text are malformed.</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @PostMapping("api/components")
  public ResponseEntity<?> addComponent(@RequestBody ResponseComponent responseComponent) {
    return ControllerUtility.tryAndCatch(() ->
        responseComponentManager.addComponent(responseComponent));
  }

  /**
   * Add a set of default components to the repository.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 on success with the default components as body.</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @PostMapping("api/components/default")
  public ResponseEntity<?> addDefaultComponents() {
    return ControllerUtility.tryAndCatch(responseComponentManager::addDefaultComponents);
  }

  /**
   * Returns all components that match the given filter, sorting and pagination parameters.
   *
   * @param count    The amount of components that should be returned. By default, all get returned.
   * @param page     Page number, by default this is 0. This is ignored when count is not given.
   * @param keyWords If given, only components that contain the given keywords will be returned.
   * @param sortBy   Sorts by name ascending or descending, priority ascending and descending. By
   *                 default, the components will be sorted by id.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 on success with the components that match the parameters as body.
   *       </li>
   *       <li>status code 400 on illegal parameters.</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @GetMapping("api/components")
  public ResponseEntity<?> getAllComponents(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("action_code") Optional<String[]> actionCode,
      @RequestParam("keywords") Optional<String[]> keyWords
  ) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager
        .getAllComponents(count, page, sortBy, actionCode, keyWords));
  }


  /**
   * Returns the number of saved components.
   *
   * @param keyWords   if given, only counts components containing these keywords
   * @param actionCode if given, only counts components containing an action with these Action-code
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 on success with the count as body.</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @GetMapping("api/components/count")
  public ResponseEntity<?> getComponentCount(
      @RequestParam("keywords") Optional<String[]> keyWords,
      @RequestParam("action_code") Optional<String[]> actionCode) {
    return ControllerUtility.tryAndCatch(() ->
        "" + responseComponentManager.getAllComponents(Optional.empty(),
            Optional.empty(), Optional.empty(), actionCode, keyWords).size());
  }


  /**
   * Find the component with the given ID.
   *
   * @param id the ID to look for
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 on success with the new component as body</li>
   *       <li>status code 404 when no component with that id exists.</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
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
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 204 on success</li>
   *       <li>status code 404 when no component with that id exists</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @DeleteMapping("api/components/{id}")
  public ResponseEntity<?> deleteComponent(@PathVariable long id) {
    return ControllerUtility.tryAndCatch(() -> responseComponentManager.deleteComponent(id));
  }

  /**
   * Deletes all components from the database.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 204 on success</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @DeleteMapping("api/components/all")
  public ResponseEntity<?> deleteAllComponents() {
    return ControllerUtility.tryAndCatch(responseComponentManager::deleteAllComponents);
  }

  /**
   * Updates components by ID that are already in the database.
   *
   * @param componentId       ID of the component to update
   * @param responseComponent updated version of the component.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 on success with the new component as body</li>
   *       <li>status code 404 when no component with that id exists</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @PutMapping("api/components/{componentId}")
  public ResponseEntity<?> updateComponent(@PathVariable long componentId,
                                           @RequestBody ResponseComponent responseComponent) {
    return ControllerUtility.tryAndCatch(
        () -> responseComponentManager.updateComponent(componentId, responseComponent));
  }
}
