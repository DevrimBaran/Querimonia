package de.fraunhofer.iao.querimonia.response.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@JsonPropertyOrder(value = {
    "name",
    "actionCode",
    "rulesXml",
    "parameters"
})
public class Action {

  /**
   * The unique primary key of the action.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private long actionId;

  public void setActionId(long actionId) {
    this.actionId = actionId;
  }

  /**
   * An identifier for the actions to display.
   */
  @Column(name = "name", nullable = false)
  @NonNull
  private String name = "";

  /**
   * The action that could be executed.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "actionCode", nullable = false)
  @NonNull
  private ActionCode actionCode = ActionCode.SEND_MAIL;

  /**
   * Here are parameters for the Action to be executed.
   */
  @Column()
  @NonNull
  private HashMap<String, String> parameters = new HashMap<>();

  @JsonCreator
  @SuppressWarnings("unused")
  public Action(
      @NonNull
      @JsonProperty("name")
          String name,

      @NonNull
      @JsonProperty("actionCode")
          ActionCode actionCode,

      @NonNull
      @JsonProperty("parameters")
          HashMap<String, String> parameters
  ) {
    this.name = name;
    this.actionCode = actionCode;
    this.parameters = parameters;
  }

  /**
   * Empty default constructor (only used for hibernate).
   */
  @SuppressWarnings("unused")
  private Action() {

  }

  public long getActionId() {
    return actionId;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public ActionCode getActionCode() {
    return actionCode;
  }

  @NonNull
  public Map<String, String> getParameters() {
    return parameters;
  }

  /**
   * Runs the action.
   */
  public void executeAction() {
    switch (actionCode) {
      case ATTACH_VOUCHER:
        //TODO Attach Voucher action
      case COMPENSATION:
        //TODO Compensation action
      case SEND_MAIL:
        //TODO Send Mail action
      default:
    }

  }
}
