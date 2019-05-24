package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.db.ResponseTemplate;
import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<ResponseTemplate, Integer> {
}
