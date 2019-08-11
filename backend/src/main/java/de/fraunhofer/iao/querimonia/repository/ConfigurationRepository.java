package de.fraunhofer.iao.querimonia.repository;

import de.fraunhofer.iao.querimonia.config.Configuration;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository for configurations.
 */
@SuppressWarnings("unused")
public interface ConfigurationRepository extends PagingAndSortingRepository<Configuration, Long> {

  /**
   * Returns all configurations that are either active or not active.
   *
   * @param active the state that the returned configurations should have.
   *
   * @return all configurations with the given active state.
   */
  List<Configuration> findAllByActive(boolean active);
}
