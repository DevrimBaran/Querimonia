package de.fraunhofer.iao.querimonia.db.repositories;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import org.springframework.data.repository.CrudRepository;

/**
 * Interface to the database for storing db.
 */
public interface ComplaintRepository extends CrudRepository<Complaint, Long> {
}
