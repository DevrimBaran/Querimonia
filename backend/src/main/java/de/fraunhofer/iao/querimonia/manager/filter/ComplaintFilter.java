package de.fraunhofer.iao.querimonia.manager.filter;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
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
   * @param complaint          the complaint which gets checked.
   * @param optionalDateMinStr the earliest date that should be accepted. All complaints before this
   *                           date get rejected.
   * @param optionalDateMaxStr the latest date that should be accepted.
   *
   * @return true, if the upload date of the complaint is not after the maximum date or the maximum
   *     date is not given and the complaint not before the minimum date or the minimum date is not
   *     given.
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
   * Checks if a complaint is in a certain state.
   *
   * @param complaint      the complaints which gets checked.
   * @param optionalStates contains possible states, that the complaint may be in. The states
   *                       should match the {@link ComplaintState} names.
   *
   * @return true, if the complaint is one of the states given by the parameter, else false.
   */
  public static boolean filterByState(Complaint complaint, Optional<String[]> optionalStates) {
    if (optionalStates.isEmpty()) {
      return true;
    }
    // get stream of optional
    Stream<String> states = optionalStates.stream().flatMap(Stream::of);
    return states.anyMatch(complaint.getState().name()::equalsIgnoreCase);
  }

  /**
   * Checks if the complaint text of the given complaint contains all the given keywords. Case is
   * ignored.
   *
   * @param complaint        the complaint to check.
   * @param optionalKeywords the keywords that should be in the complaint text.
   *
   * @return true, if the keywords are not present or the text contains all keywords, ignoring case.
   */
  public static boolean filterByKeywords(Complaint complaint, Optional<String[]> optionalKeywords) {
    // get stream of optional
    Stream<String> keywords = optionalKeywords.stream().flatMap(Stream::of);
    // look for all keywords
    return keywords
        .allMatch(keyword -> StringUtils.containsIgnoreCase(complaint.getText(), keyword));
  }

  /**
   * Checks if a complaints sentiment is one of the given emotions. A complaint has the sentiment,
   * that has the highest probability in the sentiment map.
   *
   * @param complaint the complaint which gets checked.
   * @param emotions  an optional array of emotions. The complaint should match at least one of
   *                  these.
   *
   * @return true, if the value of emotions is absent or the complaint has a sentiment with the
   *     highest probability that in one of the given emotions.
   */
  public static boolean filterByEmotion(Complaint complaint, Optional<String[]> emotions) {
    return checkForParameters(complaint.getSentiment().getEmotion(), emotions);
  }

  /**
   * Checks if a complaints subject is one of the given subjects. A complaint has a certain subject,
   * if the probability of it is at least 50%. If the value of the subjects is not given, this
   * filter will return true.
   *
   * @param complaint the complaint which gets checked.
   * @param subjects  an optional array of subjects. The complaint gets checked if its subject is
   *                  one of these.
   *
   * @return true, if the value of sentiments is absent or
   */
  public static boolean filterBySubject(Complaint complaint, Optional<String[]> subjects) {
    return checkForParameters(complaint.getSubject(), subjects);
  }

  private static boolean checkForParameters(ComplaintProperty complaintProperty,
                                            Optional<String[]> optionalParameters) {
    if (optionalParameters.isEmpty()) {
      // no filter is applied, then return true
      return true;
    }
    return Arrays.asList(optionalParameters.get()).contains(complaintProperty.getValue());
  }

  /**
   * Creates a comparator for sorting the complaints.
   *
   * @param sortBy an array of string, that specify the sorting. See open api specification for
   *               details.
   */
  public static Comparator<Complaint> createComplaintComparator(Optional<String[]> sortBy) {
    return new ComparatorBuilder<Complaint>()
        .append("upload_date", complaint -> LocalDateTime.of(complaint.getReceiveDate(),
            complaint.getReceiveTime()))
        .append("id", Complaint::getId)
        .append("sentiment", Complaint::getSentiment)
        .append("state", Complaint::getState)
        .append("emotion", complaint -> complaint.getSentiment().getEmotion())
        .append("subject", Complaint::getSubject)
        .build(sortBy.orElse(new String[]{"state_asc", "upload_date_desc"}));
  }

}
