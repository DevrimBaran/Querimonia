package de.fraunhofer.iao.querimonia.nlp.response;

import de.fraunhofer.iao.querimonia.db.ComplaintUtility;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * The default response generator.
 */
public class DefaultResponseGenerator implements ResponseGenerator {

  private static final String BEGIN_RESPONSE_PART = "Begrüßung";
  private TemplateRepository templateRepository;

  public DefaultResponseGenerator(TemplateRepository templateRepository) {
    this.templateRepository = templateRepository;
  }

  /**
   * Fills a response component with the information given in the entities.
   *
   * @param component the response component that gets filled out.
   * @param entities  the named entities of the complaint.
   * @return a filled out response component.
   */
  private static CompletedResponseComponent fillResponseComponent(ResponseComponent component,
                                                                  Map<String, String> entities) {
    List<ResponseComponent.ResponseSlice> slices = component.getSlices();
    StringBuilder resultText = new StringBuilder();
    List<NamedEntity> entityList = new ArrayList<>();
    // the current position in the text
    int resultPosition = 0;

    for (ResponseComponent.ResponseSlice slice : slices) {
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
    return new CompletedResponseComponent(resultText.toString(), component, entityList);
  }

  @Override
  public ResponseSuggestion generateResponse(String text,
                                             Map<String, Double> subjectMap,
                                             Map<String, Double> sentimentMap,
                                             List<NamedEntity> entities,
                                             LocalDateTime uploadTime) {
    List<ResponseComponent> responseComponents = new ArrayList<>();
    templateRepository.findAll().forEach(responseComponents::add);

    ArrayList<CompletedResponseComponent> result = new ArrayList<>();
    Map<String, String> entityValueMap = ComplaintUtility.getEntityValueMap(text, entities);

    String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .format(uploadTime.toLocalDate());
    String formattedTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
        .format(uploadTime.toLocalTime());
    entityValueMap.put("Upload_Datum", formattedDate);
    entityValueMap.put("Upload_Zeit", formattedTime);

    Optional<String> optionalSubject = ComplaintUtility.getEntryWithHighestProbability(subjectMap);

    generateComponentOrder(responseComponents, entityValueMap, optionalSubject, BEGIN_RESPONSE_PART)
        .stream()
        .map(component -> fillResponseComponent(component, entityValueMap))
        .forEach(result::add);

    return new ResponseSuggestion(result);
  }

  /**
   * Checks if the subject of the response component matches the subject of the complaint.
   */
  private boolean subjectMatches(Optional<String> optionalSubject,
                                 ResponseComponent responseComponent) {
    return optionalSubject.map(subject -> subject
        .equalsIgnoreCase(
            // if response component has no subject the filter should return true
            responseComponent.getSubject().orElse(subject)))
        .orElse(true);
  }

  private List<ResponseComponent> generateComponentOrder(
      List<ResponseComponent> responseComponents,
      Map<String, String> entities,
      Optional<String> optionalSubject,
      String nextResponsePart) {

    Optional<ResponseComponent> currentComponent = responseComponents.stream()
        .filter(responseComponent ->
                    responseComponent.getResponsePart().equalsIgnoreCase(nextResponsePart))
        .filter(responseComponent -> subjectMatches(optionalSubject, responseComponent))
        .filter(responseComponent -> entities.keySet()
            .containsAll(responseComponent.getRequiredEntities()))
        .min(Comparator.comparingInt(rc -> -rc.getRequiredEntities().size()));
    if (currentComponent.isPresent()) {
      List<ResponseComponent> result = new ArrayList<>();
      List<String> successorParts = currentComponent.get().getSuccessorParts();
      if (!successorParts.isEmpty()) {
        // TODO optimize successors
        int next = new Random().nextInt(successorParts.size());
        result = generateComponentOrder(responseComponents, entities, optionalSubject,
                                        successorParts.get(next));
      }
      result.add(0, currentComponent.get());
      return result;
    } else {
      return new ArrayList<>();
    }
  }
}
