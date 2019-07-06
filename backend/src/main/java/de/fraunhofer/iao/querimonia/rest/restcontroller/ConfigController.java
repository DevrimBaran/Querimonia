package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.db.repositories.ConfigurationRepository;
import de.fraunhofer.iao.querimonia.property.AnalyzerConfigProperties;
import de.fraunhofer.iao.querimonia.rest.manager.ConfigurationManager;
import org.springframework.http.HttpStatus;
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
 * This controller is used to manage the configurations of Querimonia.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ConfigController {

  private ConfigurationManager configurationManager;

  public ConfigController(AnalyzerConfigProperties analyzerConfigProperties,
                          ConfigurationRepository configurationRepository) {

    this.configurationManager = new ConfigurationManager(analyzerConfigProperties,
        configurationRepository);
  }

  @GetMapping("api/config")
  public ResponseEntity<?> getConfigurations(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy) {

    return ControllerUtility.tryAndCatch(() ->
        configurationManager.getConfigurations(count, page, sortBy));
  }

  @PostMapping("api/config")
  public ResponseEntity<?> addConfiguration(@RequestBody Configuration configuration) {

    return ControllerUtility.tryAndCatch(() ->
        configurationManager.addConfiguration(configuration), HttpStatus.CREATED);
  }

  @GetMapping("api/config/{configId}")
  public ResponseEntity<?> getConfiguration(@PathVariable int configId) {
    return ControllerUtility.tryAndCatch(() ->
        configurationManager.getConfiguration(configId));
  }

  @DeleteMapping("api/config/{configId}")
  public ResponseEntity<?> deleteConfiguration(@PathVariable int configId) {
    return ControllerUtility.tryAndCatch(() ->
        configurationManager.deleteConfiguration(configId));
  }

  @PutMapping("api/config/{configId}")
  public ResponseEntity<?> updateConfiguration(
      @PathVariable int configId,
      @RequestBody Configuration configuration) {
    return ControllerUtility.tryAndCatch(() ->
        configurationManager.updateConfiguration(configId, configuration));
  }

  @GetMapping("api/config/count")
  public ResponseEntity<?> countConfigurations() {
    return ControllerUtility.tryAndCatch(configurationManager::countConfigurations);
  }

  @GetMapping("api/config/current")
  public ResponseEntity<?> getCurrentConfiguration() {
    return ControllerUtility.tryAndCatch(configurationManager::getCurrentConfiguration);
  }

  @PutMapping("api/config/current")
  public ResponseEntity<?> updateCurrentConfiguration(@RequestParam int configId) {
    return ControllerUtility.tryAndCatch(() ->
        configurationManager.updateCurrentConfiguration(configId));
  }

  @DeleteMapping("api/config/all")
  public ResponseEntity<?> deleteAllConfigurations() {
    return ControllerUtility.tryAndCatch(configurationManager::deleteAllConfigurations);
  }
}
