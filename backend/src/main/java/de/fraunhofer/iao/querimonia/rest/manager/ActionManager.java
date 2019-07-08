package de.fraunhofer.iao.querimonia.rest.manager;


import de.fraunhofer.iao.querimonia.db.repositories.ActionRepository;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.action.ActionCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ActionManager {

  private static final Logger logger = LoggerFactory.getLogger(ActionManager.class);

  private final ActionRepository actionRepository;

  public ActionManager(ActionRepository actionRepository) {
    this.actionRepository = actionRepository;
  }

  public List<Action> getActions(Optional<Integer> count, Optional<Integer> page,
                                 Optional<String[]> sortBy, Optional<String[]> keywords) {
    ArrayList<Action> list = new ArrayList<>();
    actionRepository.findAll().forEach(list::add);

    //filter and sort Actions
    Stream<Action> filteredResult = list.stream()
        .filter(action -> filterByKeywords(action, keywords))
        .sorted(createComplaintComparator(sortBy));

    //getting the right amount and page
    if (count.isPresent()) {
      if (page.isPresent()) {
        // skip pages
        filteredResult = filteredResult
            .skip(page.get() * count.get());
      }
      // only take count amount of entries
      filteredResult = filteredResult.limit(count.get());
    }

    logger.info("returning actions");
    return filteredResult.collect(Collectors.toList());
  }


  public boolean addAction(Action action) {
    final boolean[] sameName = {false};
    actionRepository.findAll().forEach(action1 -> sameName[0] = Boolean.logicalOr(sameName[0],
        action.getName().equals(action1.getName())));
    if (!sameName[0]) {
      actionRepository.save(action);
      return true;
    }
    logger.info("Action with id " + action.getActionId() + " added ");
    return false;
  }

  public Optional<Action> getActionById(int actionId) {
    logger.info("returning the Action with Id " + actionId);
    return actionRepository.findById(actionId);
  }

  public boolean deleteAction(int actionId) {
    if (actionRepository.existsById(actionId)) {
      logger.info("deleted Action with Id " + actionId);
      actionRepository.deleteById(actionId);
      return true;
    } else {
      logger.info("Action with Id " + actionId + " is queried but not present in Database");
      return false;
    }
  }

  public boolean updateAction(int actionId, Action action) {
    if (!actionRepository.existsById(actionId)) {
      logger.info("Action with Id " + actionId + " is queried but not present in Database");
      return false;
    }
    actionRepository.deleteById(actionId);
    action.setActionId(actionId);
    actionRepository.save(action);
    logger.info("Action with Id " + actionId + " updated");
    return true;
  }

  /**
   * gets the count of actions currently in the database with specific attributes
   *
   * @param actionCode only count actions with this Action Code
   * @param keywords   only count Actions with these occurring keywords in them
   * @return amount of Actions with these attributes
   */
  public int getCount(Optional<ActionCode> actionCode, Optional<String[]> keywords) {
    ArrayList<Action> list = new ArrayList<>();
    actionRepository.findAll().forEach(list::add);
    int count = (int) list.stream()
        .filter(action -> filterByKeywords(action, keywords))
        .filter(action -> actionCode.map(code -> action.getActionCode().equals(code)).orElse(Boolean.TRUE))
        .count();
    logger.info("Returning the current count of Actions: " + count);
    return count;
  }

  public void deleteAll() {
    logger.info("deleted all Actions");
    actionRepository.deleteAll();
  }

  /**
   * filter action with an array of keywords
   */
  private boolean filterByKeywords(Action action, Optional<String[]> optionalKeywords) {
    // get stream of optional
    Stream<String> keywords = optionalKeywords.map(Stream::of).orElseGet(Stream::empty);
    //look for all
    return keywords.allMatch(keyword -> StringUtils.containsIgnoreCase
        (action.getName() + " " + action.getActionCode().getDescription(), keyword));

  }

  /**
   * Creates a comparator for sorting the actions.
   *
   * @param sortBy an array of string, that specify the sorting. See open api specification for
   *               details.
   */
  private static Comparator<Action> createComplaintComparator(Optional<String[]> sortBy) {
    return (a1, a2) -> {
      if (sortBy.isPresent()) {
        int compareValue;

        for (String sortAspect : sortBy.get()) {
          //get index where suffix starts
          int suffixIndex = sortAspect.lastIndexOf('_');

          //get sort Aspect without suffix
          String rawSortAspect = sortAspect.substring(0, suffixIndex);

          switch (rawSortAspect) {
            case "name":
              compareValue = a1.getName().compareTo(a2.getName());
              break;
            case "id":
              compareValue = Integer.compare(a1.getActionId(), a2.getActionId());
              break;
            default:
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                  "Illegal sorting paramter: " + rawSortAspect);

          }

          //switching between ascending and descending order
          if (sortAspect.substring(suffixIndex).equals("_desc")) {
            compareValue *= -1;
          }

          if (compareValue != 0)
          //check further sort aspects only if the previous (with more priority) are equal
          {
            return compareValue;
          }
        }
      }
      return -Integer.compare(a1.getActionId(), a2.getActionId());
    };

  }
}
