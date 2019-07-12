package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.response.generation.SingleCompletedComponent;
import org.springframework.data.repository.CrudRepository;

public interface SingleCompletedComponentRepository
    extends CrudRepository<SingleCompletedComponent, Integer> {
}
