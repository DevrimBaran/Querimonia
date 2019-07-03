package de.fraunhofer.iao.querimonia.response.action;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// TODO implement
@Entity
public class Action {

  @Id
  @GeneratedValue
  private int actionId;
}
