package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.config.Configuration;
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

  /**
   * Creates new configuration manager.
   *
   * @param analyzerConfigProperties the properties object for getting the current configuration.
   * @param configurationRepository  the repository for properties.
   */
  @Autowired
  public ConfigurationManager(
      AnalyzerConfigProperties analyzerConfigProperties,
      ConfigurationRepository configurationRepository) {
    this.analyzerConfigProperties = analyzerConfigProperties;
    this.configurationRepository = configurationRepository;
  }

  public synchronized List<Configuration> getConfigurations(Optional<Integer> count,
                                                            Optional<Integer> page,
                                                            Optional<String[]> sortBy) {

    Stream<Configuration> allConfigs =
        StreamSupport.stream(configurationRepository.findAll().spliterator(), false);

    if (count.isPresent()) {
      if (page.isPresent()) {
        allConfigs = allConfigs.skip(page.get() * count.get());
      }
      allConfigs = allConfigs.limit(count.get());
    }

    return allConfigs.sorted(getConfigComparator(sortBy)).collect(Collectors.toList());
  }

  public synchronized Configuration addConfiguration(Configuration configuration) {
    return configurationRepository.save(configuration);
  }

  public synchronized Configuration getConfiguration(long configId) {
    return configurationRepository
        .findById(configId)
        .orElseThrow(() -> new NotFoundException(configId));
  }

  public synchronized void deleteConfiguration(long configId) {
    if (configurationRepository.existsById(configId)) {
      configurationRepository.deleteById(configId);
      // check if current configuration gets removed
      if (analyzerConfigProperties.getId() == configId) {
        analyzerConfigProperties.setId(0);
      }
    } else {
      throw new NotFoundException(configId);
    }
  }

  public synchronized Configuration updateConfiguration(long configId,
                                                        Configuration configuration) {
    if (configurationRepository.existsById(configId)) {
      configuration.setConfigId(configId);
      return configurationRepository.save(configuration);
    }
    throw new NotFoundException(configId);
  }

  public synchronized String countConfigurations() {
    return Long.toString(configurationRepository.count());
  }

  public synchronized Configuration getCurrentConfiguration() {
    return configurationRepository
        .findById(analyzerConfigProperties.getId())
        .orElse(Configuration.FALLBACK_CONFIGURATION);
  }

  public synchronized Configuration updateCurrentConfiguration(long configId) {
    if (configurationRepository.existsById(configId)) {
      analyzerConfigProperties.setId(configId);
    }
    return getConfiguration(configId);
  }

  public synchronized void deleteAllConfigurations() {
    configurationRepository.deleteAll();
    analyzerConfigProperties.setId(0);
  }

  synchronized void storeConfiguration(Configuration configuration) {
    configurationRepository.save(configuration);
  }

  /**
   * Returns a comparator for the configurations.
   */
  private Comparator<Configuration> getConfigComparator(Optional<String[]> sortBy) {
    return new ComparatorBuilder<Configuration>()
        .append("id", Configuration::getConfigId)
        .append("name", Configuration::getName)
        .build(sortBy.orElse(new String[] {"id_asc"}));
  }
}
