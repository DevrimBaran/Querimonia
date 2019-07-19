package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ResponseComponentRepository extends
    PagingAndSortingRepository<ResponseComponent, Long> {
}
