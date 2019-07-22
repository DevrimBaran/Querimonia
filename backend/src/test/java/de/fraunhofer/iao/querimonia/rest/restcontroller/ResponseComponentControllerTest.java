package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.MockComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.MockComponentRepository;
import de.fraunhofer.iao.querimonia.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import de.fraunhofer.iao.querimonia.rest.manager.ResponseComponentManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static de.fraunhofer.iao.querimonia.response.component.TestComponents.*;
import static org.junit.Assert.*;

/**
 * Unit test class for ResponseComponentController
 *
 * @author Simon Weiler
 */
public class ResponseComponentControllerTest {

    private MockComponentRepository componentRepository;
    private ResponseComponentController responseComponentController;

  private boolean equalsComponent(ResponseComponent component1, ResponseComponent component2) {
    return component1.getComponentName().equals(component2.getComponentName())
            && component1.getActions().equals(component2.getActions())
            && component1.getComponentTexts().equals(component2.getComponentTexts())
            && component1.getRulesXml().equals(component2.getRulesXml())
            && (component1.getPriority() == component2.getPriority());
  }

  @Before
  public void setup() {
    componentRepository = new MockComponentRepository();
    ComplaintRepository complaintRepository = new MockComplaintRepository();
    ResponseComponentManager responseComponentManager = new ResponseComponentManager(componentRepository, complaintRepository);
    responseComponentController = new ResponseComponentController(responseComponentManager);
  }

  @Test
  public void testAddComponent() {
    responseComponentController.addComponent(COMPONENT_STANDARD);
    ResponseComponent component = componentRepository.findAll().iterator().next();
    assertTrue(equalsComponent(component, COMPONENT_STANDARD));
  }

  @Test
  public void testAddDefaultComponents() {
    ResponseEntity<?> responseEntity = responseComponentController.addDefaultComponents();
    assertNotNull(responseEntity.getBody());
    assertNotEquals(QuerimoniaException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testGetAllComponents() {
    //TODO: Write tests
  }

  @Test
  public void testGetComponentCountNoKeywords() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    ResponseEntity<?> responseEntity = responseComponentController.getComponentCount(Optional.empty());
    assertNotNull(responseEntity.getBody());
    int componentCount = (Integer) responseEntity.getBody();
    assertEquals(2, componentCount);
  }

  @Test
  public void testGetComponentCountKeywords() {
    responseComponentController.addComponent(COMPONENT_A);
    responseComponentController.addComponent(COMPONENT_B);
    String[] keywords = {"Guten", "Morgen"};
    ResponseEntity<?> responseEntity = responseComponentController.getComponentCount(Optional.of(keywords));
    assertNotNull(responseEntity.getBody());
    int componentCount = (Integer) responseEntity.getBody();
    assertEquals(1, componentCount);
  }

  @Test
  public void testGetComponentByID() {
    responseComponentController.addComponent(COMPONENT_A);
    ResponseEntity<?> responseEntity = responseComponentController.getComponentByID(1);
    assertNotNull(responseEntity.getBody());
    ResponseComponent responseComponent = (ResponseComponent) responseEntity.getBody();
    assertTrue(equalsComponent(responseComponent, COMPONENT_A));
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
    responseComponentController.deleteAllComponents();
    assertFalse(componentRepository.findAll().iterator().hasNext());
  }

  @Test
  public void testUpdateComponent() {
    responseComponentController.addComponent(COMPONENT_C);
    responseComponentController.updateComponent(1, COMPONENT_D);
    ResponseComponent responseComponent = componentRepository.findAll().iterator().next();
    assertTrue(equalsComponent(responseComponent, COMPONENT_D));
  }
}
