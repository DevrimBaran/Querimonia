package de.fraunhofer.iao.querimonia.rest.manager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Manager for creating, viewing and deleting Response Templates.
 *
 * @author Simon Weiler
 */
public class ResponseComponentManager {

  // TODO: This is a temporary fix, default templates should be loaded from separate file!
  private final String defaultTemplatesJson = "[\n" +
          "  {\n" +
          "    \"componentName\": \"BegrüßungMitName\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><PredecessorCount max=\\\"0\\\"/><EntityAvailable label=\\\"Name\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Sehr geehrter Herr/Frau ${Name},\\n\",\n" +
          "      \"Liebe/Lieber Frau/Herr ${Name},\\n\",\n" +
          "      \"Guten Tag, Frau/Herr ${Name},\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"BegrüßungOhneName\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><PredecessorCount max=\\\"0\\\"/><Not><EntityAvailable label=\\\"Name\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Sehr geehrter Kundin/Kunde,\\n\",\n" +
          "      \"Liebe/Lieber Kundin/Kunde,\\n\",\n" +
          "      \"Guten Tag,\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"EingangsdatumMitDatum\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Begrüßung.*\\\" position=\\\"last\\\"/><EntityAvailable label=\\\"UploadDatum\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"vielen Dank für Ihre Beschwerde, die am ${UploadDatum} bei uns im System eingegangen ist.\\n\",\n" +
          "      \"vielen Dank für Ihre Beschwerde, die wir am ${UploadDatum} erhalten haben.\\n\",\n" +
          "      \"vielen Dank für Ihre Beschwerde, die Sie am ${UploadDatum} bei uns eingereicht haben.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"EingangsdatumOhneDatum\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Begrüßung.*\\\" position=\\\"last\\\"/><Not><EntityAvailable label=\\\"UploadDatum\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"vielen Dank für Ihre Beschwerde, die kürzlich bei uns im System eingegangen ist.\\n\",\n" +
          "      \"vielen Dank für Ihre Beschwerde, die wir vor Kurzem erhalten haben.\\n\",\n" +
          "      \"vielen Dank für Ihre Beschwerde, die Sie vor Kurzem bei uns eingereicht haben.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungFahrerUnfreundlichKeinePlatzhalter\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrer unfreundlich\\\"/><Not><EntityAvailable label=\\\"Haltestelle\\\"/></Not><Not><EntityAvailable label=\\\"Linie\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin haben Sie sich über das Fehlverhalten eines Fahrers beschwert.\\n\",\n" +
          "      \"Der Grund für Ihre Beschwerde war das Fehlverhalten eines Fahrers.\\n\",\n" +
          "      \"Sie beschwerten sich über einen Fahrer, der sich unangemessen verhalten hat.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungFahrerUnfreundlichNurLinie\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrer unfreundlich\\\"/><Not><EntityAvailable label=\\\"Haltestelle\\\"/></Not><EntityAvailable label=\\\"Linie\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin haben Sie sich über das Fehlverhalten eines Fahrers der Linie ${Linie} beschwert.\\n\",\n" +
          "      \"Der Grund für Ihre Beschwerde war das Fehlverhalten eines Fahrers der Linie ${Linie}.\\n\",\n" +
          "      \"Sie beschwerten sich über einen Fahrer der Linie ${Linie}, der sich unangemessen verhalten hat.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungFahrerUnfreundlichNurHaltestelle\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrer unfreundlich\\\"/><EntityAvailable label=\\\"Haltestelle\\\"/><Not><EntityAvailable label=\\\"Linie\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin haben Sie sich über das Fehlverhalten eines Fahrers an der Haltestelle ${Haltestelle} beschwert.\\n\",\n" +
          "      \"Der Grund für Ihre Beschwerde war das Fehlverhalten eines Fahrers an der Haltestelle ${Haltestelle}.\\n\",\n" +
          "      \"Sie beschwerten sich über einen Fahrer an der Haltestelle ${Haltestelle}, der sich unangemessen verhalten hat.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungFahrerUnfreundlichLinieUndHaltestelle\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrer unfreundlich\\\"/><EntityAvailable label=\\\"Haltestelle\\\"/><EntityAvailable label=\\\"Linie\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin haben Sie sich über das Fehlverhalten eines Fahrers der Linie ${Linie} an der Haltestelle ${Haltestelle} beschwert.\\n\",\n" +
          "      \"Der Grund für Ihre Beschwerde war das Fehlverhalten eines Fahrers der Linie ${Linie} an der Haltestelle ${Haltestelle}.\\n\",\n" +
          "      \"Sie beschwerten sich über einen Fahrer der Linie ${Linie} an der Haltestelle ${Haltestelle}, der sich unangemessen verhalten hat.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungFahrtNichtErfolgtKeinePlatzhalter\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrt nicht erfolgt\\\"/><Not><EntityAvailable label=\\\"Haltestelle\\\"/></Not><Not><EntityAvailable label=\\\"Linie\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin haben Sie sich über eine nicht erfolgte Fahrt beschwert.\\n\",\n" +
          "      \"Sie beschwerten sich darüber, dass die betroffene Haltestelle nicht bedient wurde.\\n\",\n" +
          "      \"Der Auslöser für Ihre Beschwerde war eine nicht erfolgte Fahrt.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungFahrtNichtErfolgtNurLinie\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrt nicht erfolgt\\\"/><Not><EntityAvailable label=\\\"Haltestelle\\\"/></Not><EntityAvailable label=\\\"Linie\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin haben Sie sich über eine nicht erfolgte Fahrt der Linie ${Linie} beschwert.\\n\",\n" +
          "      \"Sie beschwerten sich darüber, dass die betroffene Haltestelle von der Linie ${Linie} nicht bedient wurde.\\n\",\n" +
          "      \"Der Auslöser für Ihre Beschwerde war eine nicht erfolgte Fahrt der Linie ${Linie}.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungFahrtNichtErfolgtNurHaltestelle\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrt nicht erfolgt\\\"/><EntityAvailable label=\\\"Haltestelle\\\"/><Not><EntityAvailable label=\\\"Linie\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin haben Sie sich über eine nicht erfolgte Fahrt an der Haltestelle ${Haltestelle} beschwert.\\n\",\n" +
          "      \"Sie beschwerten sich darüber, dass die Haltestelle ${Haltestelle} nicht bedient wurde.\\n\",\n" +
          "      \"Der Auslöser für Ihre Beschwerde war eine nicht erfolgte Fahrt an der Haltestelle ${Haltestelle}.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungFahrtNichtErfolgtLinieUndHaltestelle\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrt nicht erfolgt\\\"/><EntityAvailable label=\\\"Haltestelle\\\"/><EntityAvailable label=\\\"Linie\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin haben Sie sich über eine nicht erfolgte Fahrt der Linie ${Linie} an der Haltestelle ${Haltestelle} beschwert.\\n\",\n" +
          "      \"Sie beschwerten sich darüber, dass die Haltestelle ${Haltestelle} von der Linie ${Linie} nicht bedient wurde.\\n\",\n" +
          "      \"Der Auslöser für Ihre Beschwerde war eine nicht erfolgte Fahrt der Linie ${Linie} an der Haltestelle ${Haltestelle}.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungSonstigesKeinePlatzhalter\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Sonstiges\\\"/><Not><EntityAvailable label=\\\"Haltestelle\\\"/></Not><Not><EntityAvailable label=\\\"Linie\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin beschwerten Sie sich über ein Problem mit unserem Betrieb.\\n\",\n" +
          "      \"Der Auslöser für Ihre Beschwerde war ein Problem mit unserem Betrieb.\\n\",\n" +
          "      \"Darin äußerten Sie Kritik an unserem Betrieb.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungSonstigesNurLinie\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Sonstiges\\\"/><Not><EntityAvailable label=\\\"Haltestelle\\\"/></Not><EntityAvailable label=\\\"Linie\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin beschwerten Sie sich über ein Problem mit unserem Betrieb der Linie ${Linie}.\\n\",\n" +
          "      \"Der Auslöser für Ihre Beschwerde war ein Problem mit unserem Betrieb der Linie ${Linie}.\\n\",\n" +
          "      \"Darin äußerten Sie Kritik an unserem Betrieb der Linie ${Linie}.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungSonstigesNurHaltestelle\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Sonstiges\\\"/><EntityAvailable label=\\\"Haltestelle\\\"/><Not><EntityAvailable label=\\\"Linie\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin beschwerten Sie sich über ein Problem mit unserem Betrieb an der Haltestelle ${Haltestelle}.\\n\",\n" +
          "      \"Der Auslöser für Ihre Beschwerde war ein Problem mit unserem Betrieb an der Haltestelle ${Haltestelle}.\\n\",\n" +
          "      \"Darin äußerten Sie Kritik an unserem Betrieb an der Haltestelle ${Haltestelle}.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ProblembeschreibungSonstigesLinieUndHaltestelle\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Eingangsdatum.*\\\" position=\\\"last\\\"/><Subject value=\\\"Sonstiges\\\"/><EntityAvailable label=\\\"Haltestelle\\\"/><EntityAvailable label=\\\"Linie\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Darin beschwerten Sie sich über ein Problem mit unserem Betrieb der Linie ${Linie} an der Haltestelle ${Haltestelle}.\\n\",\n" +
          "      \"Der Auslöser für Ihre Beschwerde war ein Problem mit unserem Betrieb der Linie ${Linie} an der Haltestelle ${Haltestelle}.\\n\",\n" +
          "      \"Darin äußerten Sie Kritik an unserem Betrieb der Linie ${Linie} an der Haltestelle ${Haltestelle}.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"EntschuldigungFahrerUnfreundlich\",\n" +
          "    \"priority\": 50,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Problembeschreibung.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrer unfreundlich\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Wir bedauern das Verhalten unseres Fahrers sehr und entschuldigen uns dafür.\\n\",\n" +
          "      \"Für das Verhalten unseres Fahrers und die daraus für Sie entstandenen Unannehmlichkeiten bitten wir um Entschuldigung.\\n\",\n" +
          "      \"Dass Sie von diesem Fehlverhalten betroffen waren, bedauern wir und entschuldigen uns für die daraus entstandenen Unannehmlichkeiten.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"EntschuldigungFahrtNichtErfolgt\",\n" +
          "    \"priority\": 50,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Problembeschreibung.*\\\" position=\\\"last\\\"/><Subject value=\\\"Fahrt nicht erfolgt\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Dass Sie von diesem Ausfall betroffen waren, bedauern wir und entschuldigen uns für die Unannehmlichkeiten, die Ihnen hieraus entstanden sind.\\n\",\n" +
          "      \"Wir bedauern den Ausfall der betroffenen Fahrt und bitten Sie, die daraus entstandenen Unannehmlichkeiten zu entschuldigen.\\n\",\n" +
          "      \"Für den Ausfall der betroffenen Fahrt und die daraus für Sie enstandenen Unannehmlichkeiten entschuldigen wir uns.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"EntschuldigungSonstiges\",\n" +
          "    \"priority\": 50,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Problembeschreibung.*\\\" position=\\\"last\\\"/><Subject value=\\\"Sonstiges\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Wir bedauern den Vorfall sowie die daraus für Sie entstandenen Unannehmlichkeiten sehr und entschuldigen uns dafür.\\n\",\n" +
          "      \"Ihre Verärgerung über den Vorfall ist nachvollziehbar und wir bitten Sie diesbezüglich um Entschuldigung.\\n\",\n" +
          "      \"Der Vorfall ist bedauernswert und wir bitten Sie, die daraus entstandenen Unannehmlichkeiten zu entschuldigen.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"LösungsvorschlagFahrerUnfreundlich\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Problembeschreibung.*\\\" position=\\\"any\\\"/><Subject value=\\\"Fahrer unfreundlich\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Über den von Ihnen geschilderten Sachverhalt haben wir den Bereich Fahrdienst informiert, damit dort ein Gespräch mit dem betreffenden Personal geführt werden kann.\\n\",\n" +
          "      \"Wir lassen den Fahrer durch den zuständigen Fachbereich ansprechen.\\n\",\n" +
          "      \"Wir haben Ihr Anliegen an den Teamleiter des betreffenden Fahrers weitergegeben, der ihn diesbezüglich ansprechen wird.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"LösungsvorschlagFahrtNichtErfolgt\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Problembeschreibung.*\\\" position=\\\"any\\\"/><Subject value=\\\"Fahrt nicht erfolgt\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Der Grund für den Ausfall war eine unerwartete Störung in unserem Betrieb. Diese wurde nun behoben, sodass keine weiteren Ausfälle diesbezüglich zu erwarten sind.\\n\",\n" +
          "      \"Die Störung, welche den Ausfall versursacht hat, wurde inzwischen behoben und die betroffenen Linien und Haltestellen werden wieder planmäßig betrieben.\\n\",\n" +
          "      \"Der Ausfall ist auf eine Störung in unserem Betrieb zurückzuführen, welche mittlerweile behoben wurde. Der Betrieb sollte daher wieder planmäßig laufen.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"LösungsvorschlagSonstiges\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Problembeschreibung.*\\\" position=\\\"any\\\"/><Subject value=\\\"Sonstiges\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Wir haben die zuständigen Fachbereiche über den Sachverhalt informiert. Eine Lösung ist derzeit in Arbeit.\\n\",\n" +
          "      \"Die zuständigen Fachbereiche wurden informiert, sodass schnellstmöglich eine Lösung erarbeitet werden kann.\\n\",\n" +
          "      \"Wir haben das Problem an die zuständigen Fachbereiche weitergeleitet, welche aktuell an einer Lösung dafür arbeiten.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ErstattungMitGeldbetrag\",\n" +
          "    \"priority\": 50,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Lösungsvorschlag.*\\\" position=\\\"last\\\"/><EntityAvailable label=\\\"Geldbetrag\\\"/></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Die entstandenen Kosten in Höhe von ${Geldbetrag} werden Ihnen baldmöglichst zurückerstattet.\\n\",\n" +
          "      \"Der Betrag von ${Geldbetrag} wird Ihrem Konto schnellstmöglich gutgeschrieben.\\n\",\n" +
          "      \"Als Entschädigung wird Ihnen der genannte Betrag von ${Geldbetrag} so bald wie möglich überwiesen.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"ErstattungOhneGeldbetrag\",\n" +
          "    \"priority\": 50,\n" +
          "    \"rulesXml\": \"<Rules><And><Predecessor matches=\\\"Lösungsvorschlag.*\\\" position=\\\"last\\\"/><Not><EntityAvailable label=\\\"Geldbetrag\\\"/></Not></And></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Die entstandenen Kosten werden Ihnen baldmöglichst zurückerstattet.\\n\",\n" +
          "      \"Ein entsprechender Geldbetrag wird Ihrem Konto als Kompensation schnellstmöglich gutgeschrieben.\\n\",\n" +
          "      \"Als Entschädigung wird Ihnen so bald wie möglich ein entsprechender Geldbetrag überwiesen.\\n\"\n" +
          "    ]\n" +
          "  },\n" +
          "  {\n" +
          "    \"componentName\": \"Schluss\",\n" +
          "    \"priority\": 100,\n" +
          "    \"rulesXml\": \"<Rules><Predecessor matches=\\\"Lösungsvorschlag.*\\\" position=\\\"any\\\"/></Rules>\",\n" +
          "    \"templateTexts\": [\n" +
          "      \"Wir hoffen, damit in Ihrem Sinne gehandelt zu haben.\\nFür die Zukunft wünschen wir Ihnen alles Gute.\\n\",\n" +
          "      \"Wir hoffen Ihnen damit behilflich gewesen sein zu können.\\nFür die Zukunft wünschen wir Ihnen reibungslose Fahrten.\\n\",\n" +
          "      \"Wir hoffen in Ihrem Interesse gehandelt zu haben.\\nFür die Zukunft wünschen wir Ihnen reibungslose Fahrten mit unseren Verkehrsmitteln.\\n\"\n" +
          "    ]\n" +
          "  }\n" +
          "]";

  private static final ResponseStatusException NOT_FOUND_EXCEPTION
      = new ResponseStatusException(HttpStatus.NOT_FOUND, "Component does not exist!");

  private static final ResponseStatusException JSON_PARSE_EXCEPTION
      = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
      "Contents of DefaultTemplates.json are not valid JSON code!");

  private static final ResponseStatusException JSON_MAPPING_EXCEPTION
      = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
      "Could not map contents of DefaultTemplates.json to an array of ResponseComponents!");

  private static final ResponseStatusException FILE_NOT_FOUND_EXCEPTION
      = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
      "Could not find DefaultTemplates.json!");

  private static final ResponseStatusException FILE_IO_EXCEPTION
      = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
      "Could not read DefaultTemplates.json!");


  /**
   * Add a new component to the repository.
   *
   * @param templateRepository the component repository to use
   * @return the created component
   */
  public synchronized ResponseComponent addTemplate(TemplateRepository templateRepository,
                                                    ResponseComponent responseComponent) {
    templateRepository.save(responseComponent);
    return responseComponent;
  }

  /**
   * Add a set of default templates to the repository. Default templates can be found in
   * resources/DefaultTemplates.json.
   *
   * @return the list of default templates
   */
  public synchronized List<ResponseComponent> addDefaultTemplates(
          TemplateRepository templateRepository) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      /*DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
      Resource defaultTemplatesResource = defaultResourceLoader
              .getResource("DefaultTemplates.json");

      if (!defaultTemplatesResource.exists()) {
        throw FILE_NOT_FOUND_EXCEPTION;
      }

      File defaultTemplatesFile = defaultTemplatesResource.getFile();

      List<ResponseComponent> defaultTemplates = Arrays.asList(objectMapper.readValue(
              defaultTemplatesFile, ResponseComponent[].class));*/

      List<ResponseComponent> defaultTemplates = Arrays.asList(objectMapper.readValue(
              defaultTemplatesJson, ResponseComponent[].class));

      defaultTemplates.forEach(templateRepository::save);
      return defaultTemplates;

    } catch (JsonParseException e) {
      throw JSON_PARSE_EXCEPTION;
    } catch (JsonMappingException e) {
      throw JSON_MAPPING_EXCEPTION;
    } catch (FileNotFoundException e) {
      throw FILE_NOT_FOUND_EXCEPTION;
    } catch (IOException e) {
      throw FILE_IO_EXCEPTION;
    }
  }

  /**
   * Find the component with the given ID.
   *
   * @param templateRepository the component repository to use
   * @param id                 the ID to look for
   * @return the response component with the given ID
   */
  public synchronized ResponseComponent getTemplateByID(TemplateRepository templateRepository,
                                                        int id) {
    return templateRepository.findById(id)
        .orElseThrow(() -> NOT_FOUND_EXCEPTION);
  }

  /**
   * Delete the component with the given ID.
   *
   * @param templateRepository the component repository to use
   * @param id                 the ID of the component to delete
   */
  public synchronized void deleteTemplateByID(TemplateRepository templateRepository, int id) {
    templateRepository.deleteById(id);
  }
}
