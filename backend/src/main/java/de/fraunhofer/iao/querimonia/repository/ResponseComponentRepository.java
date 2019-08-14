package de.fraunhofer.iao.querimonia.repository;

import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for response components.
 */
public interface ResponseComponentRepository extends
    PagingAndSortingRepository<ResponseComponent, Long> {
}
