package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import de.fraunhofer.iao.querimonia.manager.filter.ComplaintFilter;
import de.fraunhofer.iao.querimonia.nlp.Sentiment;
import de.fraunhofer.iao.querimonia.repository.ComplaintRepository;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.PersistentResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.rest.restobjects.CategoryStats;
import de.fraunhofer.iao.querimonia.rest.restobjects.MonthStats;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
   * Combines two lists into one.
   */
  private static <T> List<T> combineLists(List<T> list1, List<T> list2) {
    list1.addAll(list2);
    return list1;
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
   *
   * @return a map with the most common words and their frequency.
   */
  @GetMapping("/api/stats/tagcloud")
  public ResponseEntity<?> getMostCommonWords(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("emotion") Optional<String[]> sentiment,
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
          .map(Complaint::getWordCounts)
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

  /**
   * Returns a map which contains the most common categories and their stats.
   *
   * @param count   how many categories should be returned.
   *                The count most common categories will be returned in the map.
   * @param dateMin no complaints before this date will be evaluated.
   * @param dateMax no complaints after this date will be evaluated.
   * @param subject only complaints with this subject will be evaluated.
   *
   * @return a map with the most common categories and their stats.
   */
  @GetMapping("/api/stats/categoriesStats")
  public ResponseEntity<?> getCategoriesStats(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("subject") Optional<String[]> subject) {

    return ControllerUtility.tryAndCatch(() -> {
      int maxComplaintCount = count.orElse(Integer.MAX_VALUE);

      LinkedHashMap<String, CategoryStats> result = new LinkedHashMap<>();

      LinkedHashMap<String, Integer> resultCategorie = new LinkedHashMap<>();
      // create stream of all complaints
      StreamSupport.stream(complaintRepository.findAll().spliterator(),
          false)
          // filter complaints
          .filter(compl -> ComplaintFilter.filterByDate(compl, dateMin, dateMax))
          .filter(compl -> ComplaintFilter.filterBySubject(compl, subject))
          // get their word lists
          .map(Complaint::getProperties).reduce(new ArrayList<>(),
          StatsController::combineLists).stream().map(ComplaintProperty::getValue)
          .forEach(entry -> {
            int value = resultCategorie.getOrDefault(entry, 0) + 1;
            resultCategorie.put(entry, value);
          });
      ArrayList<String> resultCategoriesSorted = new ArrayList<>();
      resultCategorie.entrySet().stream()
          .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
          .limit(maxComplaintCount).forEach(cat -> resultCategoriesSorted.add(cat.getKey()));

      for (String categorie : resultCategoriesSorted) {

        double resultTendency = 0.0;
        LinkedHashMap<String, Double> emotion = new LinkedHashMap<>();
        String[] categorieArray = {categorie};
        // create stream of all complaints
        ArrayList<Sentiment> sent = new ArrayList<>();
        StreamSupport.stream(complaintRepository.findAll().spliterator(), false)
            // filter complaints
            .filter(compl -> ComplaintFilter.filterByDate(compl, dateMin, dateMax))
            .filter(compl -> ComplaintFilter.filterBySubject(compl, Optional.of(categorieArray)))
            // get their word lists
            .map(Complaint::getSentiment).forEach(sent::add);
        for (Sentiment s : sent) {
          resultTendency += s.getTendency();
          double emotionSum = emotion.getOrDefault(
              s.getEmotion().getValue(), 0.0) + (1.0 / sent.size());
          emotion.put(s.getEmotion().getValue(), emotionSum);
        }
        int compCount = sent.size();
        double tendency = (resultTendency / sent.size());

        LinkedHashMap<String, Double> status = new LinkedHashMap<>();
        long countComplaints = StreamSupport.stream(complaintRepository.findAll()
            .spliterator(), false)
            .filter(compl -> ComplaintFilter.filterByDate(compl, dateMin, dateMax))
            .filter(compl -> ComplaintFilter.filterBySubject(compl, Optional.of(categorieArray)))
            .count();
        for (ComplaintState state : ComplaintState.values()) {
          // create stream of all complaints
          String[] stateAr = {state.toString()};
          long c = StreamSupport.stream(complaintRepository.findAll().spliterator(), false)
              // filter complaints
              .filter(compl -> ComplaintFilter.filterByDate(compl, dateMin, dateMax))
              .filter(compl -> ComplaintFilter.filterBySubject(compl, Optional.of(categorieArray)))
              .filter(complaint -> ComplaintFilter.filterByState(complaint, Optional.of(stateAr)))
              .count();
          status.put(state.toString(), countComplaints == 0
              ? 0 : (double) c / countComplaints);
        }

        CategoryStats categoryStats = new CategoryStats(compCount, tendency, emotion, status);

        result.put(categorie, categoryStats);

      }

      return result;
    });
  }

  /**
   * Returns a map which contains labels of entities and the most found entities.
   *
   * @param count   how many entities should be returned. The most found entities will be
   *                returned in the map.
   * @param dateMin no complaints before this date will be evaluated.
   * @param dateMax no complaints after this date will be evaluated.
   * @param subject only complaints with this subject will be evaluated.
   *
   * @return a map with the labels of entities and the most found entities.
   */
  @GetMapping("/api/stats/entitiesStats")
  public ResponseEntity<?> getEntitiesStats(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("subject") Optional<String[]> subject) {

    return ControllerUtility.tryAndCatch(() -> {
      int maxComplaintCount = count.orElse(Integer.MAX_VALUE);

      LinkedHashMap<String, LinkedHashMap<String, Integer>> result = new LinkedHashMap<>();

      StreamSupport.stream(complaintRepository.findAll().spliterator(),
          false)
          // filter complaints
          .filter(compl -> ComplaintFilter.filterByDate(compl, dateMin, dateMax))
          .filter(compl -> ComplaintFilter.filterBySubject(compl, subject))
          // get their word lists
          .map(Complaint::getEntities)
          .reduce(new ArrayList<>(), StatsController::combineLists)
          .forEach(ent -> {
            LinkedHashMap<String, Integer> value = result.getOrDefault(
                ent.getLabel(), new LinkedHashMap<>());
            int value2 = value.getOrDefault(ent.getValue(), 0) + 1;
            value.put(ent.getValue(), value2);
            result.put(ent.getLabel(), value);
          });
      if (maxComplaintCount != Integer.MAX_VALUE) {
        for (String key : result.keySet()) {
          LinkedHashMap<String, Integer> newValue = new LinkedHashMap<>();
          result.get(key).entrySet().stream()
              .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
              .limit(maxComplaintCount)
              .forEach(ent -> newValue.put(ent.getKey(), ent.getValue()));
          result.replace(key, newValue);
        }
      }
      return result;
    });
  }

  /**
   * Returns a map which contains the most common rules and their number of occurrences.
   *
   * @param count     how many rules should be returned. The count most common rules will be
   *                  returned in the map.
   * @param dateMin   no complaints before this date will be evaluated.
   * @param dateMax   no complaints after this date will be evaluated.
   * @param sentiment only complaints with this sentiment will be evaluated.
   * @param subject   only complaints with this subject will be evaluated.
   *
   * @return a map with the most common rules and their number of occurrences.
   */
  @GetMapping("/api/stats/rulesStats")
  public ResponseEntity<?> getRuleStats(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("sentiment") Optional<String[]> sentiment,
      @RequestParam("subject") Optional<String[]> subject) {

    return ControllerUtility.tryAndCatch(() -> {
      int maxComplaintCount = count.orElse(Integer.MAX_VALUE);

      LinkedHashMap<String, Integer> result = new LinkedHashMap<>();

      StreamSupport.stream(complaintRepository.findAll().spliterator(),
          false)
          // filter complaints
          .filter(compl -> ComplaintFilter.filterByDate(compl, dateMin, dateMax))
          .filter(compl -> ComplaintFilter.filterBySubject(compl, subject))
          .filter(compl -> ComplaintFilter.filterByEmotion(compl, sentiment))
          // get their word lists
          .map(Complaint::getResponseSuggestion)
          .map(ResponseSuggestion::getResponseComponents)
          .reduce(new ArrayList<>(), StatsController::combineLists)
          .stream()
          .map(CompletedResponseComponent::getComponent)
          .map(PersistentResponseComponent::getComponentName)
          .forEach(name -> result.merge(name, 1, Integer::sum));
      if (maxComplaintCount != Integer.MAX_VALUE) {
        LinkedHashMap<String, Integer> resultFixSize = new LinkedHashMap<>();
        result.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(maxComplaintCount)
            .forEach(rule -> resultFixSize.put(rule.getKey(), rule.getValue()));
        return resultFixSize;
      }

      return result;
    });
  }

  /**
   * Returns a map which contains months and their stats.
   *
   * @param dateMin   no months before this date will be evaluated.
   * @param dateMax   no months after this date will be evaluated.
   * @param sentiment only complaints with this sentiment will be evaluated.
   * @param subject   only complaints with this subject will be evaluated.
   *
   * @return a map with the months and their stats.
   */
  @GetMapping("/api/stats/monthsStats")
  public ResponseEntity<?> getMonthsStats(
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("sentiment") Optional<String[]> sentiment,
      @RequestParam("subject") Optional<String[]> subject) {

    return ControllerUtility.tryAndCatch(() -> {

      LinkedHashMap<YearMonth, MonthStats> result = new LinkedHashMap<>();

      String[] sortBy = {"upload_date_asc"};
      Optional<Complaint> comp = StreamSupport.stream(complaintRepository.findAll().spliterator(),
          false)
          // filter complaints
          .filter(compl -> ComplaintFilter.filterByDate(compl, dateMin, dateMax))
          .filter(compl -> ComplaintFilter.filterBySubject(compl, subject))
          .filter(compl -> ComplaintFilter.filterByEmotion(compl, sentiment))
          .min(ComplaintFilter.createComplaintComparator(Optional.of(sortBy)));
      if (comp.isEmpty()) {
        return result;
      }
      LocalDate firstDate = comp.get().getReceiveDate();
      LocalDate endday = LocalDate.now();
      if (dateMax.isPresent()) {
        endday = LocalDate.parse(dateMax.get());
      }
      for (LocalDate date = firstDate; !date.isAfter(endday);
           date = date.plusMonths(1).withDayOfMonth(1)) {
        LinkedHashMap<String, Double> resultComplaintsStatus = new LinkedHashMap<>();
        Optional<Long> processingHours = Optional.empty();
        double avgProcessing = 0d;
        final LocalDate minDate = date;
        final LocalDate maxDate;
        if (endday.isBefore(date.withDayOfMonth(date.lengthOfMonth()))) {
          maxDate = endday;
        } else {
          maxDate = date.withDayOfMonth(date.lengthOfMonth());
        }
        long countComplaints = StreamSupport.stream(complaintRepository.findAll().spliterator(),
            false)
            .filter(compl -> ComplaintFilter.filterByDate(compl, Optional.of(minDate.toString()),
                Optional.of(maxDate.toString()))).count();
        for (ComplaintState state : ComplaintState.values()) {
          // create stream of all complaints
          String[] stateArray = {state.toString()};

          final long countStateCom = StreamSupport.stream(complaintRepository
              .findAll().spliterator(), false)
              // filter complaints
              .filter(compl -> ComplaintFilter.filterByDate(
                  compl, Optional.of(minDate.toString()), Optional.of(maxDate.toString())))
              // get their word lists
              .filter(complaint -> ComplaintFilter.filterByState(
                  complaint, Optional.of(stateArray))).count();
          resultComplaintsStatus.put(state.toString(), (double) countStateCom / countComplaints);

          if (state == ComplaintState.CLOSED) {
            processingHours = StreamSupport.stream(complaintRepository
                .findAll().spliterator(), false)
                // filter complaints
                .filter(compl -> ComplaintFilter.filterByDate(
                    compl, Optional.of(minDate.toString()), Optional.of(maxDate.toString())))
                // get their word lists
                .filter(complaint -> ComplaintFilter.filterByState(
                    complaint, Optional.of(stateArray)))
                .map(complaint -> complaint.getReceiveDate().atTime(complaint.getReceiveTime())
                    .until(complaint.getCloseDate().atTime(complaint.getCloseTime()),
                        ChronoUnit.HOURS))
                .reduce(Long::sum);
          }
          avgProcessing =
              processingHours.map(hours -> hours / ((double) countStateCom)).orElse(0d);
        }

        MonthStats monthStats = new MonthStats(countComplaints, resultComplaintsStatus,
            avgProcessing);

        result.put(YearMonth.from(date), monthStats);
      }

      return result;
    });
  }

}