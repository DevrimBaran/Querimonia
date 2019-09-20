package de.fraunhofer.iao.querimonia.manager;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.manager.filter.ComparatorBuilder;
import de.fraunhofer.iao.querimonia.repository.ComplaintRepository;
import de.fraunhofer.iao.querimonia.repository.ConfigurationRepository;
import de.fraunhofer.iao.querimonia.rest.contact.KiKuKoContactExtractors;
import de.fraunhofer.iao.querimonia.rest.restobjects.AvailableExtractors;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import de.fraunhofer.iao.querimonia.utility.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class is used to manage the configurations in the database.
 */
@Service
public class ConfigurationManager {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

  private final ConfigurationRepository configurationRepository;
  private final ComplaintRepository complaintRepository;
  private final FileStorageService fileStorageService;

  /**
   * Creates new configuration manager.
   *
   * @param configurationRepository the repository for properties.
   * @param complaintRepository     the repository for complaints.
   */
  @Autowired
  public ConfigurationManager(
      ConfigurationRepository configurationRepository,
      ComplaintRepository complaintRepository,
      FileStorageService fileStorageService) {

    this.configurationRepository = configurationRepository;
    this.complaintRepository = complaintRepository;
    this.fileStorageService = fileStorageService;
  }

  /**
   * Returns all configuration of the database. Sorting and pagination can be used.
   *
   * @param count  the amount of configurations per page.
   * @param page   the page number.
   * @param sortBy sorting parameters.
   *
   * @return the list of configurations of the database, respecting the pagination and sorting
   *     parameters.
   */
  public synchronized List<Configuration> getConfigurations(Optional<Integer> count,
                                                            Optional<Integer> page,
                                                            Optional<String[]> sortBy) {
    // create stream of all configurations of the database
    Stream<Configuration> allConfigs =
        StreamSupport.stream(configurationRepository.findAll().spliterator(), false);

    if (count.isPresent()) {
      if (page.isPresent()) {
        allConfigs = allConfigs.skip(page.get() * count.get());
      }
      allConfigs = allConfigs.limit(count.get());
    }

    return allConfigs
        .sorted(getConfigComparator(sortBy))
        .collect(Collectors.toList());
  }

  /**
   * Adds a new configuration to the database.
   *
   * @param configuration the configuration that will be added.
   *
   * @return the new added configuration.
   */
  public synchronized Configuration addConfiguration(Configuration configuration) {
    return configurationRepository.save(configuration);
  }

  /**
   * Returns the configuration with the given id.
   *
   * @param configId the id of the configuration.
   *
   * @return the configuration with the given id.
   */
  public synchronized Configuration getConfiguration(long configId) {
    return configurationRepository
        .findById(configId)
        .orElseThrow(() -> new NotFoundException(configId));
  }

  /**
   * Deletes the configuration with the given id.
   *
   * @param configId the id of the configuration that should be deleted.
   */
  public synchronized void deleteConfiguration(long configId) {
    if (configurationRepository.existsById(configId)) {
      // remove reference in all complaints
      for (Complaint complaint : complaintRepository.findAll()) {
        if (complaint.getConfiguration() != null
            && complaint.getConfiguration().getId() == configId) {
          complaint = complaint.withConfiguration(null);
        }
        complaintRepository.save(complaint);
      }

      configurationRepository.deleteById(configId);
      // check if current configuration gets removed
    } else {
      throw new NotFoundException(configId);
    }
  }

  /**
   * The configuration with the given id gets replaced with the given configuration.
   *
   * @param configId      the id of the configuration that should be replaced. Must exists.
   * @param configuration the configuration that should be used.
   *
   * @return the new configuration.
   */
  public synchronized Configuration updateConfiguration(long configId,
                                                        Configuration configuration) {
    if (configurationRepository.existsById(configId)) {
      configuration = configuration.withConfigId(configId);
      return configurationRepository.save(configuration);
    }
    throw new NotFoundException(configId);
  }

  /**
   * Returns the amount of configurations that are stored in the database.
   *
   * @return the amount of configurations.
   */
  public synchronized Long countConfigurations() {
    return configurationRepository.count();
  }

  /**
   * Returns the configuration that is currently active.
   *
   * @return the configuration that is currently active.
   */
  public synchronized Configuration getCurrentConfiguration() {
    var activeConfigs = configurationRepository.findAllByActive(true);
    Configuration currentConfig;
    if (activeConfigs.isEmpty()) {
      // fall back if no config is active
      currentConfig = Configuration.FALLBACK_CONFIGURATION.withActive(true);
      storeConfiguration(currentConfig);
    } else {
      // fix if multiple active configs are in the db
      currentConfig = activeConfigs.remove(0);
      activeConfigs.forEach(configuration -> storeConfiguration(configuration.withActive(false)));
    }
    return currentConfig;
  }

  /**
   * Sets the configuration with the given id as active.
   *
   * @param configId the id of the configuration that should be activated.
   *
   * @return the now active configuration.
   */
  public synchronized Configuration updateCurrentConfiguration(long configId) {
    var currentConfig = getConfiguration(configId).withActive(true);
    var activeConfigs = configurationRepository.findAllByActive(true);
    activeConfigs.stream()
        .map(configuration -> configuration.withActive(false))
        .forEach(this::storeConfiguration);
    storeConfiguration(currentConfig);
    return currentConfig;
  }

  /**
   * Adds example configurations to the database. The first configuration will be set as active.
   *
   * @return the example configurations.
   */
  public synchronized List<Configuration> addDefaultConfigurations() {
    var configurations = fileStorageService.getJsonObjectsFromFile(Configuration[].class,
        "DefaultConfigurations.json");
    configurations
        .forEach(this::addConfiguration);
    configurations.stream()
        .findFirst()
        .map(Configuration::getId)
        .map(this::updateCurrentConfiguration);
    logger.info("Added default configurations.");
    return configurations;
  }

  /**
   * Deletes all configurations of the database.
   */
  public synchronized void deleteAllConfigurations() {
    for (Complaint complaint : complaintRepository.findAll()) {
      complaint =
          complaint.withConfiguration(null);
      complaintRepository.save(complaint);
    }
    configurationRepository.deleteAll();
    logger.info("Deleted all configurations");
  }

  /**
   * Returns all Extractors of KiKuKo.
   */
  public synchronized AvailableExtractors getAllExtractors() {
    KiKuKoContactExtractors contact = new KiKuKoContactExtractors();
    return new AvailableExtractors(
        contact.executeKikukoRequest("tools"),
        contact.executeKikukoRequest("pipelines"),
        contact.executeKikukoRequest("domains"));
  }

  /**
   * Stores the configuration in the database.
   *
   * @param configuration the configuration that should be stored.
   */
  private synchronized void storeConfiguration(Configuration configuration) {
    configurationRepository.save(configuration);
  }

  /**
   * Returns a comparator for the configurations.
   */
  private Comparator<Configuration> getConfigComparator(Optional<String[]> sortBy) {
    return new ComparatorBuilder<Configuration>()
        .append("id", Configuration::getId)
        .append("name", Configuration::getName)
        .build(sortBy.orElse(new String[] {"id_asc"}));
  }
}
