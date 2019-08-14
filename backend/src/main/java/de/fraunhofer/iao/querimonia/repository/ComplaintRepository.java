package de.fraunhofer.iao.querimonia.repository;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Interface to the database.
 */
public interface ComplaintRepository extends PagingAndSortingRepository<Complaint, Long> {

  /**
   * Finds all complaints with the given state.
   *
   * @param state the state of the complaints.
   *
   * @return a list of all complaints that are in the given state.
   */
  List<Complaint> findAllByState(ComplaintState state);

}
