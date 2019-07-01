package de.fraunhofer.iao.querimonia.rest.manager.filter;

import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.Optional;

/**
 * The filterclass for templates.
 */
public class ResponseComponentFilter {
  /**
   * This methods sorts by name or priority of the component.
   * @param sortBy Is the variable of which aspect should be sorted.
   * @return Returns the sorted component.
   */
  public static Comparator<ResponseComponent> createTemplateComparator(Optional<String[]> sortBy) {
    return (template1, template2) -> {
      int compareValue = 0;

      for (String sortAspect : sortBy.orElse(new String[] {"name_asc"})) {
        if (!(sortAspect.endsWith("desc") || sortAspect.endsWith("asc"))) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "Illegal sorting paramter: Sorting parameter must end with desc or asc.");
        }
        // get index where prefix starts
        int indexOfPrefix = sortAspect.lastIndexOf('_');
        // get aspect without prefix
        String rawSortAspect = sortAspect.substring(0, indexOfPrefix);
        switch (rawSortAspect) {
          case "priority":
            compareValue = Integer.compare(template1.getPriority(), template2.getPriority());
            break;
          case "name":
            compareValue = template1.getComponentName().toLowerCase().compareTo(template2
                .getComponentName().toLowerCase());
            break;
          default:
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Illegal sorting paramter: " + rawSortAspect);
        }
        // invert sorting if desc
        if (sortAspect.substring(indexOfPrefix).equalsIgnoreCase("_desc")) {
          compareValue *= -1;
        }

        if (compareValue != 0) {
          // if difference is already found, don't continue comparing
          // (the later the aspects are in the array, the less priority they have, so
          // only continue on equal templates)
          return compareValue;
        }

        // all sorting aspects where checked
      }
      return compareValue;
    };
  }
}
