package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.nlp.response.CompletedResponseComponent;
import org.springframework.data.repository.CrudRepository;

public interface CompletedResponseComponentRepository
    extends CrudRepository<CompletedResponseComponent, Integer> {
}
