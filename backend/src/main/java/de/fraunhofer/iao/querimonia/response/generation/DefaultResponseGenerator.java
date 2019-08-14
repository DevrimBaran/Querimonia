package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.repository.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.response.action.Action;

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
 * TODO: better doc
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

    List<NamedEntity> allEntities = new ArrayList<>(complaintBuilder.getEntities());

    // add upload date entities
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
        .setColor(getColorOfEntity(complaintBuilder, "Eingangsdatum"))
        .createNamedEntity());
    allEntities.add(new NamedEntityBuilder()
        .setLabel("Eingangszeit")
        .setStart(0)
        .setEnd(0)
        .setSetByUser(false)
        .setExtractor(extractor)
        .setValue(formattedTime)
        .setColor(getColorOfEntity(complaintBuilder, "Eingangszeit"))
        .createNamedEntity());

    return getResponseSuggestion(complaintBuilder, responseComponentsFiltered, allEntities);
  }

  private List<ResponseComponent> filterComponents(ComplaintBuilder complaintData,
                                                   List<ResponseComponent> responseComponents) {
    List<ResponseComponent> responseComponentsFiltered = new ArrayList<>();
    responseComponents.stream()
        .filter(template -> template.getRootRule().isPotentiallyRespected(complaintData))
        .sorted(Comparator.comparingInt(ResponseComponent::getPriority))
        .forEach(responseComponentsFiltered::add);

    Collections.reverse(responseComponentsFiltered);
    return responseComponentsFiltered;
  }

  private ResponseSuggestion getResponseSuggestion(ComplaintBuilder complaintData,
                                                   List<ResponseComponent> filteredComponents,
                                                   List<NamedEntity> allEntities) {
    List<CompletedResponseComponent> generatedResponse = new ArrayList<>();

    outer:
    while (true) {
      for (int i = 0; i < filteredComponents.size(); i++) {
        ResponseComponent currentRuledObject = filteredComponents.get(i);
        // find first respected rule, use the component and remove it from the list
        if (currentRuledObject.getRootRule().isRespected(complaintData, generatedResponse)) {
          filteredComponents.remove(i);

          // filter the entities so only the required ones get added
          var matchingEntities = allEntities.stream()
              .filter(namedEntity ->
                  currentRuledObject.getRequiredEntities().contains(namedEntity.getLabel()))
              .collect(Collectors.toList());

          generatedResponse
              .add(new CompletedResponseComponent(currentRuledObject, matchingEntities));
          continue outer;
        }
      }
      // no matching rule left:
      break;
    }

    List<Action> validActions = filteredComponents.stream()
        .map(ResponseComponent::getActions)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    return new ResponseSuggestion(generatedResponse, validActions);
  }

  private String getColorOfEntity(ComplaintBuilder complaintBuilder, String label) {
    return complaintBuilder
        .getConfiguration()
        .getExtractors()
        .stream()
        .filter(extractorDefinition -> extractorDefinition.getLabel().equals(label))
        .map(ExtractorDefinition::getColor)
        .findAny()
        // fallback color
        .orElse("#cc22cc");
  }
}
