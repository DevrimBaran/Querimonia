package de.fraunhofer.iao.querimonia.manager;

import de.fraunhofer.iao.querimonia.manager.filter.ResponseComponentFilter;
import de.fraunhofer.iao.querimonia.repository.ComplaintRepository;
import de.fraunhofer.iao.querimonia.repository.CompletedComponentRepository;
import de.fraunhofer.iao.querimonia.repository.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.repository.ResponseSuggestionRepository;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponentBuilder;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import de.fraunhofer.iao.querimonia.utility.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manager for creating, viewing and deleting Response Components.
 *
 * @author Simon Weiler
 */
@Service
public class ResponseComponentManager {

  private static final Logger logger = LoggerFactory.getLogger(ResponseComponentManager.class);

  private final ResponseComponentRepository componentRepository;
  private final ComplaintRepository complaintRepository;
  private final ResponseSuggestionRepository responseSuggestionRepository;
  private final CompletedComponentRepository completedComponentRepository;
  private final FileStorageService fileStorageService;

  /**
   * Creates a new response component manager. Constructor is called by spring.
   */
  @Autowired
  public ResponseComponentManager(
      @Qualifier("responseComponentRepository") ResponseComponentRepository componentRepository,
      ComplaintRepository complaintRepository,
      ResponseSuggestionRepository responseSuggestionRepository,
      CompletedComponentRepository completedComponentRepository,
      FileStorageService fileStorageService) {
    this.componentRepository = componentRepository;
    this.complaintRepository = complaintRepository;
    this.responseSuggestionRepository = responseSuggestionRepository;
    this.completedComponentRepository = completedComponentRepository;
    this.fileStorageService = fileStorageService;
  }

  /**
   * Add a new component to the repository.
   *
   * @param responseComponent the component to add.
   *
   * @return the created component
   */
  public synchronized ResponseComponent addComponent(ResponseComponent responseComponent) {
    componentRepository.save(responseComponent);
    return responseComponent;
  }

  /**
   * Add a set of default components to the repository. Default components can be found in
   * resources/DefaultComponents.json.
   *
   * @return the list of default components
   */
  public synchronized List<ResponseComponent> addDefaultComponents() {
    List<ResponseComponent> defaultComponents;
    defaultComponents = fileStorageService
        .getJsonObjectsFromFile(ResponseComponent[].class, "DefaultComponents.json");

    defaultComponents.forEach(componentRepository::save);
    logger.info("Added default components.");
    return defaultComponents;
  }

  /**
   * Pagination for components (sort_by, page, count).
   *
   * @param count    number of components per page.
   * @param page     number of the page.
   * @param sortBy   Sorts by name ascending or descending, priority ascending and descending.
   * @param keywords if given, complaints get filtered by these keywords.
   *
   * @return Returns a list of sorted components.
   */
  public synchronized List<ResponseComponent> getAllComponents(
      Optional<Integer> count,
      Optional<Integer> page,
      Optional<String[]> sortBy,
      Optional<String[]> actionCode,
      Optional<String[]> keywords) {
    ArrayList<ResponseComponent> result = new ArrayList<>();

    Iterable<ResponseComponent> responseComponents = componentRepository.findAll();
    for (ResponseComponent responseComponent : responseComponents) {
      ResponseComponent newResponseComponent = new ResponseComponentBuilder(responseComponent)
          .createResponseComponent();
      result.add(newResponseComponent);
    }

    Stream<ResponseComponent> filteredResult =
        result.stream()
            .filter(responseComponent -> ResponseComponentFilter.filterByKeywords(
                responseComponent, keywords))
            .filter(responseComponent -> ResponseComponentFilter.filterByActionCode(
                responseComponent, actionCode))
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
   * @param id the ID to look for
   *
   * @return the response component with the given ID
   */
  public synchronized ResponseComponent getComponentByID(long id) {
    ResponseComponent responseComponent = componentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(id));
    return new ResponseComponentBuilder(responseComponent).createResponseComponent();
  }

  /**
   * Deletes a component from the database. This is not possible, if a complaint uses the
   * component with the given id.
   *
   * @param componentId the id of the component, that should be deleted.
   *
   * @throws QuerimoniaException if a complaint uses the component in the response.
   */
  public synchronized void deleteComponent(long componentId) {
    if (componentRepository.existsById(componentId)) {
      componentRepository.deleteById(componentId);
    } else {
      throw new NotFoundException(componentId);
    }
  }

  /**
   * Deletes all components. This is not possible, if any complaint uses a component.
   */
  public synchronized void deleteAllComponents() {
    componentRepository.deleteAll();
    logger.info("Deleted all components.");
  }

  public synchronized ResponseComponent updateComponent(long componentId,
                                                        ResponseComponent responseComponent) {
    responseComponent = responseComponent.withId(componentId);
    componentRepository.save(responseComponent);
    return responseComponent;
  }
}
