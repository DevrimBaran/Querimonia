package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.db.ResponseSuggestion;
import org.springframework.data.repository.CrudRepository;

public interface ResponseRepository extends CrudRepository<ResponseSuggestion, Integer> {
}
