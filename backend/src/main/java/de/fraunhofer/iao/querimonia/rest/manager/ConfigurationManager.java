package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ConfigurationRepository;
import de.fraunhofer.iao.querimonia.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.property.AnalyzerConfigProperties;
import de.fraunhofer.iao.querimonia.rest.manager.filter.ComparatorBuilder;
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

  private final AnalyzerConfigProperties analyzerConfigProperties;
  private final ConfigurationRepository configurationRepository;
  private final ComplaintRepository complaintRepository;

  /**
   * Creates new configuration manager.
   *
   * @param analyzerConfigProperties the properties object for getting the current configuration.
   * @param configurationRepository  the repository for properties.
   * @param complaintRepository      the repository for complaints.
   */
  @Autowired
  public ConfigurationManager(
      AnalyzerConfigProperties analyzerConfigProperties,
      ConfigurationRepository configurationRepository,
      ComplaintRepository complaintRepository) {
    this.analyzerConfigProperties = analyzerConfigProperties;
    this.configurationRepository = configurationRepository;
    this.complaintRepository = complaintRepository;
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
        if (complaint.getConfiguration().getId() == configId) {
          complaint = complaint.withConfiguration(Configuration.FALLBACK_CONFIGURATION);
        }
        complaintRepository.save(complaint);
      }

      // dont delete fallback configuration
      if (configId != Configuration.FALLBACK_CONFIGURATION.getId()) {
        configurationRepository.deleteById(configId);
      }
      // check if current configuration gets removed
      if (analyzerConfigProperties.getId() == configId) {
        analyzerConfigProperties.setId(Configuration.FALLBACK_CONFIGURATION.getId());
      }
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
    return configurationRepository
        .findById(analyzerConfigProperties.getId())
        .orElse(Configuration.FALLBACK_CONFIGURATION);
  }

  /**
   * Sets the configuration with the given id as active.
   *
   * @param configId the id of the configuration that should be activated.
   *
   * @return the now active configuration.
   */
  public synchronized Configuration updateCurrentConfiguration(long configId) {
    if (configurationRepository.existsById(configId)) {
      analyzerConfigProperties.setId(configId);
    }
    return getConfiguration(configId);
  }

  /**
   * Deletes all configurations of the database.
   */
  public synchronized void deleteAllConfigurations() {
    for (Complaint complaint : complaintRepository.findAll()) {
      complaint = complaint.withConfiguration(Configuration.FALLBACK_CONFIGURATION);
      complaintRepository.save(complaint);
    }
    for (Configuration configuration : configurationRepository.findAll()) {
      if (!configuration.getId().equals(Configuration.FALLBACK_CONFIGURATION.getId())) {
        configurationRepository.deleteById(configuration.getId());
      }
    }
    analyzerConfigProperties.setId(Configuration.FALLBACK_CONFIGURATION.getId());
  }

  /**
   * Stores the configuration in the database.
   *
   * @param configuration the configuration that should be stored.
   */
  synchronized void storeConfiguration(Configuration configuration) {
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
