package de.fraunhofer.iao.querimonia.nlp.response;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Manager for creating, viewing and deleting Response Templates.
 *
 * @author Simon Weiler
 */
public class ResponseTemplateManager {

  private static final ResponseStatusException NOT_FOUND_EXCEPTION
      = new ResponseStatusException(HttpStatus.NOT_FOUND, "Component does not exists!");

  private static final String FAHRT_NICHT_ERFOLGT_DEFAULT_TEMPLATE_TEXT =
      "Guten Tag,\n"
          + "vielen Dank für Ihre Beschwerde, die am ${upload_date} bei uns "
          + "im System eingegangen ist.\n"
          + "Darin haben sie sich über eine nicht erfolgte Fahrt beschwert.\n"
          + "Leider konnte die genannte Fahrt aufgrund einer Störung nicht durchgeführt werden.\n"
          + "Dass Sie von dieser Störung betroffen waren, bedauern wir und entschuldigen uns für "
          + "die "
          + "Unannehmlichkeiten, die Ihnen hieraus entstanden sind.\n"
          + "Für die Zukunft wünschen wir Ihnen reibungslose Fahrten mit unseren Verkehrsmitteln.";

  private static final String FAHRER_UNFREUNDLICH_DEFAULT_TEMPLATE_TEXT =
      "Guten Tag,\n"
          + "vielen Dank für Ihre Beschwerde, die am ${upload_date} bei uns im System "
          + "eingegangen ist.\n"
          + "Darin haben sie sich über das Fehlverhalten eines Fahrers beschwert.\n"
          + "Dieser war Ihren Schilderungen nach unfreundlich und hat sich nicht angemessen"
          + " verhalten"
          + ".\n"
          + "Diesen Vorfall bedauern wir und lassen den Fahrer durch den zuständigen Fachbereich "
          + "ansprechen.\n"
          + "Wir hoffen damit in Ihrem Sinn gehandelt zu haben.\n"
          + "Für die Zukunft wünschen wir Ihnen reibungslose Fahrten.";

  private static final String SONSTIGES_DEFAULT_TEMPLATE_TEXT =
      "Guten Tag,\n"
          + "vielen Dank für Ihre Beschwerde, die am ${upload_date} bei uns im System eingegangen"
          + " ist.\n"
          + "Wir haben die zuständigen Fachbereiche über den Sachverhalt verständigt.\n"
          + "Bitte haben Sie Verständnis dafür, dass wir, das Beschwerdemanagement, in die "
          + "Problemlösung nicht immer eingebunden sind.\n"
          + "Daher können wir Ihnen momentan keine zufriedenstellende Antwort geben. "
          + "Das bedauern wir sehr.\n"
          + "Für die Zukunft wünschen wir Ihnen allzeit gute Fahrt.";

  private static final String MISSING_SUBJECT_DEFAULT_TEMPLATE_TEXT =
      "Danke für ihre Nachricht "
          + "vom ${upload_date}!";

  private static ResponseTemplateManager instance;

  /**
   * Private constructor for the manager.
   */
  private ResponseTemplateManager() {
  }

  /**
   * Return the ResponseTemplateManager instance or create it.
   *
   * @return the current ResponseTemplateManager instance
   */
  public static ResponseTemplateManager instantiate() {
    if (instance == null) {
      instance = new ResponseTemplateManager();
    }
    return instance;
  }

  /**
   * Add a new template to the repository.
   *
   * @param templateRepository the template repository to use
   * @return the created template
   */
  public ResponseComponent addTemplate(TemplateRepository templateRepository,
                                       ResponseComponent responseComponent) {
    templateRepository.save(responseComponent);
    return responseComponent;
  }

  /**
   * Find the template with the given ID.
   *
   * @param templateRepository the template repository to use
   * @param id                 the ID to look for
   * @return the response template with the given ID
   */
  public ResponseComponent getTemplateByID(TemplateRepository templateRepository, int id) {
    return templateRepository.findById(id)
        .orElseThrow(() -> NOT_FOUND_EXCEPTION);
  }

  /**
   * Find a template with the given subject.
   *
   * @param templateRepository the template repository to use
   * @param subject            the subject to look for
   * @return a response template with the given subject
   */
  public ResponseComponent getTemplateBySubject(TemplateRepository templateRepository,
                                                String subject) {

    ArrayList<ResponseComponent> possibleTemplates = new ArrayList<>();

    // Find all templates with the correct subject
    for (ResponseComponent template : templateRepository.findAll()) {
      boolean templateFits = template.getSubject().map(s -> s.equals(subject)).orElse(true);
      if (templateFits) {
        possibleTemplates.add(template);
      }
    }

    // Use default templates if none are available
    if (possibleTemplates.isEmpty()) {
      String templateText = getDefaultTemplateText(subject);
      ResponseComponent responseTemplate = ResponseTemplateFactory.createTemplate(templateText,
                                                                                  subject,
                                                                                  "COMPLETE_TEXT");
      templateRepository.save(responseTemplate);
      return responseTemplate;
    }

    // Pick a random template and return it
    Random randomize = new Random();
    return possibleTemplates.get(randomize.nextInt(possibleTemplates.size()));
  }

  /**
   * Returns a default template text, if no templates are available.
   *
   * @param subject the subject of the template
   * @return the default template text for the given subject
   */
  private String getDefaultTemplateText(String subject) {
    String templateText;
    switch (subject) {
      case "Fahrt nicht erfolgt":
        templateText = FAHRT_NICHT_ERFOLGT_DEFAULT_TEMPLATE_TEXT;
        break;
      case "Fahrer unfreundlich":
        templateText = FAHRER_UNFREUNDLICH_DEFAULT_TEMPLATE_TEXT;
        break;
      case "Sonstiges":
        templateText = SONSTIGES_DEFAULT_TEMPLATE_TEXT;
        break;
      default:
        templateText = MISSING_SUBJECT_DEFAULT_TEMPLATE_TEXT;
    }
    return templateText;
  }

  /**
   * Delete the template with the given ID.
   *
   * @param templateRepository the template repository to use
   * @param id                 the ID of the template to delete
   */
  public void deleteTemplateByID(TemplateRepository templateRepository, int id) {
    templateRepository.deleteById(id);
  }
}
