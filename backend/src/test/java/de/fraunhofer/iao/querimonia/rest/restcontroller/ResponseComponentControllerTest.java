package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.manager.ResponseComponentManager;
import de.fraunhofer.iao.querimonia.repository.ComplaintRepository;
import de.fraunhofer.iao.querimonia.repository.MockComplaintRepository;
import de.fraunhofer.iao.querimonia.repository.MockCompletedComponentRepository;
import de.fraunhofer.iao.querimonia.repository.MockComponentRepository;
import de.fraunhofer.iao.querimonia.repository.MockSuggestionRepository;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import de.fraunhofer.iao.querimonia.utility.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static de.fraunhofer.iao.querimonia.response.component.TestComponents.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Unit test class for ResponseComponentController
 *
 * @author Simon Weiler
 */
public class ResponseComponentControllerTest {

  private MockComponentRepository componentRepository;
  private ResponseComponentController responseComponentController;

  @Before
  public void setup() {
    componentRepository = new MockComponentRepository();
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir("src/test/resources/uploads/");
    var fileStorageService = new FileStorageService(fileStorageProperties);
    ComplaintRepository complaintRepository = new MockComplaintRepository();
    ResponseComponentManager responseComponentManager =
        new ResponseComponentManager(componentRepository, complaintRepository,
            new MockSuggestionRepository(), new MockCompletedComponentRepository(),
            fileStorageService);
    responseComponentController = new ResponseComponentController(responseComponentManager);
  }

  @Test
  public void testAddComponent() {
    responseComponentController.addComponent(COMPONENT_E);
    ResponseComponent component = componentRepository.findAll().iterator().next();
    assertEquals(component, COMPONENT_E);
  }

  @Test
  public void testAddDefaultComponents() {
    ResponseEntity<?> responseEntity = responseComponentController.addDefaultComponents();
    assertNotNull(responseEntity.getBody());
    assertThat(responseEntity.getBody(), is(not(instanceOf(QuerimoniaException.class))));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsSimple() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(3), Optional.of(0), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(3, responseComponents.size());
    assertEquals(responseComponents.get(0), COMPONENT_A);
    assertEquals(responseComponents.get(1), COMPONENT_B);
    assertEquals(responseComponents.get(2), COMPONENT_E);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsCount() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(2), Optional.of(0), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(2, responseComponents.size());
    assertEquals(responseComponents.get(0), COMPONENT_A);
    assertEquals(responseComponents.get(1), COMPONENT_B);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsPaging() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(2), Optional.of(1), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(1, responseComponents.size());
    assertEquals(responseComponents.get(0), COMPONENT_E);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsSorting() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    String[] sortBy = {"name_desc"};
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(3), Optional.of(0), Optional.of(sortBy), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(3, responseComponents.size());
    assertEquals(responseComponents.get(0), COMPONENT_E);
    assertEquals(responseComponents.get(1), COMPONENT_B);
    assertEquals(responseComponents.get(2), COMPONENT_A);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsKeywordsComponentText() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    String[] keywords = {"Guten", "Herr"};
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(3), Optional.of(0), Optional.empty(), Optional.empty(), Optional.of(keywords));
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(1, responseComponents.size());
    assertEquals(responseComponents.get(0), COMPONENT_A);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsKeywordsComponentName() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    String[] keywords = {"B"};
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(3), Optional.of(0), Optional.empty(), Optional.empty(), Optional.of(keywords));
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(1, responseComponents.size());
    assertEquals(responseComponents.get(0), COMPONENT_B);
  }

  @Test
  public void testGetAllComponentsInvalidSortBy1() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    String[] sortBy = {"magic"};
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(3), Optional.of(0), Optional.of(sortBy), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    assertEquals(QuerimoniaException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testGetAllComponentsInvalidSortBy2() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    String[] sortBy = {"magic_asc"};
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(3), Optional.of(0), Optional.of(sortBy), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    assertEquals(QuerimoniaException.class, responseEntity.getBody().getClass());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsCountOversize() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(4), Optional.of(0), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(3, responseComponents.size());
    assertEquals(responseComponents.get(0), COMPONENT_A);
    assertEquals(responseComponents.get(1), COMPONENT_B);
    assertEquals(responseComponents.get(2), COMPONENT_E);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsCountEmpty() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(0), Optional.of(0), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(0, responseComponents.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetAllComponentsPageEmpty() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    responseComponentController.addComponent(COMPONENT_E);
    ResponseEntity<?> responseEntity = responseComponentController.getAllComponents(
        Optional.of(3), Optional.of(1), Optional.empty(), Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<ResponseComponent> responseComponents = (List<ResponseComponent>) responseEntity.getBody();
    assertEquals(0, responseComponents.size());
  }

  @Test
  public void testGetComponentCountNoKeywords() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    ResponseEntity<?> responseEntity =
        responseComponentController.getComponentCount(Optional.empty(), Optional.empty());
    assertNotNull(responseEntity.getBody());
    var componentCount = (String) responseEntity.getBody();
    assertEquals("2", componentCount);
  }

  @Test
  public void testGetComponentCountKeywords() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    String[] keywords = {"Guten", "Morgen"};
    ResponseEntity<?> responseEntity =
        responseComponentController.getComponentCount(Optional.of(keywords), Optional.empty());
    assertNotNull(responseEntity.getBody());
    var componentCount = (String) responseEntity.getBody();
    assertEquals("1", componentCount);
  }

  @Test
  public void testGetComponentByID() {
    responseComponentController.addComponent(COMPONENT_A);
    ResponseEntity<?> responseEntity = responseComponentController.getComponentByID(1);
    assertNotNull(responseEntity.getBody());
    ResponseComponent responseComponent = (ResponseComponent) responseEntity.getBody();
    assertEquals(responseComponent, COMPONENT_A);
  }

  @Test
  public void testGetComponentByIDInvalid() {
    responseComponentController.addComponent(COMPONENT_A);
    ResponseEntity<?> responseEntity = responseComponentController.getComponentByID(2);
    assertNotNull(responseEntity.getBody());
    assertEquals(NotFoundException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testDeleteComponent() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.deleteComponent(1);
    assertFalse(componentRepository.findAll().iterator().hasNext());
  }

  @Test
  public void testDeleteComponentInvalid() {
    responseComponentController.addComponent(COMPONENT_A);
    ResponseEntity<?> responseEntity = responseComponentController.deleteComponent(2);
    assertNotNull(responseEntity.getBody());
    assertEquals(NotFoundException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testDeleteAllComponents() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    // responseComponentController.deleteAllComponents();
    // assertFalse(componentRepository.findAll().iterator().hasNext());
  }

  @Test
  public void testUpdateComponent() {
    responseComponentController.addComponent(COMPONENT_C);
    responseComponentController.updateComponent(3, COMPONENT_D);
    ResponseComponent responseComponent = componentRepository.findById(3L).orElseThrow();
    assertEquals(responseComponent, COMPONENT_D);
  }
}
