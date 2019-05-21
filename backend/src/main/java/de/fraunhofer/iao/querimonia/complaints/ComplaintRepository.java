package de.fraunhofer.iao.querimonia.complaints;

import org.springframework.data.repository.CrudRepository;

/**
 * Interface to the database for storing complaints
 */
public interface ComplaintRepository extends CrudRepository<Complaint, Integer> {
}
