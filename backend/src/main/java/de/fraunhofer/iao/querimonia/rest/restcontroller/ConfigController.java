package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.manager.ConfigurationManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * This controller is used to manage the configurations of Querimonia.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ConfigController {

  private final ConfigurationManager configurationManager;

  /**
   * Creates new config controller.
   *
   * @param configurationManager the manager for the configurations.
   */
  public ConfigController(ConfigurationManager configurationManager) {

    this.configurationManager = configurationManager;
  }

  /**
   * Returns all configurations of the database. Sorting and pagination can be used.
   *
   * @param count  the number of elements per page.
   * @param page   the page number.
   * @param sortBy the sort parameters. Available sort parameters can be found in OpenAPI.yaml file.
   *
   * @return a response entity with:
   *     <ul>
   *     <li>status code 200 and all configurations that match the parameters on success.</li>
   *     <li>status code 400 on invalid parameters.</li>
   *     <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @GetMapping("api/config")
  public ResponseEntity<?> getConfigurations(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy) {

    return ControllerUtility.tryAndCatch(() ->
        configurationManager.getConfigurations(count, page, sortBy));
  }

  /**
   * Adds a new configuration to the database.
   *
   * @param configuration the configuration that should be added.
   *
   * @return a response entity with:
   *     <ul>
   *     <li>status code 201 with the new created configuration on success.</li>
   *     <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @PostMapping("api/config")
  public ResponseEntity<?> addConfiguration(@RequestBody Configuration configuration) {

    return ControllerUtility.tryAndCatch(() ->
        configurationManager.addConfiguration(configuration), HttpStatus.CREATED);
  }

  /**
   * Returns the configuration with the given id.
   *
   * @param configId the id of the configuration.
   *
   * @return a response entity with:
   *     <ul>
   *     <li>status code 200 and the configuration with the given id on success.</li>
   *     <li>status code 404 when the configuration id does not exist.</li>
   *     <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @GetMapping("api/config/{configId}")
  public ResponseEntity<?> getConfiguration(@PathVariable int configId) {
    return ControllerUtility.tryAndCatch(() ->
        configurationManager.getConfiguration(configId));
  }

  /**
   * Deletes a configuration with the given id.,,
   *
   * @param configId the id of the configuration that should be deleted.
   *
   * @return a response entity with:
   *     <ul>
   *     <li>status code 201 on success.</li>
   *     <li>status code 404 when the configuration id does not exist.</li>
   *     <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @DeleteMapping("api/config/{configId}")
  public ResponseEntity<?> deleteConfiguration(@PathVariable int configId) {
    return ControllerUtility.tryAndCatch(() ->
        configurationManager.deleteConfiguration(configId));
  }

  /**
   * Puts a given configuration with the given id in the database.
   *
   * @param configId      the id of the configuration
   * @param configuration the new configuration.
   *
   * @return the updated configuration.
   */
  @PutMapping("api/config/{configId}")
  public ResponseEntity<?> updateConfiguration(
      @PathVariable int configId,
      @RequestBody Configuration configuration) {
    return ControllerUtility.tryAndCatch(() ->
        configurationManager.updateConfiguration(configId, configuration));
  }

  /**
   * Returns the count of the configurations saved in the database.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 and the count of the configurations on success</li>
   *       <li>status code 500 on an unexpected server error</li>
   *     </ul>
   */
  @GetMapping("api/config/count")
  public ResponseEntity<?> countConfigurations() {
    return ControllerUtility.tryAndCatch(() ->
        Long.toString(configurationManager.countConfigurations()));
  }

  /**
   * Returns the currently active configuration.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 and the current configuration on success.</li>
   *       <li>status code 500 on an unexpected server error</li>
   *     </ul>
   */
  @GetMapping("api/config/current")
  public ResponseEntity<?> getCurrentConfiguration() {
    return ControllerUtility.tryAndCatch(configurationManager::getCurrentConfiguration);
  }

  /**
   * Sets the active configuration to the configuration with the given id.
   *
   * @param configId the id of the configuration that should be used
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 and the current configuration on success.</li>
   *       <li>status code 404 if no configuration with the given id exists.</li>
   *       <li>status code 500 on an unexpected server error</li>
   *     </ul>
   */
  @PutMapping("api/config/current")
  public ResponseEntity<?> updateCurrentConfiguration(@RequestParam int configId) {
    return ControllerUtility.tryAndCatch(() ->
        configurationManager.updateCurrentConfiguration(configId));
  }

  /**
   * Adds example configurations to the database.
   *
   * @return a response entity with:
   *    <ul>
   *      <li>status code 200 and the added configurations</li>
   *      <li>status code 500 on an unexpected server error</li>
   *    </ul>
   */
  @PostMapping("api/config/default")
  public ResponseEntity<?> addDefaultConfigurations() {
    return ControllerUtility.tryAndCatch(configurationManager::addDefaultConfigurations);
  }

  /**
   * Deletes all configuration of the database.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 204 on success.</li>
   *       <li>status code 500 on an unexpected server error</li>
   *     </ul>
   */
  @DeleteMapping("api/config/all")
  public ResponseEntity<?> deleteAllConfigurations() {
    return ControllerUtility.tryAndCatch(configurationManager::deleteAllConfigurations);
  }

  /**
   * Returns the configuration that is currently active.
   *
   * @return Extractors listed by tool, pipeline and domain
   */
  @GetMapping("api/config/allExtractors")
  public ResponseEntity<?> getAllExtractors() {
    return ControllerUtility.tryAndCatch(configurationManager::getAllExtractors);
  }
}
