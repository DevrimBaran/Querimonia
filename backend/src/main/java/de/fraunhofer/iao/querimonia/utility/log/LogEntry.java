package de.fraunhofer.iao.querimonia.utility.log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iao.querimonia.complaint.ComplaintUtility;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This class represents a single entry in the log. Log entries are used to give the user
 * information about the analysis process and error if they occur.
 */
@Entity
public class LogEntry {

  /**
   * The maximum length of the log message.
   */
  public static final int MESSAGE_MAX_LENGTH = 1024;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private long id;

  @NonNull
  @Enumerated(EnumType.STRING)
  private LogCategory category = LogCategory.GENERAL;

  @NonNull
  @Column(length = MESSAGE_MAX_LENGTH)
  private String message = "";

  @NonNull
  private LocalDate date = LocalDate.now();

  @NonNull
  private LocalTime time = LocalTime.now();

  @SuppressWarnings("unused")
  private LogEntry() {
    // hibernate constructor
  }

  /**
   * Constructor for the factory methods of {@link ComplaintLog}.
   */
  LogEntry(@NonNull LogCategory category, @NonNull String message,
                  @NonNull LocalDate date, @NonNull LocalTime time) {

    ComplaintUtility.checkStringLength(message, MESSAGE_MAX_LENGTH);

    this.category = category;
    this.message = message;
    this.date = date;
    this.time = time;
  }

  /**
   * Returns the id of the log entry. The id is only necessary for database persistence.
   *
   * @return the id of the log entry.
   */
  public long getId() {
    return id;
  }

  /**
   * Returns the category of the log entry. The category is shown as prefix in front of the message.
   *
   * @return the {@link LogCategory category} of the log entry.
   */
  @NonNull
  public LogCategory getCategory() {
    return category;
  }

  /**
   * Returns the message of the log entry.
   *
   * @return the message of the log entry.
   */
  @NonNull
  public String getMessage() {
    return message;
  }

  /**
   * Returns the date when the log entry was created. This is when the event that is logged with
   * this log entry happened.
   *
   * @return the date when the log entry was created.
   */
  @NonNull
  public LocalDate getDate() {
    return date;
  }

  /**
   * Returns the time when the log entry was created. This is when the event that is logged with
   * this log entry happened.
   *
   * @return the time when the log entry was created.
   */
  @NonNull
  public LocalTime getTime() {
    return time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LogEntry logEntry = (LogEntry) o;

    return new EqualsBuilder()
        .append(category, logEntry.category)
        .append(message, logEntry.message)
        .append(date, logEntry.date)
        .append(time, logEntry.time)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(category)
        .append(message)
        .append(date)
        .append(time)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("category", category)
        .append("message", message)
        .append("date", date)
        .append("time", time)
        .toString();
  }
}
