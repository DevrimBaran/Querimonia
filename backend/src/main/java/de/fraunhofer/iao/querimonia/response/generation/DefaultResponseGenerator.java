package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.complaint.ComplaintUtility;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import de.fraunhofer.iao.querimonia.response.component.ResponseSlice;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The default response generator.
 */
public class DefaultResponseGenerator implements ResponseGenerator {

  private final TemplateRepository templateRepository;

  public DefaultResponseGenerator(TemplateRepository templateRepository) {
    this.templateRepository = templateRepository;
  }

  /**
   * Fills a response component with the information given in the entities.
   *
   * @param currentSlices the current text slices
   * @param entities      the named entities of the complaint.
   * @return a filled out response component.
   */
  private static SingleCompletedComponent fillResponseComponent(List<ResponseSlice> currentSlices,
                                                                Map<String, String> entities) {

    StringBuilder resultText = new StringBuilder();
    List<NamedEntity> entityList = new ArrayList<>();
    // the current position in the text
    int resultPosition = 0;

    for (ResponseSlice slice : currentSlices) {
      String textToAppend;

      if (slice.isPlaceholder()) {
        String placeholderName = slice.getContent();
        textToAppend = entities.get(placeholderName);
        if (textToAppend == null) {
          throw new IllegalArgumentException("Entity " + placeholderName + " not present");
        }
        // create entity with label
        entityList.add(new NamedEntity(placeholderName, resultPosition,
                                       resultPosition + textToAppend.length()));
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

    // filter out not matching templates
    List<ResponseComponent> responseComponentsFiltered =
        filterComponents(complaintData, responseComponents);

    Map<String, String> entityValueMap =
        ComplaintUtility.getEntityValueMap(complaintData.getText(), complaintData.getEntities());

    String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .format(complaintData.getUploadTime().toLocalDate());
    String formattedTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
        .format(complaintData.getUploadTime().toLocalTime());
    entityValueMap.put("UploadDatum", formattedDate);
    entityValueMap.put("UploadZeit", formattedTime);

    return getResponseSuggestion(complaintData, responseComponentsFiltered, entityValueMap);
  }

  private List<ResponseComponent> filterComponents(ComplaintData complaintData,
                                                   List<ResponseComponent> responseComponents) {
    List<ResponseComponent> responseComponentsFiltered = new ArrayList<>();
    responseComponents.stream()
        .filter(template -> template.getRootRule().isPotentiallyRespected(complaintData))
        .forEach(responseComponentsFiltered::add);

    // sort by priority
    responseComponentsFiltered.sort(Comparator.comparingInt(ResponseComponent::getPriority));
    Collections.reverse(responseComponentsFiltered);
    return responseComponentsFiltered;
  }

  private ResponseSuggestion getResponseSuggestion(ComplaintData complaintData,
                                                   List<ResponseComponent> filteredComponents,
                                                   Map<String, String> entityValueMap) {
    List<CompletedResponseComponent> generatedResponse = new ArrayList<>();
    outer:
    while (true) {
      for (int i = 0; i < filteredComponents.size(); i++) {
        ResponseComponent currentComponent = filteredComponents.get(i);
        // find first respected rule, use the component and remove it from the list
        if (currentComponent.getRootRule().isRespected(complaintData, generatedResponse)) {
          filteredComponents.remove(i);
          generatedResponse.add(
              new CompletedResponseComponent(currentComponent.getTemplateSlices().stream()
                                                 .map(responseSlices -> fillResponseComponent(
                                                     responseSlices,
                                                     entityValueMap))
                                                 .collect(Collectors.toList()), currentComponent));
          continue outer;
        }
      }
      // no matching rule left:
      break;
    }

    return new ResponseSuggestion(generatedResponse, new ArrayList<>());
  }


}