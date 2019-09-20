package de.fraunhofer.iao.querimonia.repository;

import de.fraunhofer.iao.querimonia.complaint.Combination;

public class MockCombinationRepository extends MockRepository<Combination> implements
    CombinationRepository {
  @Override
  public boolean existsByLineAndPlaceAndStop(String line, String place, String stop) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public boolean existsByLineAndStop(String line, String stop) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public boolean existsByLineAndPlace(String line, String place) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public boolean existsByPlaceAndStop(String place, String stop) {
    throw new RuntimeException("not implemented");
  }
}
