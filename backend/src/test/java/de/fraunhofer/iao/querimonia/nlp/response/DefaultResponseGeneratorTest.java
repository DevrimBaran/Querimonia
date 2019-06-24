/*
package de.fraunhofer.iao.querimonia.nlp.response;

import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

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
    private static ResponseComponent testComponentStopAndLines;
    private static ResponseComponent testComponentMoney;
    private static ResponseComponent testComponentFinish;

    private static  List<ResponseComponent> testTemplates = new ArrayList<>();

    @BeforeClass
    public static void initializeResponseComponents() {
        testComponentName = new ResponseComponent("Hallo ${Name}!", "Test", "Begrüßung",
                new ArrayList<>(Collections.singletonList("Eingangsdatum")));

        testComponentDate = new ResponseComponent("Danke für deine Beschwerde vom ${Upload_Datum}.",
                "Test", "Eingangsdatum",
                new ArrayList<>(Collections.singletonList("Problembeschreibung")));

        testComponentStopAndLines = new ResponseComponent("Darin hast du dich über die Haltestelle ${Haltestelle} " +
                "der Linie ${Linie} beschwert.", "Test", "Problembeschreibung",
                new ArrayList<>(Collections.singletonList("Erstattung")));

        testComponentMoney = new ResponseComponent("Dafür zahlen wir dir ${Geldbetrag}!", "Test", "Erstattung",
                new ArrayList<>(Collections.singletonList("Schluss")));

        testComponentFinish = new ResponseComponent("Tschüss!", "Test", "Schluss",
                new ArrayList<>());

        testTemplates.add(testComponentName);
        testTemplates.add(testComponentDate);
        testTemplates.add(testComponentStopAndLines);
        testTemplates.add(testComponentMoney);
        testTemplates.add(testComponentFinish);
    }

    @Before
    public void setup() {
        TemplateRepository templateRepository = mock(TemplateRepository.class);
        defaultResponseGenerator = new DefaultResponseGenerator(templateRepository);
        when(templateRepository.findAll()).thenReturn(testTemplates);
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
        entities.add(new NamedEntity("Name", 8, 13));
        entities.add(new NamedEntity("Haltestelle", 31, 43));
        entities.add(new NamedEntity("Linie", 54, 56));
        entities.add(new NamedEntity("Geldbetrag", 86, 89));

        LocalDateTime uploadTime = LocalDateTime.of(2019, 6, 20, 18, 0);

        List<CompletedResponseComponent> correctResponseComponents = new ArrayList<>();
        correctResponseComponents.add(new CompletedResponseComponent("Hallo Peter!",
                testComponentName,
                new ArrayList<>(Collections.singletonList(new NamedEntity("Name", 6, 11)))));

        correctResponseComponents.add(new CompletedResponseComponent("Danke für deine Beschwerde vom " +
                "20. Juni 2019.",
                testComponentDate,
                new ArrayList<>(Collections.singletonList(new NamedEntity("Upload_Datum", 31, 44)))));

        correctResponseComponents.add(new CompletedResponseComponent("Darin hast du dich über die " +
                "Haltestelle Hauptbahnhof der Linie U6 beschwert.",
                testComponentStopAndLines,
                new ArrayList<>(Arrays.asList(new NamedEntity("Haltestelle", 40, 52), new NamedEntity("Linie", 63, 65)))));

        correctResponseComponents.add(new CompletedResponseComponent("Dafür zahlen wir dir 20€!",
                testComponentMoney,
                new ArrayList<>(Collections.singletonList(new NamedEntity("Geldbetrag", 21, 24)))));

        correctResponseComponents.add(new CompletedResponseComponent("Tschüss!",
                testComponentFinish,
                new ArrayList<>()));

        ResponseSuggestion testResponseSuggestion = defaultResponseGenerator.generateResponse(text, subjectMap, sentimentMap, entities, uploadTime);
        List<CompletedResponseComponent> testResponseComponents = testResponseSuggestion.getAlternatives();

        for (int i = 0; i < correctResponseComponents.size(); i++) {
            CompletedResponseComponent correctComponent = correctResponseComponents.get(i);
            CompletedResponseComponent testComponent = testResponseComponents.get(i);

            assertEquals(correctComponent.getCompletedText(), testComponent.getCompletedText());
            assertEquals(correctComponent.getComponent(), testComponent.getComponent());

            List<NamedEntity> correctEntities = correctComponent.getEntities();
            List<NamedEntity> testEntities = testComponent.getEntities();
            for(int j = 0; j < correctEntities.size(); j++) {
                NamedEntity correctEntity = correctEntities.get(j);
                NamedEntity testEntity = testEntities.get(j);
                assertEquals(correctEntity.toString(), testEntity.toString());
            }
        }
    }
}
*/
