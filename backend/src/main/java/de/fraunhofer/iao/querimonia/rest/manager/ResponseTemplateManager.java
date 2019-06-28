package de.fraunhofer.iao.querimonia.rest.manager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.response.template.ResponseComponent;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Manager for creating, viewing and deleting Response Templates.
 *
 * @author Simon Weiler
 */
public class ResponseTemplateManager {

  private static final ResponseStatusException NOT_FOUND_EXCEPTION
          = new ResponseStatusException(HttpStatus.NOT_FOUND, "Component does not exist!");

  private static final ResponseStatusException JSON_PARSE_EXCEPTION
          = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Contents of DefaultTemplates.json are not valid JSON code!");

  private static final ResponseStatusException JSON_MAPPING_EXCEPTION
          = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not map contents of DefaultTemplates.json to an array of ResponseComponents!");

  private static final ResponseStatusException FILE_IO_EXCEPTION
          = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read DefaultTemplates.json!");

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
   * Add a set of default templates to the repository.
   * Default templates can be found in backend/doc/DefaultTemplates.json.
   *
   * @return the list of default templates
   */
  public List<ResponseComponent> addDefaultTemplates(TemplateRepository templateRepository) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {

      List<ResponseComponent> defaultTemplates = Arrays.asList(objectMapper.readValue(
              new File("backend/doc/DefaultTemplates.json"), ResponseComponent[].class));

      defaultTemplates.forEach(templateRepository::save);
      return defaultTemplates;

    } catch (JsonParseException e) {
      throw JSON_PARSE_EXCEPTION;
    } catch (JsonMappingException e) {
      throw JSON_MAPPING_EXCEPTION;
    } catch (IOException e) {
      throw FILE_IO_EXCEPTION;
    }
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
