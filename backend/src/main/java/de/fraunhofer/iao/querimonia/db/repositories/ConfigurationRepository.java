package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.config.Configuration;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConfigurationRepository extends PagingAndSortingRepository<Configuration, Long> {
}
