package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.repository.ResponseComponentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The default response generator.
 */
public class DefaultResponseGenerator implements ResponseGenerator {

  private final ResponseComponentRepository templateRepository;

  public DefaultResponseGenerator(ResponseComponentRepository templateRepository) {
    this.templateRepository = templateRepository;
  }

  @Override
  public ResponseSuggestion generateResponse(ComplaintBuilder complaintBuilder) {
    List<ResponseComponent> responseComponents = new ArrayList<>();
    templateRepository.findAll()
        .forEach(responseComponent -> responseComponents.add(responseComponent.copy()));

    // filter out not matching templates
    List<ResponseComponent> responseComponentsFiltered =
        filterComponents(complaintBuilder, responseComponents);

    // find all entities
    List<NamedEntity> allEntities = complaintBuilder.getEntities();

    return createResponseSuggestion(complaintBuilder, responseComponentsFiltered, allEntities);
  }

  /**
   * Returns the components that could be potentially used in the response.
   */
  private List<ResponseComponent> filterComponents(ComplaintBuilder complaintData,
                                                   List<ResponseComponent> responseComponents) {
    List<ResponseComponent> responseComponentsFiltered = new ArrayList<>();
    responseComponents.stream()
        // check that the root rule may be respected
        .filter(component -> component.getRootRule().isPotentiallyRespected(complaintData))
        // sort by priority
        .sorted(Comparator.comparingInt(ResponseComponent::getPriority))
        .forEach(responseComponentsFiltered::add);

    // reverse for highest priority is first in the list
    Collections.reverse(responseComponentsFiltered);
    return responseComponentsFiltered;
  }

  private ResponseSuggestion createResponseSuggestion(ComplaintBuilder complaintData,
                                                      List<ResponseComponent> filteredComponents,
                                                      List<NamedEntity> allEntities) {
    var generatedResponse = new ArrayList<CompletedResponseComponent>();

    outer:
    while (true) {
      for (int i = 0; i < filteredComponents.size(); i++) {
        ResponseComponent currentComponent = filteredComponents.get(i);
        // find first respected rule, use the component and remove it from the list
        if (currentComponent.getRootRule().isRespected(complaintData, generatedResponse)) {
          filteredComponents.remove(i);

          // filter the entities so only the required ones get added
          var matchingEntities = allEntities.stream()
              .filter(namedEntity ->
                  currentComponent.getRequiredEntities().contains(namedEntity.getLabel()))
              .collect(Collectors.toList());

          generatedResponse
              .add(new CompletedResponseComponent(currentComponent.toPersistableComponent(),
                  matchingEntities, false));
          // restart on the component with the highest priority again
          continue outer;
        }
      }
      // no matching rule left:
      break;
    }

    return new ResponseSuggestion(generatedResponse, "");
  }

}
