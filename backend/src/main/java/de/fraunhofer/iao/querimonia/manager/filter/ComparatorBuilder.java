package de.fraunhofer.iao.querimonia.manager.filter;

import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is used to build comparators that can be used for sorting. A comparator uses sort
 * aspects for the comparison, that can be added using {@link #append(String, Function)}.
 * A comparator can be created using {@link #build(String[])}.
 *
 * @param <T> the type that should be sorted/compared.
 */
public class ComparatorBuilder<T> {

  private final Map<String, Function<T, Comparable>> sortAttributes = new HashMap<>();

  /**
   * Creates a new comparator that compares objects of the type T. The array of sort aspects
   * specifies which attributes should be used for sorting. The sequence defines the priority of
   * each sort aspect, the first aspect is the most prioritised one.
   *
   * @param sortAspects the array of sort aspects. Each aspect must be defined using {@link
   *                    #append(String, Function)}. The have to have the prefix "asc" or "desc"
   *                    to define if the sorting should be ascending or descending.
   *
   * @return a comparator that compares objects of type T.
   */
  public Comparator<T> build(String[] sortAspects) {
    return (t1, t2) -> {
      int compareValue = 0;

      for (String sortAspect : sortAspects) {
        if (!(sortAspect.endsWith("desc") || sortAspect.endsWith("asc"))) {
          throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
              "Ungültiger Sortier-Parameter: " + sortAspect + "; Sortierparameter müssen mit "
                  + "'desc' und 'asc' enden.", "Illegaler Parameter");
        }
        // get index where prefix starts
        int indexOfPrefix = sortAspect.lastIndexOf('_');
        // get aspect without prefix
        String rawSortAspect = sortAspect.substring(0, indexOfPrefix);
        if (sortAttributes.containsKey(rawSortAspect)) {
          var attributeFunction = sortAttributes.get(rawSortAspect);
          Comparable attribute1 = attributeFunction.apply(t1);
          Comparable attribute2 = attributeFunction.apply(t2);

          //noinspection unchecked
          compareValue = attribute1.compareTo(attribute2);
        } else {
          throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
              "Unbekannter Sortiertparameter: " + rawSortAspect,
              "Illegaler Parameter");
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

  /**
   * Adds a new sort aspect for the comparator.
   *
   * @param sortAspect        the name of the sort aspect.
   * @param attributeFunction the function that returns the comparable attribute which should be
   *                          used for the comparision.
   *
   * @return this instance.
   */
  public ComparatorBuilder<T> append(String sortAspect, Function<T, Comparable> attributeFunction) {
    sortAttributes.put(sortAspect, attributeFunction);
    return this;
  }

}
