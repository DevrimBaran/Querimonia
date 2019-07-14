package de.fraunhofer.iao.querimonia.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.exception.NotFoundException;
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
 * Manager for creating, viewing and deleting Response Components.
 *
 * @author Simon Weiler
 */
public class ResponseComponentManager {

  private static final String JSON_ERROR_TEXT =
      "Die Default-Antwortbausteine konnten nicht geladen werden.";


  /**
   * Add a new component to the repository.
   *
   * @param componentRepository the component repository to use
   *
   * @return the created component
   */
  public synchronized ResponseComponent addComponent(
      ResponseComponentRepository componentRepository,
      ResponseComponent responseComponent) {
    componentRepository.save(responseComponent);
    return responseComponent;
  }

  /**
   * Add a set of default components to the repository. Default components can be found in
   * resources/DefaultComponents.json.
   *
   * @return the list of default components
   */
  public synchronized List<ResponseComponent> addDefaultComponents(
      ResponseComponentRepository componentRepository) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
      Resource defaultComponentsResource = defaultResourceLoader
          .getResource("DefaultComponents.json");

      if (!defaultComponentsResource.exists()) {
        throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
        JSON_ERROR_TEXT, "Fehlende Datei");
      }

      InputStream defaultComponentsStream = defaultComponentsResource.getInputStream();

      List<ResponseComponent> defaultComponents = Arrays.asList(objectMapper.readValue(
          defaultComponentsStream, ResponseComponent[].class));

      defaultComponents.forEach(componentRepository::save);
      return defaultComponents;

    } catch (IOException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
          JSON_ERROR_TEXT, e, "Ungültige JSON Datei");
    }
  }

  /**
   * Pagination for components (sort_by, page, count).
   *
   * @param count    number of components per page.
   * @param page     number of the page.
   * @param keywords if given, complaints get filtered by these keywords.
   * @param sortBy   Sorts by name ascending or descending, priority ascending and descending.
   *
   * @return Returns a list of sorted components.
   */
  public synchronized List<ResponseComponent> getAllComponents(
      ResponseComponentRepository componentRepository,
      Optional<Integer> count,
      Optional<Integer> page,
      Optional<String[]> sortBy,
      Optional<String[]> keywords) {
    ArrayList<ResponseComponent> result = new ArrayList<>();
    componentRepository.findAll().forEach(result::add);

    Stream<ResponseComponent> filteredResult =
        result.stream()
            .filter(responseComponent -> ResponseComponentFilter.filterByKeywords(
                responseComponent, keywords))
            .sorted(ResponseComponentFilter.createComponentComparator(sortBy));

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
   * @param componentRepository the component repository to use
   * @param id                  the ID to look for
   *
   * @return the response component with the given ID
   */
  public synchronized ResponseComponent getComponentByID(
      ResponseComponentRepository componentRepository,
      long id) {
    return componentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(id));
  }
}
