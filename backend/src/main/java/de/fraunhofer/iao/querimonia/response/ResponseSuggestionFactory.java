package de.fraunhofer.iao.querimonia.response;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A factory class for creating response suggestions from templates and data.
 * Currently only supports dynamic dates.
 *
 * @author Simon Weiler
 */
public class ResponseSuggestionFactory {

  /**
   * Creates a new response suggestion with a custom date.
   *
   * @param complaint the complaint to respond to
   * @param template  the template for the response
   * @return the filled out response suggestion
   */
  public static ResponseSuggestion createResponseSuggestionWithDate(Complaint complaint,
                                                                    ResponseComponent template) {
    String dateString = extractDateAsString(complaint.getReceiveDate());
    String templateText = template.getTemplateText();
    ArrayList<CompletedResponseComponent> responseComponents = new ArrayList<>();
    responseComponents.add(fillResponseComponent(template, complaint.getEntityValueMap()));
    return new ResponseSuggestion(responseComponents);
  }

  /**
   * Fills out all the placeholders in a response component.
   *
   * @param component the response template component.
   * @param entities a hashmap that includes all values to the placeholder named of the template.
   * @return a completed response component with all filled out placeholders.
   */
  public static CompletedResponseComponent fillResponseComponent(ResponseComponent component,
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

      } else {
        break;
      }
    }
    return new CompletedResponseComponent(resultText.toString(), component, entityList);
  }

  /**
   * Extracts the receive date from a complaint and returns it as a readable string.
   *
   * @param complaintDate the complaint date to extract from
   * @return the date as a readable string
   */
  private static String extractDateAsString(LocalDate complaintDate) {

    int complaintDay = complaintDate.getDayOfMonth();
    String complaintMonth;
    int complaintYear = complaintDate.getYear();

    switch (complaintDate.getMonthValue()) {
      case 1:
        complaintMonth = "Januar";
        break;
      case 2:
        complaintMonth = "Februar";
        break;
      case 3:
        complaintMonth = "März";
        break;
      case 4:
        complaintMonth = "April";
        break;
      case 5:
        complaintMonth = "Mai";
        break;
      case 6:
        complaintMonth = "Juni";
        break;
      case 7:
        complaintMonth = "Juli";
        break;
      case 8:
        complaintMonth = "August";
        break;
      case 9:
        complaintMonth = "September";
        break;
      case 10:
        complaintMonth = "Oktober";
        break;
      case 11:
        complaintMonth = "November";
        break;
      case 12:
        complaintMonth = "Dezember";
        break;
      default:
        throw new IllegalStateException();
    }

    return String.format("%d. %s %d", complaintDay, complaintMonth, complaintYear);
  }
}
