package de.fraunhofer.iao.querimonia.rest.manager;


import de.fraunhofer.iao.querimonia.db.repositories.ActionRepository;
import de.fraunhofer.iao.querimonia.response.action.Action;
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

    return filteredResult.collect(Collectors.toList());
  }


  public Action addAction(Action action){
    actionRepository.save(action);
    return action;
  }

  public Optional<Action> getActionbyId(int actionId){
    return actionRepository.findById(actionId);
  }

  public boolean deleteAction(int actionId){
    Optional<Action> possibleAction = actionRepository.findById(actionId);
    if (possibleAction.isPresent()){
      actionRepository.deleteById(actionId);
      return true;
    }else{
      return false;
    }
  }

  public boolean updateAction(int actionId, Action action){
    return false;
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
