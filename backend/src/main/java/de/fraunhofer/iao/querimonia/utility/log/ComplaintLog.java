package de.fraunhofer.iao.querimonia.utility.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Contains factory methods to create {@link LogEntry log entries}.
 */
public class ComplaintLog {

  private static final Logger LOGGER = LoggerFactory.getLogger(ComplaintLog.class);

  /**
   * Creates a new log entry. Also logs the message in the console.
   *
   * @param category    the category of the log item.
   * @param complaintId the of the complaint the log item belongs to.
   * @param message     the message of the log item. The message may not be longer than
   *                    {@value LogEntry#MESSAGE_MAX_LENGTH} characters.
   *
   * @return the new log entry.
   */
  public static LogEntry createLogEntry(LogCategory category, long complaintId, String message) {
    LOGGER.info("complaint " + complaintId + " - " + category.name() + " - " + message);
    return new LogEntry(category, fitStringLength(message),
        LocalDate.now(ZoneId.of("Europe/Berlin")),
        LocalTime.now(ZoneId.of("Europe/Berlin")));
  }

  private static String fitStringLength(String message) {
    return message.substring(0, Math.min(message.length(), LogEntry.MESSAGE_MAX_LENGTH));
  }
}
