package de.fraunhofer.iao.querimonia.repository;

import de.fraunhofer.iao.querimonia.config.Configuration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MockConfigurationRepository extends MockRepository<Configuration>
    implements ConfigurationRepository {

  @Override
  public List<Configuration> findAllByActive(boolean active) {
    return StreamSupport.stream(super.findAll().spliterator(), false)
        .filter(Configuration::isActive)
        .collect(Collectors.toList());
  }
}
