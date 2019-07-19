package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.config.Configuration;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ConfigurationRepository extends PagingAndSortingRepository<Configuration, Long> {

  List<Configuration> findAllByActive(boolean active);
}
