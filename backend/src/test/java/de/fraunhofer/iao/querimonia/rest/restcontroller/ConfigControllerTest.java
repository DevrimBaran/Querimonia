package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.manager.ConfigurationManager;
import de.fraunhofer.iao.querimonia.repository.MockComplaintRepository;
import de.fraunhofer.iao.querimonia.repository.MockConfigurationRepository;
import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import de.fraunhofer.iao.querimonia.utility.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.COMPLAINT_F;
import static de.fraunhofer.iao.querimonia.config.TestConfigurations.*;
import static org.junit.Assert.*;

/**
 * Unit test class for ConfigController
 *
 * @author Simon Weiler
 */
public class ConfigControllerTest {

  private MockConfigurationRepository mockConfigurationRepository;
  private MockComplaintRepository mockComplaintRepository;
  private ConfigController configController;

  @Before
  public void setUp() {
    FileStorageProperties fileStorageProperties = new FileStorageProperties();
    fileStorageProperties.setUploadDir("src/test/resources/uploads/");
    var fileStorageService = new FileStorageService(fileStorageProperties);
    mockConfigurationRepository = new MockConfigurationRepository();
    mockComplaintRepository = new MockComplaintRepository();
    ConfigurationManager configurationManager =
        new ConfigurationManager(mockConfigurationRepository, mockComplaintRepository, fileStorageService);
    configController = new ConfigController(configurationManager);
  }

  @Test
  public void testGetConfigurationsAll() {
    configController.addConfiguration(CONFIGURATION_F);
    configController.addConfiguration(CONFIGURATION_E);
    configController.addConfiguration(CONFIGURATION_D);
    ResponseEntity<?> responseEntity = configController.getConfigurations(Optional.of(3), Optional.of(0), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Configuration> configurationList = List.of(CONFIGURATION_D, CONFIGURATION_E, CONFIGURATION_F);
    assertEquals(configurationList, responseEntity.getBody());
  }

  @Test
  public void testGetConfigurationsNone() {
    configController.addConfiguration(CONFIGURATION_F);
    configController.addConfiguration(CONFIGURATION_E);
    configController.addConfiguration(CONFIGURATION_D);
    ResponseEntity<?> responseEntity = configController.getConfigurations(Optional.of(3), Optional.of(1), Optional.empty());
    assertNotNull(responseEntity.getBody());
    assertEquals(Collections.emptyList(), responseEntity.getBody());
  }

  @Test
  public void testGetConfigurationsLimitedCount() {
    configController.addConfiguration(CONFIGURATION_F);
    configController.addConfiguration(CONFIGURATION_E);
    configController.addConfiguration(CONFIGURATION_D);
    ResponseEntity<?> responseEntity = configController.getConfigurations(Optional.of(2), Optional.of(0), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Configuration> configurationList = List.of(CONFIGURATION_D, CONFIGURATION_E);
    assertEquals(configurationList, responseEntity.getBody());
  }

  @Test
  public void testGetConfigurationsMultiplePages() {
    configController.addConfiguration(CONFIGURATION_F);
    configController.addConfiguration(CONFIGURATION_E);
    configController.addConfiguration(CONFIGURATION_D);
    ResponseEntity<?> responseEntity = configController.getConfigurations(Optional.of(2), Optional.of(1), Optional.empty());
    assertNotNull(responseEntity.getBody());
    List<Configuration> configurationList = List.of(CONFIGURATION_F);
    assertEquals(configurationList, responseEntity.getBody());
  }

  @Test
  public void testGetConfigurationsSorted() {
    configController.addConfiguration(CONFIGURATION_F);
    configController.addConfiguration(CONFIGURATION_E);
    configController.addConfiguration(CONFIGURATION_D);
    String[] sortBy = {"name_asc"};
    ResponseEntity<?> responseEntity = configController.getConfigurations(Optional.of(3), Optional.of(0), Optional.of(sortBy));
    assertNotNull(responseEntity.getBody());
    List<Configuration> configurationList = List.of(CONFIGURATION_D, CONFIGURATION_E, CONFIGURATION_F);
    assertEquals(configurationList, responseEntity.getBody());
  }

  @Test
  public void testAddConfiguration() {
    configController.addConfiguration(CONFIGURATION_D);
    Configuration configuration = mockConfigurationRepository.findAll().iterator().next();
    assertEquals(CONFIGURATION_D, configuration);
  }

  @Test
  public void testGetConfiguration() {
    configController.addConfiguration(CONFIGURATION_D);
    ResponseEntity<?> responseEntity = configController.getConfiguration(4);
    assertNotNull(responseEntity.getBody());
    assertEquals(CONFIGURATION_D, responseEntity.getBody());
  }

  @Test
  public void testGetConfigurationInvalidID() {
    ResponseEntity<?> responseEntity = configController.getConfiguration(4);
    assertNotNull(responseEntity.getBody());
    assertEquals(NotFoundException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testDeleteConfiguration() {
    configController.addConfiguration(CONFIGURATION_D);
    configController.addConfiguration(CONFIGURATION_E);
    configController.deleteConfiguration(4);
    Iterator<Configuration> iterator = mockConfigurationRepository.findAll().iterator();
    assertEquals(CONFIGURATION_E, iterator.next());
    assertFalse(iterator.hasNext());
  }

  @Test
  public void testDeleteConfigurationUpdateComplaint() {
    configController.addConfiguration(CONFIGURATION_D);
    mockComplaintRepository.save(COMPLAINT_F);
    configController.deleteConfiguration(4);
    Optional<Complaint> complaint = mockComplaintRepository.findById(6L);
    assertTrue(complaint.isPresent());
    assertNull(complaint.get().getConfiguration());
  }

  @Test
  public void testDeleteConfigurationFallback() {
    configController.addConfiguration(CONFIGURATION_D);
    configController.deleteConfiguration(4);
    assertFalse(mockComplaintRepository.findAll().iterator().hasNext());
  }

  @Test
  public void testDeleteConfigurationInvalidID() {
    ResponseEntity<?> responseEntity = configController.deleteConfiguration(4);
    assertNotNull(responseEntity.getBody());
    assertEquals(NotFoundException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testUpdateConfiguration() {
    configController.addConfiguration(CONFIGURATION_D);
    ResponseEntity<?> responseEntity = configController.updateConfiguration(4, CONFIGURATION_E);
    assertNotNull(responseEntity.getBody());
    assertEquals(CONFIGURATION_E, responseEntity.getBody());
  }


  @Test
  public void testUpdateConfigurationInvalidID() {
    ResponseEntity<?> responseEntity = configController.updateConfiguration(4, CONFIGURATION_E);
    assertNotNull(responseEntity.getBody());
    assertEquals(NotFoundException.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testCountConfigurations() {
    configController.addConfiguration(CONFIGURATION_D);
    configController.addConfiguration(CONFIGURATION_E);
    ResponseEntity<?> responseEntity = configController.countConfigurations();
    assertNotNull(responseEntity.getBody());
    assertEquals("2", configController.countConfigurations().getBody());
  }

  @Test
  public void testGetCurrentConfiguration() {
    configController.addConfiguration(CONFIGURATION_D);
    configController.addConfiguration(CONFIGURATION_E);
    ResponseEntity<?> responseEntity = configController.getCurrentConfiguration();
    assertNotNull(responseEntity.getBody());
    assertEquals(CONFIGURATION_E, responseEntity.getBody());
  }

  @Test
  public void testGetCurrentConfigurationFallback() {
    configController.addConfiguration(CONFIGURATION_D);
    ResponseEntity<?> responseEntity = configController.getCurrentConfiguration();
    assertNotNull(responseEntity.getBody());
    assertEquals(Configuration.FALLBACK_CONFIGURATION, responseEntity.getBody());
    List<Configuration> configurations = mockConfigurationRepository.findAllByActive(true);
    assertEquals(1, configurations.size());
    assertEquals(Configuration.FALLBACK_CONFIGURATION, configurations.get(0));
  }

  @Test
  public void testGetCurrentConfigurationMultipleActive() {
    configController.addConfiguration(CONFIGURATION_E);
    configController.addConfiguration(CONFIGURATION_F);
    ResponseEntity<?> responseEntity = configController.getCurrentConfiguration();
    assertNotNull(responseEntity.getBody());
    assertEquals(CONFIGURATION_E, responseEntity.getBody());
    Optional<Configuration> configuration = mockConfigurationRepository.findById(6L);
    assertTrue(configuration.isPresent());
    assertFalse(configuration.get().isActive());
  }

  @Test
  public void testUpdateCurrentConfiguration() {
    configController.addConfiguration(CONFIGURATION_D);
    configController.addConfiguration(CONFIGURATION_E);
    configController.updateCurrentConfiguration(4);
    Iterator<Configuration> iterator = mockConfigurationRepository.findAll().iterator();
    assertTrue(iterator.next().isActive());
    assertFalse(iterator.next().isActive());
  }

  @Test
  public void testAddDefaultConfigurations() {
    // Only throws an exception because mock repositories cannot contain the ID 0
    configController.addDefaultConfigurations();
    assertTrue(mockConfigurationRepository.count() >= 1);
  }

  @Test
  public void testDeleteAllConfigurations() {
    configController.addConfiguration(CONFIGURATION_D);
    configController.addConfiguration(CONFIGURATION_E);
    mockComplaintRepository.save(COMPLAINT_F);
    configController.deleteAllConfigurations();
    Optional<Complaint> complaint = mockComplaintRepository.findById(6L);
    assertTrue(complaint.isPresent());
    assertNull(complaint.get().getConfiguration());
    assertEquals(0, mockConfigurationRepository.count());
  }

  @Test
  public void testGetAllExtractors() {
    //TODO: Mock Kikuko extractors
  }
}