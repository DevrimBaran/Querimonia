package de.fraunhofer.iao.querimonia.db.manager;

import de.fraunhofer.iao.querimonia.db.combination.LineStopCombination;
import de.fraunhofer.iao.querimonia.db.repository.LineStopCombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This manager is used to manage combinations of lines, places and stops.
 */
@Service
public class LineStopCombinationManager {

  private LineStopCombinationRepository lineStopCombinationRepository;

  @Autowired
  public LineStopCombinationManager(
      LineStopCombinationRepository lineStopCombinationRepository) {
    this.lineStopCombinationRepository = lineStopCombinationRepository;
  }

  /**
   * Returns all combinations of lines, stops and places of the database.
   *
   * @return all combinations of lines, stops and places of the database.
   */
  public List<LineStopCombination> getAllCombinations() {
    return StreamSupport.stream(lineStopCombinationRepository.findAll().spliterator(), false)
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * Adds combinations to the database.
   */
  public void addLineStopCombinations(
      List<LineStopCombination> lineStopCombinations) {
    lineStopCombinationRepository.saveAll(lineStopCombinations);
  }
}
