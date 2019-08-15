package de.fraunhofer.iao.querimonia.manager;

import de.fraunhofer.iao.querimonia.complaint.LineStopCombination;
import de.fraunhofer.iao.querimonia.repository.CombinationRepository;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(CombinationManager.class);

  private final CombinationRepository lineStopCombinationRepository;
  private final FileStorageService fileStorageService;

  @Autowired
  public CombinationManager(
      CombinationRepository combinationRepository,
      FileStorageService fileStorageService) {
    this.lineStopCombinationRepository = combinationRepository;
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
    List<LineStopCombination> combinations = addLineStopCombinations(fileStorageService
        .getJsonObjectsFromFile(LineStopCombination[].class, "DefaultCombinations.json"));
    logger.info("Added default combinations.");
    return combinations;
  }

  /**
   * Deletes all combinations.
   */
  public void deleteAllCombinations() {
    lineStopCombinationRepository.deleteAll();
    logger.info("Deleted all combinations.");
  }
}
