package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.response.template.ResponseComponent;
import de.fraunhofer.iao.querimonia.rest.manager.ResponseTemplateManager;
import de.fraunhofer.iao.querimonia.rest.manager.TemplateFilter;
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


  /**
   * Add a set of default templates to the repository.
   *
   * @return the list of default templates
   */
  @PostMapping("api/templates/default")
  public List<ResponseComponent> addDefaultTemplates() {
    initialize();
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

    Stream<ResponseComponent> filteredResult = result.stream().sorted(TemplateFilter
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

  /**
   * Updates templates by ID that are already in the database.
   *
   * @param templateId        Is the template ID.
   * @param responseComponent Is the template itself.
   */
  @PutMapping("api/templates/{templateId}")
  public void updateTemplate(@PathVariable int templateId, @RequestBody ResponseComponent responseComponent) {
    initialize();
    responseComponent.setComponentId(templateId);
    templateRepository.save(responseComponent);
  }
}
