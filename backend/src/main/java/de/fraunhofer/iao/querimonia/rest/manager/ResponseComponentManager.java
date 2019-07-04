package de.fraunhofer.iao.querimonia.rest.manager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Manager for creating, viewing and deleting Response Templates.
 *
 * @author Simon Weiler
 */
public class ResponseComponentManager {

  private static final QuerimoniaException NOT_FOUND_EXCEPTION
      = new QuerimoniaException(HttpStatus.NOT_FOUND, "Antwort-Komponente existiert nicht!",
      "Ungültige ID");

  private static final QuerimoniaException JSON_PARSE_EXCEPTION
      = new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
      "Die Beispiel-Bausteine konnten nicht geladen werden!", "Server-Error");

  private static final QuerimoniaException JSON_MAPPING_EXCEPTION
      = JSON_PARSE_EXCEPTION;

  private static final QuerimoniaException FILE_IO_EXCEPTION
      = JSON_PARSE_EXCEPTION;


  /**
   * Add a new component to the repository.
   *
   * @param templateRepository the component repository to use
   * @return the created component
   */
  public synchronized ResponseComponent addTemplate(TemplateRepository templateRepository,
                                                    ResponseComponent responseComponent) {
    templateRepository.save(responseComponent);
    return responseComponent;
  }

  /**
   * Add a set of default templates to the repository. Default templates can be found in
   * backend/doc/DefaultTemplates.json.
   *
   * @return the list of default templates
   */
  public synchronized List<ResponseComponent> addDefaultTemplates(
      TemplateRepository templateRepository) {
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
   * Find the component with the given ID.
   *
   * @param templateRepository the component repository to use
   * @param id                 the ID to look for
   * @return the response component with the given ID
   */
  public synchronized ResponseComponent getTemplateByID(TemplateRepository templateRepository,
                                                        int id) {
    return templateRepository.findById(id)
        .orElseThrow(() -> NOT_FOUND_EXCEPTION);
  }

  /**
   * Delete the component with the given ID.
   *
   * @param templateRepository the component repository to use
   * @param id                 the ID of the component to delete
   */
  public synchronized void deleteTemplateByID(TemplateRepository templateRepository, int id) {
    templateRepository.deleteById(id);
  }
}
