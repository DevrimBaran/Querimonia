package de.fraunhofer.iao.querimonia.rest.manager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import de.fraunhofer.iao.querimonia.rest.manager.filter.ResponseComponentFilter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manager for creating, viewing and deleting Response Templates.
 *
 * @author Simon Weiler
 */
public class ResponseComponentManager {

  private static final QuerimoniaException NOT_FOUND_EXCEPTION
      = new QuerimoniaException(HttpStatus.NOT_FOUND,
      "Component does not exist!",
      "Missing component");

  private static final QuerimoniaException JSON_PARSE_EXCEPTION
      = new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
      "Contents of DefaultTemplates.json are not valid JSON code!",
      "Invalid JSON code");

  private static final QuerimoniaException JSON_MAPPING_EXCEPTION
      = new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
      "Could not map contents of DefaultTemplates.json to an array of ResponseComponents!",
      "Invalid JSON content");

  private static final QuerimoniaException FILE_NOT_FOUND_EXCEPTION
      = new QuerimoniaException(HttpStatus.NOT_FOUND,
      "Could not find DefaultTemplates.json!",
      "Missing JSON file");

  private static final QuerimoniaException FILE_IO_EXCEPTION
      = new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
      "Could not read DefaultTemplates.json!",
      "Broken JSON file");


  /**
   * Add a new component to the repository.
   *
   * @param templateRepository the component repository to use
   * @return the created component
   */
  public synchronized ResponseComponent addTemplate(ResponseComponentRepository templateRepository,
                                                    ResponseComponent responseComponent) {
    templateRepository.save(responseComponent);
    return responseComponent;
  }

  /**
   * Add a set of default templates to the repository. Default templates can be found in
   * resources/DefaultTemplates.json.
   *
   * @return the list of default templates
   */
  public synchronized List<ResponseComponent> addDefaultTemplates(
      ResponseComponentRepository templateRepository) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
      Resource defaultTemplatesResource = defaultResourceLoader
          .getResource("DefaultTemplates.json");

      if (!defaultTemplatesResource.exists()) {
        throw FILE_NOT_FOUND_EXCEPTION;
      }

      InputStream defaultTemplatesStream = defaultTemplatesResource.getInputStream();

      List<ResponseComponent> defaultTemplates = Arrays.asList(objectMapper.readValue(
          defaultTemplatesStream, ResponseComponent[].class));

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
   * Pagination for templates (sort_by, page, count).
   *
   * @param count  Counter for the templates.
   * @param page   Page number.
   * @param keywords  Keywords of the template texts.
   * @param sortBy Sorts by name ascending or descending, priority ascending and descending.
   * @return Returns a list of sorted templates.
   */
  public synchronized List<ResponseComponent> getAllTemplates(
      ResponseComponentRepository templateRepository,
      Optional<Integer> count,
      Optional<Integer> page,
      Optional<String[]> sortBy,
      Optional<String[]> keywords) {
    ArrayList<ResponseComponent> result = new ArrayList<>();
    templateRepository.findAll().forEach(result::add);

    Stream<ResponseComponent> filteredResult =
            result.stream()
                    .filter(responseComponent -> ResponseComponentFilter.filterByKeywords(
                        responseComponent, keywords))
                    .sorted(ResponseComponentFilter.createTemplateComparator(sortBy));

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
   * @param templateRepository the component repository to use
   * @param id                 the ID to look for
   * @return the response component with the given ID
   */
  public synchronized ResponseComponent getTemplateByID(
      ResponseComponentRepository templateRepository,
      long id) {
    return templateRepository.findById(id)
        .orElseThrow(() -> NOT_FOUND_EXCEPTION);
  }
}
