package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.response.action.Action;
import org.springframework.data.repository.CrudRepository;

/**
 * Interface to access the action table.
 */
public interface ActionRepository extends CrudRepository<Action, Long> {
}
