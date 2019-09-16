
package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.manager.ResponseComponentManager;
import de.fraunhofer.iao.querimonia.repository.MockComplaintRepository;
import de.fraunhofer.iao.querimonia.repository.MockCompletedComponentRepository;
import de.fraunhofer.iao.querimonia.repository.MockComponentRepository;
import de.fraunhofer.iao.querimonia.repository.MockSuggestionRepository;
import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.COMPLAINT_F;
import static de.fraunhofer.iao.querimonia.response.component.TestComponents.*;
import static org.junit.Assert.assertFalse;


/**
 * Unit test class for DefaultResponseGenerator.
 *
 * @author Simon Weiler
 */


public class DefaultResponseGeneratorTest {

  private MockComponentRepository mockComponentRepository;
  private DefaultResponseGenerator defaultResponseGenerator;

  private static List<ResponseComponent> testComponents = new ArrayList<>();

  @BeforeClass
  public static void initializeResponseComponents() {
    testComponents.add(COMPONENT_NAME);
    testComponents.add(COMPONENT_DATE);
    testComponents.add(COMPONENT_TIME);
    testComponents.add(COMPONENT_PROBLEM_NONE);
    testComponents.add(COMPONENT_PROBLEM_ONLY_STOP);
    testComponents.add(COMPONENT_PROBLEM_ONLY_LINE);
    testComponents.add(COMPONENT_PROBLEM_STOP_AND_LINE);
    testComponents.add(COMPONENT_MONEY_WITH);
    testComponents.add(COMPONENT_MONEY_WITHOUT);
    testComponents.add(COMPONENT_FINISH);
  }

  @Before
  public void setup() {
    mockComponentRepository = new MockComponentRepository();
    mockComponentRepository.saveAll(testComponents);
    defaultResponseGenerator = new DefaultResponseGenerator(mockComponentRepository);
  }

  /* TODO fix test
  @Test
  public void testGenerateResponseStandard() {

    CompletedResponseComponent componentNameCompleted = new CompletedResponseComponent(COMPONENT_NAME,
        List.of(new NamedEntityBuilder()
            .setLabel("Name")
            .setStart(6)
            .setEnd(11)
            .setSetByUser(false)
            .setExtractor("None")
            .setValue("Peter")
            .createNamedEntity()));

    CompletedResponseComponent componentDateCompleted = new CompletedResponseComponent(COMPONENT_DATE,
        List.of(new NamedEntityBuilder()
            .setLabel("Eingangsdatum")
            .setStart(31)
            .setEnd(44)
            .setSetByUser(false)
            .setExtractor("None")
            .setValue("20. Juni 2019")
            .createNamedEntity()));

    CompletedResponseComponent componentTimeCompleted = new CompletedResponseComponent(COMPONENT_TIME,
        List.of(new NamedEntityBuilder()
            .setLabel("Eingangszeit")
            .setStart(31)
            .setEnd(44)
            .setSetByUser(false)
            .setExtractor("None")
            .setValue("12:00:00")
            .createNamedEntity()));

    CompletedResponseComponent componentStopAndLinesCompleted = new CompletedResponseComponent(COMPONENT_PROBLEM_STOP_AND_LINE,
        List.of(new NamedEntityBuilder()
                .setLabel("Haltestelle")
                .setStart(80)
                .setEnd(94)
                .setSetByUser(false)
                .setExtractor("None")
                .setValue("Hauptbahnhof")
                .createNamedEntity(),
            new NamedEntityBuilder()
                .setLabel("Linie")
                .setStart(105)
                .setEnd(107)
                .setSetByUser(false)
                .setExtractor("None")
                .setValue("U6")
                .createNamedEntity()));

    CompletedResponseComponent componentMoneyCompleted = new CompletedResponseComponent(COMPONENT_MONEY_WITH,
        List.of(new NamedEntityBuilder()
            .setLabel("Geldbetrag")
            .setStart(63)
            .setEnd(66)
            .setSetByUser(false)
            .setExtractor("None")
            .setValue("20€")
            .createNamedEntity()));

    CompletedResponseComponent componentFinishCompleted = new CompletedResponseComponent(COMPONENT_FINISH,
        Collections.emptyList());

    List<CompletedResponseComponent> correctResponseComponents = List.of(componentNameCompleted,
        componentDateCompleted, componentTimeCompleted, componentStopAndLinesCompleted,
        componentMoneyCompleted, componentFinishCompleted);

    ComplaintBuilder complaintBuilder = new ComplaintBuilder(COMPLAINT_F);

    ResponseSuggestion testResponseSuggestion = defaultResponseGenerator.generateResponse(complaintBuilder);
    assertEquals(Collections.emptyList(), testResponseSuggestion.getActions());
    List<CompletedResponseComponent> testResponseComponents = testResponseSuggestion.getResponseComponents();

    for (int i = 0; i < correctResponseComponents.size(); i++) {
      CompletedResponseComponent correctCompletedComponent = correctResponseComponents.get(i);
      CompletedResponseComponent testCompletedComponent = testResponseComponents.get(i);
      ResponseComponent correctComponent = correctCompletedComponent.getComponent();
      ResponseComponent testComponent = testCompletedComponent.getComponent();

      assertEquals(correctCompletedComponent.getId(),
          testCompletedComponent.getId());
      assertEquals(correctComponent, testComponent);

      for (int j = 0; j < correctCompletedComponent.getEntities().size(); j++) {
        NamedEntity correctEntity = correctCompletedComponent.getEntities().get(j);
        NamedEntity testEntity = testCompletedComponent.getEntities().get(j);
        assertEquals(correctEntity.getValue(), testEntity.getValue());
      }
    }
  }*/

  @Test
  public void testGenerationWithDefaultComponents() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir("src/test/resources/uploads/");
    FileStorageService fileStorageService = new FileStorageService(fileStorageProperties);

    ResponseComponentManager responseComponentManager = new ResponseComponentManager(
        mockComponentRepository,
        new MockComplaintRepository(),
        new MockSuggestionRepository(),
        new MockCompletedComponentRepository(),
        fileStorageService);

    responseComponentManager.addDefaultComponents();
    ComplaintBuilder complaintBuilder = new ComplaintBuilder(COMPLAINT_F);
    ResponseSuggestion testResponseSuggestion = defaultResponseGenerator.generateResponse(complaintBuilder);
    assertFalse(testResponseSuggestion.getResponseComponents().isEmpty());
  }
}
