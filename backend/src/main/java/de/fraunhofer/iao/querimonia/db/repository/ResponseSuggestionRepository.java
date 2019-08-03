package de.fraunhofer.iao.querimonia.db.repository;

import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import org.springframework.data.repository.CrudRepository;

public interface ResponseSuggestionRepository extends CrudRepository<ResponseSuggestion, Long> {
}
