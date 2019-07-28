package de.fraunhofer.iao.querimonia.complaint;

/**
 * The state of the workflow process of a complaint.
 */
public enum ComplaintState {

  /**
   * Analysis could not be finished.
   */
  ERROR,

  /**
   * Analysis is in progress, complaint can not be edited in this time.
   */
  ANALYSING,

  /**
   * The client has not started working with the complaint.
   */
  NEW,

  /**
   * The client started working with the complaint.
   */
  IN_PROGRESS,

  /**
   * Complaint is finished, it can no longer be edited.
   */
  CLOSED
}
