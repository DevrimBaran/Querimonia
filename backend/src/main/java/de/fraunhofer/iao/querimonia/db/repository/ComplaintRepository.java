package de.fraunhofer.iao.querimonia.db.repository;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Interface to the database for storing db.
 */
public interface ComplaintRepository extends PagingAndSortingRepository<Complaint, Long> {
}
