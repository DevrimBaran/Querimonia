package de.fraunhofer.iao.querimonia.manager.filter;

import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The filter/sorter class for components.
 */
public class ResponseComponentFilter {

  /**
   * Checks if a response component contains certain keywords.
   *
   * @param responseComponent the component that gets checked.
   * @param optionalKeywords  the keywords, if not present this will return true.
   *
   * @return true, if the keywords are either in the component text or not present.
   */
  public static boolean filterByKeywords(ResponseComponent responseComponent, Optional<String[]>
      optionalKeywords) {
    // get stream of optional
    Stream<String> keywords = optionalKeywords.stream().flatMap(Stream::of);
    // look for all keywords
    return keywords
        .allMatch(keyword -> StringUtils.containsIgnoreCase(
            responseComponent.getComponentTexts().toString(), keyword)
            || StringUtils.containsIgnoreCase(responseComponent.getComponentName(), keyword));

  }

  /**
   * Checks if a response component contains certain section.
   *
   * @param responseComponent the component that gets checked.
   * @param optionalActionCode the Action-Code, if not present this will return true.
   *
   * @return true, if a Action with the Action-Code are either in the component or not present.
   */
  public static boolean filterByActionCode(ResponseComponent responseComponent, Optional<String[]>
      optionalActionCode) {
    ArrayList<String> actionCodes = new ArrayList<>();
    for (Action action: responseComponent.getActions()) {
      actionCodes.add(action.getActionCode().toString());
    }
    // get stream of optional
    Stream<String> optActionCodes = optionalActionCode.stream().flatMap(Stream::of);
    return optActionCodes.allMatch(actionCodes::contains);
  }

  /**
   * This methods sorts by name or priority of the component.
   *
   * @param sortBy Is the variable of which aspect should be sorted.
   *
   * @return Returns the sorted component.
   */
  public static Comparator<ResponseComponent> createComponentComparator(Optional<String[]> sortBy) {
    return new ComparatorBuilder<ResponseComponent>()
        .append("name", ResponseComponent::getComponentName)
        .append("id", ResponseComponent::getId)
        .append("priority", ResponseComponent::getPriority)
        .build(sortBy.orElse(new String[] {"id_asc"}));
  }
}
