package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.response.ResponseSuggestion;
import org.springframework.data.repository.CrudRepository;

public interface ResponseRepository extends CrudRepository<ResponseSuggestion, Integer> {
}
