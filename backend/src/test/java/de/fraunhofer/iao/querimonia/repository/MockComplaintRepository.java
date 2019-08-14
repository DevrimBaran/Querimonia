package de.fraunhofer.iao.querimonia.repository;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;

import java.util.List;

public class MockComplaintRepository extends MockRepository<Complaint>
    implements ComplaintRepository {

  @Override
  public List<Complaint> findAllByState(ComplaintState state) {
    throw new RuntimeException("not implemented");
  }
}
