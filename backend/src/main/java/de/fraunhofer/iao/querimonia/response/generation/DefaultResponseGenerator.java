package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.db.repository.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
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

  private final ResponseComponentRepository templateRepository;

  public DefaultResponseGenerator(ResponseComponentRepository templateRepository) {
    this.templateRepository = templateRepository;
  }

  @Override
  public ResponseSuggestion generateResponse(ComplaintBuilder complaintBuilder) {
    List<ResponseComponent> responseComponents = new ArrayList<>();
    templateRepository.findAll().forEach(responseComponents::add);

    List<Action> actions = new ArrayList<>();
    responseComponents.stream()
        .map(ResponseComponent::getActions)
        .forEach(actions::addAll);

    // filter out not matching templates
    List<RuledInterface> responseComponentsFiltered =
        filterComponents(complaintBuilder, responseComponents);

    // filter out not matching actions
    actions = actions.stream()
        .filter(action -> action.getRootRule().isPotentiallyRespected(complaintBuilder))
        .sorted()
        .collect(Collectors.toList());

    responseComponentsFiltered.addAll(actions);

    List<NamedEntity> allEntities = new ArrayList<>();

    String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .withLocale(Locale.GERMAN)
        .format(complaintBuilder.getReceiveDate());
    String formattedTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN)
        .format(complaintBuilder.getReceiveTime());
    // get first extractors that is used for the entities that are not in the text
    String extractor = complaintBuilder
        .getConfiguration()
        .getExtractors()
        .stream()
        .findFirst()
        .map(ExtractorDefinition::getName)
        .orElse("");

    allEntities.add(new NamedEntityBuilder()
        .setLabel("Eingangsdatum")
        .setStart(0)
        .setEnd(0)
        .setSetByUser(false)
        .setExtractor(extractor)
        .setValue(formattedDate)
        .createNamedEntity());
    allEntities.add(new NamedEntityBuilder()
        .setLabel("Eingangszeit")
        .setStart(0)
        .setEnd(0)
        .setSetByUser(false)
        .setExtractor(extractor)
        .setValue(formattedTime)
        .createNamedEntity());

    return getResponseSuggestion(complaintBuilder, responseComponentsFiltered, allEntities);
  }

  private List<RuledInterface> filterComponents(ComplaintBuilder complaintData,
                                                List<ResponseComponent> responseComponents) {
    List<RuledInterface> responseComponentsFiltered = new ArrayList<>();
    responseComponents.stream()
        .filter(template -> template.getRootRule().isPotentiallyRespected(complaintData))
        .sorted(Comparator.comparingInt(ResponseComponent::getPriority))
        .forEach(responseComponentsFiltered::add);

    Collections.reverse(responseComponentsFiltered);
    return responseComponentsFiltered;
  }

  private ResponseSuggestion getResponseSuggestion(ComplaintBuilder complaintData,
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
