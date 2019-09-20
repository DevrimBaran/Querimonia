package de.fraunhofer.iao.querimonia.repository;

import de.fraunhofer.iao.querimonia.complaint.Combination;
import org.springframework.data.repository.CrudRepository;


/**
 * This repository manages {@link Combination combinations} of lines, stops and places.
 */
public interface CombinationRepository extends CrudRepository<Combination, Long> {

  /**
   * Checks if the given combination exists.
   *
   * @param line  the line number.
   * @param place the name of the place.
   * @param stop  the name of the stop.
   *
   * @return true, if the given combination exists, else false.
   */
  boolean existsByLineAndPlaceAndStop(String line, String place, String stop);

  /**
   * Checks if the given combination exists.
   *
   * @param line  the line number.
   * @param stop  the name of the stop.
   *
   * @return true, if the given combination exists, else false.
   */
  boolean existsByLineAndStop(String line, String stop);

  /**
   * Checks if the given combination exists.
   *
   * @param line  the line number.
   * @param place the name of the place.
   *
   * @return true, if the given combination exists, else false.
   */
  boolean existsByLineAndPlace(String line, String place);

  /**
   * Checks if the given combination exists.
   *
   * @param place the name of the place.
   * @param stop  the name of the stop.
   *
   * @return true, if the given combination exists, else false.
   */
  boolean existsByPlaceAndStop(String place, String stop);
}
