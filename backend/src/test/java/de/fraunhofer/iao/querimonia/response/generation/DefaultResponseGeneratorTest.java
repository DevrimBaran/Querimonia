/*
package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.complaint.ComplaintData;
import de.fraunhofer.iao.querimonia.db.repository.ActionRepository;
import de.fraunhofer.iao.querimonia.db.repository.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


*/
/**
 * Unit test class for DefaultResponseGenerator.
 *
 * @author Simon Weiler
 *//*


public class DefaultResponseGeneratorTest {

  private DefaultResponseGenerator defaultResponseGenerator;

  private static ResponseComponent testComponentName;
  private static ResponseComponent testComponentDate;
  private static ResponseComponent testComponentStopAndLinesNone;
  private static ResponseComponent testComponentStopAndLinesOnlyStop;
  private static ResponseComponent testComponentStopAndLinesOnlyLine;
  private static ResponseComponent testComponentStopAndLinesAll;
  private static ResponseComponent testComponentMoneyWith;
  private static ResponseComponent testComponentMoneyWithout;
  private static ResponseComponent testComponentFinish;

  private static List<ResponseComponent> testTemplates = new ArrayList<>();

  @BeforeClass
  public static void initializeResponseComponents() {
    testComponentName = new ResponseComponent("Begrüßung",
            Collections.singletonList("Hallo ${Name}!"),
            "<Rules>" +
                    "<And>" +
                    "<PredecessorCount max=\"0\" />" +
                    "<EntityAvailable label=\"Name\" />" +
                    "</And>" +
                    "</Rules>");

    testComponentDate = new ResponseComponent("Eingangsdatum",
            Collections.singletonList("Danke für deine Beschwerde vom ${UploadDatum}."),
            "<Rules>" +
                    "<Predecessor matches=\"Begrüßung\" position=\"last\" />" +
                    "</Rules>");

    testComponentStopAndLinesNone = new ResponseComponent("ProblembeschreibungKeinePlatzhalter",
            Collections.singletonList("Darin hast du dich über ein Problem mit unserem Betrieb beschwert."),
            "<Rules>" +
                    "<And>" +
                    "<Predecessor matches=\"Eingangsdatum\" position=\"last\" />" +
                    "<Not>" +
                    "<EntityAvailable label=\"Haltestelle\" />" +
                    "</Not>" +
                    "<Not>" +
                    "<EntityAvailable label=\"Linie\" />" +
                    "</Not>" +
                    "</And>" +
                    "</Rules>");

    testComponentStopAndLinesOnlyStop = new ResponseComponent("ProblembeschreibungNurHaltestelle",
            Collections.singletonList("Darin hast du dich über die Haltestelle ${Haltestelle} beschwert."),
            "<Rules>" +
                    "<And>" +
                    "<Predecessor matches=\"Eingangsdatum\" position=\"last\" />" +
                    "<EntityAvailable label=\"Haltestelle\" />" +
                    "<Not>" +
                    "<EntityAvailable label=\"Linie\" />" +
                    "</Not>" +
                    "</And>" +
                    "</Rules>");

    testComponentStopAndLinesOnlyLine = new ResponseComponent("ProblembeschreibungNurLinie",
            Collections.singletonList("Darin hast du dich über die Linie ${Linie} beschwert."),
            "<Rules>" +
                    "<And>" +
                    "<Predecessor matches=\"Eingangsdatum\" position=\"last\" />" +
                    "<Not>" +
                    "<EntityAvailable label=\"Haltestelle\" />" +
                    "</Not>" +
                    "<EntityAvailable label=\"Linie\" />" +
                    "</And>" +
                    "</Rules>");

    testComponentStopAndLinesAll = new ResponseComponent("ProblembeschreibungHaltestelleUndLinie",
            Collections.singletonList("Darin hast du dich über die Haltestelle ${Haltestelle} der Linie ${Linie} beschwert."),
            "<Rules>" +
                    "<And>" +
                    "<Predecessor matches=\"Eingangsdatum\" position=\"last\" />" +
                    "<EntityAvailable label=\"Haltestelle\" />" +
                    "<EntityAvailable label=\"Linie\" />" +
                    "</And>" +
                    "</Rules>");

    testComponentMoneyWith = new ResponseComponent("ErstattungMitGeldbetrag",
            Collections.singletonList("Dafür zahlen wir dir ${Geldbetrag}!"),
            "<Rules>" +
                    "<And>" +
                    "<Predecessor matches=\"Problembeschreibung.*\" position=\"last\" />" +
                    "<EntityAvailable label=\"Geldbetrag\" />" +
                    "</And>" +
                    "</Rules>");

    testComponentMoneyWithout = new ResponseComponent("ErstattungOhneGeldbetrag",
            Collections.singletonList("Dafür zahlen wir dir etwas Geld!"),
            "<Rules>" +
                    "<And>" +
                    "<Predecessor matches=\"Problembeschreibung\" position=\"last\" />" +
                    "<Not>" +
                    "<EntityAvailable label=\"Geldbetrag\" />" +
                    "</Not>" +
                    "</And>" +
                    "</Rules>");

    testComponentFinish = new ResponseComponent("Schluss",
            Collections.singletonList("Tschüss!"),
            "<Rules>" +
                    "<Predecessor matches=\"Erstattung.*\" position=\"last\" />" +
                    "</Rules>");

    testTemplates.add(testComponentName);
    testTemplates.add(testComponentDate);
    testTemplates.add(testComponentStopAndLinesNone);
    testTemplates.add(testComponentStopAndLinesOnlyStop);
    testTemplates.add(testComponentStopAndLinesOnlyLine);
    testTemplates.add(testComponentStopAndLinesAll);
    testTemplates.add(testComponentMoneyWith);
    testTemplates.add(testComponentMoneyWithout);
    testTemplates.add(testComponentFinish);
  }

  @Before
  public void setup() {
    ResponseComponentRepository templateRepository = mock(ResponseComponentRepository.class);
    ActionRepository actionRepository = mock(ActionRepository.class);
    defaultResponseGenerator = new DefaultResponseGenerator(templateRepository, actionRepository);
    when(templateRepository.findAll()).thenReturn(testTemplates);
    when(actionRepository.findAll()).thenReturn(new ArrayList<>());
  }

  @Test
  public void testGenerateResponseStandard() {
    String text = "Ich bin Peter. Die Haltestelle Hauptbahnhof der Linie U6 ist schlecht. Bitte gebt mir 20€!";

    Map<String, Double> subjectMap = new HashMap<>();
    subjectMap.put("Kein Test", 0.0);
    subjectMap.put("Bestimmt kein Test", 0.5);
    subjectMap.put("Test", 0.85);

    Map<String, Double> sentimentMap = new HashMap<>();
    sentimentMap.put("Freude", 0.0);
    sentimentMap.put("Wut", 0.5);
    sentimentMap.put("Trauer", 0.25);

    List<NamedEntity> entities = new ArrayList<>();
    entities.add(new NamedEntity("Name", 8, 13,null));
    entities.add(new NamedEntity("Haltestelle", 31, 43,null));
    entities.add(new NamedEntity("Linie", 54, 56,null));
    entities.add(new NamedEntity("Geldbetrag", 86, 89,null));

    SingleCompletedComponent testSingleComponentName = new SingleCompletedComponent("Hallo Peter!",
            Collections.singletonList(new NamedEntity("Name", 6, 11,null)));

    SingleCompletedComponent testSingleComponentDate = new SingleCompletedComponent("Danke für deine " +
            "Beschwerde vom 20. Juni 2019.",
            Collections.singletonList(new NamedEntity("UploadDatum", 31, 44,null)));

    SingleCompletedComponent testSingleComponentStopAndLines = new SingleCompletedComponent("Darin hast " +
            "du dich über die Haltestelle Hauptbahnhof der Linie U6 beschwert.",
            new ArrayList<>(Arrays.asList(
                    new NamedEntity("Haltestelle", 40, 52,null),
                    new NamedEntity("Linie", 63, 65,null))));

    SingleCompletedComponent testSingleComponentMoney = new SingleCompletedComponent("Dafür zahlen wir " +
            "dir 20€!",
            Collections.singletonList(new NamedEntity("Geldbetrag", 21, 24,null)));

    SingleCompletedComponent testSingleComponentFinish = new SingleCompletedComponent("Tschüss!",
            new ArrayList<>());

    List<CompletedResponseComponent> correctResponseComponents = new ArrayList<>();
    correctResponseComponents.add(new CompletedResponseComponent(Collections.singletonList(testSingleComponentName),
            testComponentName));

    correctResponseComponents.add(new CompletedResponseComponent(Collections.singletonList(testSingleComponentDate),
            testComponentDate));

    correctResponseComponents.add(new CompletedResponseComponent(Collections.singletonList(testSingleComponentStopAndLines),
            testComponentStopAndLinesAll));

    correctResponseComponents.add(new CompletedResponseComponent(Collections.singletonList(testSingleComponentMoney),
            testComponentMoneyWith));

    correctResponseComponents.add(new CompletedResponseComponent(Collections.singletonList(testSingleComponentFinish),
            testComponentFinish));

    ComplaintData testComplaintData = new ComplaintData(text,
            subjectMap,
            sentimentMap,
            entities,
            LocalDateTime.of(2019, 6, 20, 18, 0));

    ResponseSuggestion testResponseSuggestion = defaultResponseGenerator.generateResponse(testComplaintData);
    List<CompletedResponseComponent> testResponseComponents = testResponseSuggestion.getResponseComponents();

    for (int i = 0; i < correctResponseComponents.size(); i++) {
      CompletedResponseComponent correctComponent = correctResponseComponents.get(i);
      CompletedResponseComponent testComponent = testResponseComponents.get(i);

      assertEquals(correctComponent.getAlternatives().get(0).getCompletedText(),
              testComponent.getAlternatives().get(0).getCompletedText());

      assertEquals(correctComponent.getComponent(), testComponent.getComponent());

      List<NamedEntity> correctEntities = correctComponent.getAlternatives()
              .stream()
              .flatMap(s -> s.getEntities().stream())
              .collect(Collectors.toList());

      List<NamedEntity> testEntities = testComponent.getAlternatives()
              .stream()
              .flatMap(s -> s.getEntities().stream())
              .collect(Collectors.toList());

      for (int j = 0; j < correctEntities.size(); j++) {
        NamedEntity correctEntity = correctEntities.get(j);
        NamedEntity testEntity = testEntities.get(j);
        assertEquals(correctEntity, testEntity);
      }
    }
  }
}
*/
