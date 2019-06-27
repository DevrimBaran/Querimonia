package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.response.template.ResponseComponent;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Manager for creating, viewing and deleting Response Templates.
 *
 * @author Simon Weiler
 */
public class ResponseTemplateManager {

  private static final ResponseStatusException NOT_FOUND_EXCEPTION
      = new ResponseStatusException(HttpStatus.NOT_FOUND, "Component does not exists!");

  private static ResponseTemplateManager instance;

  /**
   * Private constructor for the manager.
   */
  private ResponseTemplateManager() {
  }

  /**
   * Return the ResponseTemplateManager instance or create it.
   *
   * @return the current ResponseTemplateManager instance
   */
  public static ResponseTemplateManager instantiate() {
    if (instance == null) {
      instance = new ResponseTemplateManager();
    }
    return instance;
  }

  /**
   * Add a new template to the repository.
   *
   * @param templateRepository the template repository to use
   * @return the created template
   */
  public ResponseComponent addTemplate(TemplateRepository templateRepository,
                                       ResponseComponent responseComponent) {
    templateRepository.save(responseComponent);
    return responseComponent;
  }

  /**
   * Find the template with the given ID.
   *
   * @param templateRepository the template repository to use
   * @param id                 the ID to look for
   * @return the response template with the given ID
   */
  public ResponseComponent getTemplateByID(TemplateRepository templateRepository, int id) {
    return templateRepository.findById(id)
        .orElseThrow(() -> NOT_FOUND_EXCEPTION);
  }

  /**
   * Delete the template with the given ID.
   *
   * @param templateRepository the template repository to use
   * @param id                 the ID of the template to delete
   */
  public void deleteTemplateByID(TemplateRepository templateRepository, int id) {
    templateRepository.deleteById(id);
  }
}
