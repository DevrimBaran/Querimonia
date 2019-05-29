package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.response.ResponseComponent;
import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<ResponseComponent, Integer> {
}
