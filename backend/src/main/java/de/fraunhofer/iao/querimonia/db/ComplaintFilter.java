package de.fraunhofer.iao.querimonia.db;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class is an utility class that contains method for filtering the complaints in the
 * database.
 *
 * @author Paul Bredl
 */
public class ComplaintFilter {

  /**
   * Checks if a complaint was uploaded in a certain time interval.
   *
   * @param complaint       the complaint which gets checked.
   * @param optionalDateMinStr the earliest date that should be accepted. All complaints before this
   *                        date get rejected.
   * @param optionalDateMaxStr the latest date that should be accepted.
   * @return true, if the upload date of the complaint is not after the maximum date or the maximum
   * date is not given and the complaint not before the minimum date or the minimum date
   * is not given.
   */
  public static boolean filterByDate(Complaint complaint, Optional<String> optionalDateMinStr,
                                     Optional<String> optionalDateMaxStr) {
    Optional<LocalDate> optionalDateMin = optionalDateMinStr.map(LocalDate::parse);
    Optional<LocalDate> optionalDateMax = optionalDateMaxStr.map(LocalDate::parse);
    return optionalDateMin
        // receive date may not be before the min date
        .map(dateMin -> !complaint.getReceiveDate().isBefore(dateMin))
        // condition is true when no filter is given
        .orElse(true)
        && optionalDateMax
        // receive date my not be after the max date
        .map(dateMax -> !complaint.getReceiveDate().isAfter(dateMax))
        // condition is true when no filter is given
        .orElse(true);
  }

  /**
   * Checks if the complaint text of the given complaint contains all the given keywords.
   * Case is ignored.
   *
   * @param complaint        the complaint to check.
   * @param optionalKeywords the keywords that should be in the complaint text.
   * @return true, if the keywords are not present or the text contains all keywords, ignoring case.
   */
  public static boolean filterByKeywords(Complaint complaint, Optional<String[]> optionalKeywords) {
    // get stream of optional
    Stream<String> keywords = optionalKeywords.map(Stream::of).orElseGet(Stream::empty);
    // look for all keywords
    return keywords
        .allMatch(keyword -> StringUtils.containsIgnoreCase(complaint.getText(), keyword));
  }

  /**
   * Checks if a complaints sentiment is one of the given sentiments. A complaint has the sentiment,
   * that has the highest probability in the sentiment map.
   *
   * @param complaint  the complaint which gets checked.
   * @param sentiments an optional array of sentiments. The complaint should match at least one of
   *                   these.
   * @return true, if the value of sentiments is absent or the complaint has a sentiment with
   * the highest probability that in one of the given sentiments.
   */
  public static boolean filterBySentiment(Complaint complaint, Optional<String[]> sentiments) {
    return checkForParameters(complaint.getSentiment(), sentiments);
  }

  /**
   * Checks if a complaints subject is one of the given subjects. A complaint has a certain
   * subject, if the probability of it is at least 50%. If the value of the subjects is not
   * given, this filter will return true.
   *
   * @param complaint the complaint which gets checked.
   * @param subjects  an optional array of subjects. The complaint gets checked if its subject
   *                  is one of these.
   * @return true, if the value of sentiments is absent or
   */
  public static boolean filterBySubject(Complaint complaint, Optional<String[]> subjects) {
    return checkForParameters(complaint.getSubject(), subjects);
  }

  private static boolean checkForParameters(Map<String, Double> probabilityMap,
                                            Optional<String[]> optionalParameters) {
    //noinspection OptionalIsPresent
    if (!optionalParameters.isPresent()) {
      // no filter is applied, then return true
      return true;
    }
    return ComplaintUtility.getEntryWithHighestProbability(probabilityMap)
        .map(sentiment ->
            // check if the sentiment/subject of the complaint matches
            Arrays.asList(optionalParameters.get()).contains(sentiment))
        // if not present, complaint has no subject/sentiment
        .orElse(false);
  }

  /**
   * Creates a comparator for sorting the complaints.
   *
   * @param sortBy an array of string, that specify the sorting. See open api specification
   *               for details.
   */
  public static Comparator<Complaint> createComplaintComparator(Optional<String[]> sortBy) {
    return (c1, c2) -> {
      if (sortBy.isPresent()) {
        int compareValue = 0;

        for (String sortAspect : sortBy.get()) {
          // get index where prefix starts
          int indexOfPrefix = sortAspect.lastIndexOf('_');
          // get aspect without prefix
          String rawSortAspect = sortAspect.substring(0, indexOfPrefix);

          switch (rawSortAspect) {
            case "upload_date":
              compareValue = c1.getReceiveDate().compareTo(c2.getReceiveDate());
              if (compareValue == 0) {
                compareValue = c1.getReceiveTime().compareTo(c2.getReceiveTime());
              }
              break;
            case "sentiment":
              // TODO better sorting for sentiments
              compareValue = c1.getBestSentiment()
                  .orElse("")
                  .compareTo(c2.getBestSentiment().orElse(""));
              break;
            case "subject":
              compareValue = c1.getBestSubject().orElse("")
                  .compareTo(c2.getBestSubject().orElse(""));
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
            // only continue on equal complaints)
            return compareValue;
          }

        }
        // all sorting aspects where checked
        return compareValue;
      } else {
        // sort by date *descending* per default
        int compareValue = c1.getReceiveDate().compareTo(c2.getReceiveDate());
        if (compareValue == 0) {
          compareValue = c1.getReceiveTime().compareTo(c2.getReceiveTime());
        }
        return -compareValue;
      }
    };
  }

}
