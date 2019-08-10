package de.fraunhofer.iao.querimonia.db.manager;

import de.fraunhofer.iao.querimonia.complaint.LineStopCombination;
import de.fraunhofer.iao.querimonia.db.repository.LineStopCombinationRepository;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This manager is used to manage combinations of lines, places and stops.
 */
@Service
public class CombinationManager {

  private final LineStopCombinationRepository lineStopCombinationRepository;
  private final FileStorageService fileStorageService;

  @Autowired
  public CombinationManager(
      LineStopCombinationRepository lineStopCombinationRepository,
      FileStorageService fileStorageService) {
    this.lineStopCombinationRepository = lineStopCombinationRepository;
    this.fileStorageService = fileStorageService;
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
   *
   * @return the added combinations.
   */
  public List<LineStopCombination> addLineStopCombinations(
      List<LineStopCombination> lineStopCombinations) {
    return StreamSupport.stream(lineStopCombinationRepository
        .saveAll(lineStopCombinations).spliterator(), false)
        .collect(Collectors.toList());
  }

  /**
   * Adds example combinations to the database.
   *
   * @return the example combinations.
   */
  public List<LineStopCombination> addDefaultCombinations() {
    return addLineStopCombinations(fileStorageService
        .getJsonObjectsFromFile(LineStopCombination[].class, "DefaultCombinations.json"));
  }

  /**
   * Deletes all combinations.
   */
  public void deleteAllCombinations() {
    lineStopCombinationRepository.deleteAll();
  }
}
