package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.rest.manager.filter.ComplaintFilter;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * This rest controller is used to calculate various statistics.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StatsController {

  private final ComplaintRepository complaintRepository;

  public StatsController(ComplaintRepository complaintRepository) {
    this.complaintRepository = complaintRepository;
  }

  /**
   * Combines two maps that contains some kind of values mapped to their count. In the result the
   * count of equal values will be added.
   */
  private static Map<String, Integer> combineCountMaps(Map<String, Integer> map1,
                                                       Map<String, Integer> map2) {
    map2.forEach((value, count) -> {
      int countOfMap1 = map1.getOrDefault(value, 0);
      // add stored counts together
      map1.put(value, countOfMap1 + count);
    });
    return map1;
  }

  /**
   * Returns a map which contains the most common words (no stop words) and their absolute
   * frequency.
   *
   * @param count     how many words should be returned. The count most common words will be
   *                  returned in the map.
   * @param dateMin   no complaints before this date will be evaluated.
   * @param dateMax   no complaints after this date will be evaluated.
   * @param sentiment only complaints with this sentiment will be evaluated.
   * @param subject   only complaints with this subject will be evaluated.
   * @param wordsOnly if this is true, all words that contain no letters will be skipped.
   * @return a map with the most common words and their frequency.
   */
  @GetMapping("/api/stats/tagcloud")
  public ResponseEntity<?> getMostCommonWords(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("sentiment") Optional<String[]> sentiment,
      @RequestParam("subject") Optional<String[]> subject,
      @RequestParam("words_only") Optional<Boolean> wordsOnly) {

    return ControllerUtility.tryAndCatch(() -> {
      int maxComplaintCount = count.orElse(Integer.MAX_VALUE);
      LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

      // create stream of all complaints
      StreamSupport.stream(complaintRepository.findAll().spliterator(),
          false)
          // filter complaints
          .filter(compl -> ComplaintFilter.filterByDate(compl, dateMin, dateMax))
          .filter(compl -> ComplaintFilter.filterBySubject(compl, subject))
          .filter(compl -> ComplaintFilter.filterByEmotion(compl, sentiment))
          // get their word lists
          .map(Complaint::getWordList)
          // combine all count maps together
          .reduce(new HashMap<>(), StatsController::combineCountMaps)
          .entrySet()
          .stream()
          // filter out numbers of wordsOnly is true
          .filter(entry -> !wordsOnly.orElse(false)
              || entry.getKey().matches(".*[a-zA-Z].*"))
          // sort by count descending
          .sorted((entry1, entry2) -> -Integer.compare(entry1.getValue(), entry2.getValue()))
          // only take the most common words when count is given
          .limit(maxComplaintCount)
          // create new map from the results
          .forEach(entry -> result.put(entry.getKey(), entry.getValue()));

      return result;
    });
  }

}
