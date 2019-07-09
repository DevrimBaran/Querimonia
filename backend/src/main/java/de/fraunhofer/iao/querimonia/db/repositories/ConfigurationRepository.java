package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.config.Configuration;
import org.springframework.data.repository.CrudRepository;

public interface ConfigurationRepository extends CrudRepository<Configuration, Integer> {
}
