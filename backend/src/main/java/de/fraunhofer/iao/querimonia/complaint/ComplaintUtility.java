package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.springframework.http.HttpStatus;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Helper methods for working with complaints.
 */
public class ComplaintUtility {

  /**
   * Helper function to get the value with the highest probability out a probability map.
   *
   * @param probabilityMap the map which maps the value to their probabilities.
   *
   * @return the entry with the highest probability or an empty optional if the map is empty.
   */
  public static Optional<String> getEntryWithHighestProbability(
      Map<String, Double> probabilityMap) {
    Optional<Map.Entry<String, Double>> result = probabilityMap.entrySet().stream()
        // find entry with highest probability
        .max(Comparator.comparingDouble(Map.Entry::getValue));
    if (result.isPresent() && result.map(Map.Entry::getValue).get() == 0) {
      return (Optional.of("Sonstiges"));
    }
    return result.map(Map.Entry::getKey);
  }

  /**
   * Returns a certain property of the complaint.
   *
   * @param complaint the complaint.
   * @param name      the name of the property that should be extracted.
   *
   * @return the property with the given name.
   *
   * @throws IllegalStateException if the no property with the given name exists.
   */
  public static ComplaintProperty getPropertyOfComplaint(Complaint complaint, String name) {
    return complaint.getProperties()
        .stream()
        .filter(complaintProperty -> complaintProperty.getName().equals(name))
        .findAny()
        .orElse(ComplaintProperty.getDefaultProperty("Kategorie"));
  }

  /**
   * Checks the length of a string an throws an exception if the length is too long.
   *
   * @param toCheck   the string which length gets checked.
   * @param maxLength the maximal length that the string may have.
   *
   * @throws QuerimoniaException if the given string is too long.
   */
  public static void checkStringLength(String toCheck, int maxLength) {
    if (toCheck.length() > maxLength) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
          "Textlänge überschreitet Maximum von " + maxLength + " Zeichen!", "Zu langer Text");
    }
  }

  /**
   * Sending a mail from the fraunhofer mail host.
   *
   * @param subject of the mail
   * @param content content of mail
   * @param sender sender of the mail HAS TO BE a @iao.fraunhofer.de mail
   * @param receiver recipient of the mail
   * @throws MessagingException if mailing has invalif parameter
   */
  public static void sendEMail(String subject, String content, String sender,
                                    String receiver) throws MessagingException {
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", "mailhost.iao.fraunhofer.de");
    Session session = Session.getDefaultInstance(properties);
    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress(sender));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
    message.setSubject(subject, "ISO-8859-1");
    message.setText(content, "UTF-8");
    Transport.send(message);
  }

}
