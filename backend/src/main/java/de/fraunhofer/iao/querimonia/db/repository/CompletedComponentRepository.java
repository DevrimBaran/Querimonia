package de.fraunhofer.iao.querimonia.db.repository;

import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.springframework.data.repository.CrudRepository;

public interface CompletedComponentRepository
    extends CrudRepository<CompletedResponseComponent, Long> {
}
