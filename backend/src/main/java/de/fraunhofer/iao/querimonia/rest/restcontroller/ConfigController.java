package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.config.Configuration;
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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ConfigController {

  @GetMapping("api/config")
  public ResponseEntity<Configuration> getConfigurations(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy) {

    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @PostMapping("api/config")
  public ResponseEntity<Configuration> addConfiguration(@RequestBody Configuration configuration) {

    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @GetMapping("api/config/{configId}")
  public ResponseEntity<Configuration> getConfiguration(@PathVariable int configId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @DeleteMapping("api/config/{configId}")
  public ResponseEntity<Configuration> deleteConfiguration(@PathVariable int configId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @PutMapping("api/config/{configId}")
  public ResponseEntity<Configuration> updateConfiguration(
      @PathVariable int configId,
      @RequestBody Configuration configuration) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @GetMapping("api/config/count")
  public ResponseEntity<Integer> countConfigurations() {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @GetMapping("api/config/current")
  public ResponseEntity<Configuration> getCurrentConfiguration() {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @PutMapping("api/config/current")
  public ResponseEntity<Configuration> updateCurrentConfiguration(@RequestParam int configId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @DeleteMapping("api/config/all")
  public ResponseEntity<Configuration> deleteAllConfigurations() {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
