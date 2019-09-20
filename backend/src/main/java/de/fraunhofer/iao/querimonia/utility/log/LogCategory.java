package de.fraunhofer.iao.querimonia.utility.log;

/**
 * This enum represents the category of a log entry.
 */
public enum LogCategory {

  /**
   * An entry that contains information about the analysis.
   */
  ANALYSIS,

  /**
   * General information, like refreshing or closing the complaint.
   */
  GENERAL,

  /**
   * Error information.
   */
  ERROR
}
