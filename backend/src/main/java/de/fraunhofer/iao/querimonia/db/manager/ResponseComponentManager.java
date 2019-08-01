package de.fraunhofer.iao.querimonia.db.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.db.manager.filter.ResponseComponentFilter;
import de.fraunhofer.iao.querimonia.db.repository.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repository.CompletedComponentRepository;
import de.fraunhofer.iao.querimonia.db.repository.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.db.repository.ResponseSuggestionRepository;
import de.fraunhofer.iao.querimonia.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponentBuilder;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
@Service
public class ResponseComponentManager {

  private final ResponseComponentRepository componentRepository;
  private final ComplaintRepository complaintRepository;
  private final ResponseSuggestionRepository responseSuggestionRepository;
  private final CompletedComponentRepository completedComponentRepository;

  private static final String JSON_ERROR_TEXT =
      "Die Default-Antwortbausteine konnten nicht geladen werden.";

  @Autowired
  public ResponseComponentManager(
      @Qualifier("responseComponentRepository") ResponseComponentRepository componentRepository,
      ComplaintRepository complaintRepository,
      ResponseSuggestionRepository responseSuggestionRepository,
      CompletedComponentRepository completedComponentRepository) {
    this.componentRepository = componentRepository;
    this.complaintRepository = complaintRepository;
    this.responseSuggestionRepository = responseSuggestionRepository;
    this.completedComponentRepository = completedComponentRepository;
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
   * Deletes a component from the database. Removes all references in complaints.
   *
   * @param componentId the id of the component, that should be deleted.
   */
  public synchronized void deleteComponent(long componentId) {
    if (componentRepository.existsById(componentId)) {
      // remove references in complaints
      for (Complaint complaint : complaintRepository.findAll()) {
        var builder = new ComplaintBuilder(complaint);
        var suggestion = builder.getResponseSuggestion();
        // remove references
        var completedComponents = suggestion.getResponseComponents();

        // delete response if component is part of it
        boolean componentIsUsed = completedComponents
            .stream()
            .map(CompletedResponseComponent::getId)
            .anyMatch(id -> id == componentId);
        if (componentIsUsed) {
          // set to empty response
          builder.setResponseSuggestion(new ResponseSuggestion());
          complaintRepository.save(builder.createComplaint());
          responseSuggestionRepository.delete(suggestion);
          completedComponentRepository.deleteAll(completedComponents);
        }
      }
      // now check component table
      for (CompletedResponseComponent completedResponseComponent
          : completedComponentRepository.findAll()) {

        // delete response if component is part of it
        if (completedResponseComponent.getComponent().getId() == componentId) {
          completedComponentRepository.delete(completedResponseComponent);
        }
      }

      componentRepository.deleteById(componentId);
    } else {
      throw new NotFoundException(componentId);
    }
  }

  /**
   * Deletes all components.
   */
  public synchronized void deleteAllComponents() {
    // remove references in complaints
    for (Complaint complaint : complaintRepository.findAll()) {
      // delete references
      complaint = new ComplaintBuilder(complaint)
          .setResponseSuggestion(new ResponseSuggestion(new ArrayList<>(), new ArrayList<>()))
          .createComplaint();
      complaintRepository.save(complaint);
    }
    responseSuggestionRepository.deleteAll();
    completedComponentRepository.deleteAll();
    componentRepository.deleteAll();

    for (Complaint complaint : complaintRepository.findAll()) {
      // add empty responses
      complaint = new ComplaintBuilder(complaint)
          .setResponseSuggestion(new ResponseSuggestion(new ArrayList<>(), new ArrayList<>()))
          .createComplaint();
      complaintRepository.save(complaint);
    }
  }

  public synchronized ResponseComponent updateComponent(long componentId,
                                                        ResponseComponent responseComponent) {
    responseComponent = responseComponent.withId(componentId);
    componentRepository.save(responseComponent);
    return responseComponent;
  }
}
