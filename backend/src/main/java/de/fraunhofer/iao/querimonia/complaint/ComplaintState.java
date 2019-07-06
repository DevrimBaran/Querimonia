package de.fraunhofer.iao.querimonia.complaint;

/**
 * The state of the workflow process of a complaint.
 */
public enum ComplaintState {

  /**
   * Complaint is new.
   */
  NEW,
  /**
   * The client started working with the complaint.
   */
  IN_PROGRESS,
  /**
   * Complaint is finished, it can no longer be edited.
   */
  CLOSED;

  ComplaintState() {

  }
}
