package de.fraunhofer.iao.querimonia.nlp.response;

import de.fraunhofer.iao.querimonia.db.ComplaintUtility;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The default response generator.
 */
public class DefaultResponseGenerator implements ResponseGenerator {

  private static final String BEGIN_RESPONSE_PART = "Begrüßung";
  private TemplateRepository templateRepository;

  public DefaultResponseGenerator(TemplateRepository templateRepository) {
    this.templateRepository = templateRepository;
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
    entityValueMap.put("upload_date", formattedDate);
    entityValueMap.put("upload_time", formattedTime);

    Optional<String> optionalSubject = ComplaintUtility.getEntryWithHighestProbability(subjectMap);

    generateComponentOrder(responseComponents, optionalSubject, BEGIN_RESPONSE_PART)
        .stream()
        .map(component -> fillResponseComponent(component, entityValueMap))
        .forEach(result::add);

    return new ResponseSuggestion(result);
  }

  /**
   * Checks if the subject of the response component matches the subject of the
   * complaint.
   */
  private boolean subjectMatches(Optional<String> optionalSubject,
                                 ResponseComponent responseComponent) {
    return optionalSubject.map(subject -> subject
        .equalsIgnoreCase(
            // if response component has no subject the filter should return true
            responseComponent.getSubject().orElse(subject)))
        .orElse(true);
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
    String templateText = component.getTemplateText();
    StringBuilder resultText = new StringBuilder();
    List<NamedEntity> entityList = new ArrayList<>();
    // the current position in the text
    int templatePosition = 0;
    int resultPosition = 0;

    // split on every placeholder
    String[] parts = templateText.split("\\$\\{\\w*}");

    for (String currentPart : parts) {
      templatePosition += currentPart.length();
      resultPosition += currentPart.length();
      resultText.append(currentPart);

      String remainingText = templateText.substring(templatePosition);
      if (remainingText.length() >= 2) {
        // find closing bracket
        int endPosition = remainingText.indexOf('}');
        // check if closing bracket is there
        if (endPosition == -1) {
          throw new IllegalArgumentException("Illegal template format");
        }
        // find entity for placeholder
        String entityLabel = remainingText.substring(2, endPosition);
        String entityValue = entities.get(entityLabel);
        if (entityValue == null) {
          throw new IllegalArgumentException("Missing entity " + entityLabel);
        }
        resultText.append(entityValue);

        templatePosition = templatePosition + 3 + entityLabel.length();
        int endOfEntity = resultPosition + entityValue.length();
        entityList.add(new NamedEntity(entityLabel, resultPosition, endOfEntity));
        resultPosition = endOfEntity;

      } else {
        break;
      }
    }
    return new CompletedResponseComponent(resultText.toString(), component, entityList);
  }

  private List<ResponseComponent> generateComponentOrder(
      List<ResponseComponent> responseComponents,
      Optional<String> optionalSubject,
      String nextResponsePart) {

    Optional<ResponseComponent> currentComponent = responseComponents.stream()
        .filter(responseComponent -> subjectMatches(optionalSubject, responseComponent))
        .filter(responseComponent ->
            responseComponent.getResponsePart().equalsIgnoreCase(nextResponsePart))
        .findAny();
    if (currentComponent.isPresent()) {
      List<ResponseComponent> result = new ArrayList<>();
      if (!currentComponent.get().getSuccessorParts().isEmpty()) {
        // TODO optimize successors
        result = generateComponentOrder(responseComponents, optionalSubject,
            currentComponent.get().getSuccessorParts().get(0));
      }
      result.add(0, currentComponent.get());
      return result;
    } else {
      return new ArrayList<>();
    }
  }
}
