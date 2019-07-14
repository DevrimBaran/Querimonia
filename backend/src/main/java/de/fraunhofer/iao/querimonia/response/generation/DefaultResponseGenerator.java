package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.db.repositories.ActionRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.rules.RuledInterface;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The default response generator.
 */
public class DefaultResponseGenerator implements ResponseGenerator {

  private final ActionRepository actionRepository;
  private final ResponseComponentRepository templateRepository;

  public DefaultResponseGenerator(ResponseComponentRepository templateRepository,
                                  ActionRepository actionRepository) {
    this.templateRepository = templateRepository;
    this.actionRepository = actionRepository;
  }

  @Override
  public ResponseSuggestion generateResponse(ComplaintData complaintData) {
    List<ResponseComponent> responseComponents = new ArrayList<>();
    templateRepository.findAll().forEach(responseComponents::add);

    List<Action> actions = new ArrayList<>();
    actionRepository.findAll().forEach(actions::add);

    // filter out not matching templates
    List<RuledInterface> responseComponentsFiltered =
        filterComponents(complaintData, responseComponents);

    // filter out not matching actions
    actions = actions.stream()
        .filter(action -> action.getRootRule().isPotentiallyRespected(complaintData))
        .sorted()
        .collect(Collectors.toList());

    responseComponentsFiltered.addAll(actions);

    List<NamedEntity> allEntities = new ArrayList<>();

    String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .withLocale(Locale.GERMAN)
        .format(complaintData.getUploadTime().toLocalDate());
    String formattedTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN)
        .format(complaintData.getUploadTime().toLocalTime());
    allEntities.add(new NamedEntity("Eingangsdatum", 0, 0, false, "", formattedDate));
    allEntities.add(new NamedEntity("Eingangszeit", 0, 0, false, "", formattedTime));

    return getResponseSuggestion(complaintData, responseComponentsFiltered, allEntities);
  }

  private List<RuledInterface> filterComponents(ComplaintData complaintData,
                                                List<ResponseComponent> responseComponents) {
    List<RuledInterface> responseComponentsFiltered = new ArrayList<>();
    responseComponents.stream()
        .filter(template -> template.getRootRule().isPotentiallyRespected(complaintData))
        .sorted(Comparator.comparingInt(ResponseComponent::getPriority))
        .forEach(responseComponentsFiltered::add);

    Collections.reverse(responseComponentsFiltered);
    return responseComponentsFiltered;
  }

  private ResponseSuggestion getResponseSuggestion(ComplaintData complaintData,
                                                   List<RuledInterface> filteredComponents,
                                                   List<NamedEntity> allEntities) {
    List<CompletedResponseComponent> generatedResponse = new ArrayList<>();
    List<Action> validActions = new ArrayList<>();

    outer:
    while (true) {
      for (int i = 0; i < filteredComponents.size(); i++) {
        RuledInterface currentRuledObject = filteredComponents.get(i);
        // find first respected rule, use the component and remove it from the list
        if (currentRuledObject.getRootRule().isRespected(complaintData, generatedResponse)) {
          filteredComponents.remove(i);
          if (currentRuledObject instanceof ResponseComponent) {
            ResponseComponent currentComponent = (ResponseComponent) currentRuledObject;

            // filter the entities so only the required ones get added
            var matchingEntities = allEntities.stream()
                .filter(namedEntity ->
                    currentComponent.getRequiredEntities().contains(namedEntity.getLabel()))
                .collect(Collectors.toList());

            generatedResponse
                .add(new CompletedResponseComponent(currentComponent, matchingEntities));
          } else if (currentRuledObject instanceof Action) {
            validActions.add((Action) currentRuledObject);
          }
          continue outer;
        }
      }
      // no matching rule left:
      break;
    }

    return new ResponseSuggestion(generatedResponse, validActions);
  }


}
