package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import de.fraunhofer.iao.querimonia.rest.manager.ResponseComponentManager;
import de.fraunhofer.iao.querimonia.rest.manager.filter.ResponseComponentFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * REST Controller for creating, viewing and deleting Response Templates. Serves as the REST API for
 * the ResponseTemplateManager.
 *
 * @author Simon Weiler
 * @author Baran Demir
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ResponseComponentController {

  private static final ResponseStatusException NOT_FOUNT_EXCEPTION
      = new ResponseStatusException(HttpStatus.NOT_FOUND, "Template does not exist!");
  private final TemplateRepository templateRepository;
  private ResponseComponentManager responseTemplateManager;

  public ResponseComponentController(TemplateRepository templateRepository) {
    this.templateRepository = templateRepository;
    responseTemplateManager = new ResponseComponentManager();
  }


  /**
   * Add a new component to the repository.
   *
   * @return the created component
   */
  @PostMapping("api/templates")
  public ResponseComponent addTemplate(@RequestBody ResponseComponent responseComponent) {
    return responseTemplateManager.addTemplate(templateRepository, responseComponent);
  }


  /**
   * Add a set of default templates to the repository.
   *
   * @return the list of default templates
   */
  @PostMapping("api/templates/default")
  public List<ResponseComponent> addDefaultTemplates() {
    return responseTemplateManager.addDefaultTemplates(templateRepository);
  }

  /**
   * Pagination for templates (sort_by, page, count).
   *
   * @param count  Counter for the templates.
   * @param page   Pagenumber.
   * @param sortBy Sorts by name ascending or descending, priority ascending and descending.
   * @return Returns a list of sorted templates.
   */
  @GetMapping("api/templates")
  public List<ResponseComponent> getAllTemplates(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy
  ) {
    ArrayList<ResponseComponent> result = new ArrayList<>();
    templateRepository.findAll().forEach(result::add);

    Stream<ResponseComponent> filteredResult = result.stream().sorted(ResponseComponentFilter
        .createTemplateComparator(sortBy));

    if (count.isPresent()) {
      if (page.isPresent()) {
        // skip pages
        filteredResult = filteredResult
            .skip(page.get() * count.get());
      }
      // only take count amount of entries
      filteredResult = filteredResult.limit(count.get());
    }
    return filteredResult.collect(Collectors.toList());
  }


  /**
   * Find the component with the given ID.
   *
   * @param id the ID to look for
   * @return the response component with the given ID
   */
  @GetMapping("api/templates/{id}")
  public ResponseComponent getTemplateByID(@PathVariable int id) {
    return responseTemplateManager.getTemplateByID(templateRepository, id);
  }

  /**
   * Delete the component with the given ID.
   *
   * @param id the ID of the component to delete
   */
  @DeleteMapping("api/templates/{id}")
  public void deleteTemplate(@PathVariable int id) {
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
   * Updates templates by ID that are already in the database.
   *
   * @param templateId        Is the component ID.
   * @param responseComponent Is the component itself.
   */
  @PutMapping("api/templates/{templateId}")
  public void updateTemplate(@PathVariable int templateId, @RequestBody ResponseComponent responseComponent) {
    responseComponent.setComponentId(templateId);
    templateRepository.save(responseComponent);
  }
}
