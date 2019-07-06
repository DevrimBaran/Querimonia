package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import org.springframework.data.repository.CrudRepository;

public interface CompletedResponseComponentRepository
    extends CrudRepository<CompletedResponseComponent, Integer> {
}