package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.complaint.ComplaintUtility;
import de.fraunhofer.iao.querimonia.db.repositories.ActionRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import de.fraunhofer.iao.querimonia.response.component.ResponseSlice;
import de.fraunhofer.iao.querimonia.response.rules.RuledInterface;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The default response generator.
 */
public class DefaultResponseGenerator implements ResponseGenerator {

  private final TemplateRepository templateRepository;
  private final ActionRepository actionRepository;
  private final ResponseComponentRepository templateRepository;

  public DefaultResponseGenerator(TemplateRepository templateRepository, ActionRepository actionRepository) {
    this.templateRepository = templateRepository;
    this.actionRepository = actionRepository;
  }

  /**
   * Fills a response component with the information given in the entities.
   *
   * @param currentSlices the current text slices
   * @param entities      the named entities of the complaint.
   *
   * @return a filled out response component.
   */
  private static SingleCompletedComponent fillResponseComponent(List<ResponseSlice> currentSlices,
                                                                Map<NamedEntity, String> entities) {

    StringBuilder resultText = new StringBuilder();
    List<NamedEntity> entityList = new ArrayList<>();
    // the current position in the text
    int resultPosition = 0;

    for (ResponseSlice slice : currentSlices) {
      String textToAppend;

      if (slice.isPlaceholder()) {
        String placeholderName = slice.getContent();
        // find entity with that label
        var entityWithPlaceholder = entities.keySet().stream()
            .filter(namedEntity -> namedEntity.getLabel().equals(placeholderName))
            .findAny();

        textToAppend = entityWithPlaceholder.map(entities::get).orElse("");
        // create entity with label
        entityList.add(new NamedEntity(placeholderName, resultPosition,
            resultPosition + textToAppend.length(),
            entityWithPlaceholder.map(NamedEntity::getExtractor).orElse(null)));
      } else {
        // raw text that does not need to be replaced
        textToAppend = slice.getContent();
      }

      resultPosition += textToAppend.length();
      resultText.append(textToAppend);
    }
    return new SingleCompletedComponent(resultText.toString(), entityList);
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


    Map<NamedEntity, String> entityValueMap =
        ComplaintUtility.getEntityValueMap(complaintData.getText(), complaintData.getEntities());

    String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .withLocale(Locale.GERMAN)
        .format(complaintData.getUploadTime().toLocalDate());
    String formattedTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN)
        .format(complaintData.getUploadTime().toLocalTime());
    entityValueMap.put(new NamedEntity("UploadDatum", 0, 0, false, null), formattedDate);
    entityValueMap.put(new NamedEntity("UploadZeit", 0, 0, false, null), formattedTime);

    return getResponseSuggestion
        (complaintData, responseComponentsFiltered, entityValueMap);
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
                                                   Map<NamedEntity, String> entityValueMap) {
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
            generatedResponse.add(
                new CompletedResponseComponent(currentComponent.getResponseSlices().stream()
                    .map(responseSlices -> fillResponseComponent(
                        responseSlices, entityValueMap))
                    .collect(Collectors.toList()), currentComponent));
          } else if (currentRuledObject instanceof Action){
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
