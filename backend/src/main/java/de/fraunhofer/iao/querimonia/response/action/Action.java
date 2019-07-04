package de.fraunhofer.iao.querimonia.response.action;

// TODO implement

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.response.rules.Rule;
import de.fraunhofer.iao.querimonia.response.rules.RuleParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.util.HashMap;

@Entity
@JsonPropertyOrder( {
    "actionId",
    "name",
    "actionCode",
    "rulesXml",
    "parameters"
})
public class Action {

  /**
   * The unique primary key of the action
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int actionId;

  public void setActionId(int actionId) {
    this.actionId = actionId;
  }

  /**
   * A unique identifier for the actions to display
   */
  @Column(name = "name", unique = true)
  private String name;

  /**
   * The action that could be executed
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "actionCode")
  private ActionCode actionCode;


  /**
   * The rules in xml format.
   */
  @Column(length = 5000)
  private String rulesXml;

  /**
   * Here are parameters for the Action to be executed
   */
  @Column
  private HashMap<String, String> parameters;

  /**
   * The root rule, gets parsed from xml.
   */
  @Transient
  @JsonIgnore
  private Rule rootRule;

  public String getRulesXml() {
    return rulesXml;
  }

  public Rule getRootRule() {
    if (rootRule == null) {
      rootRule = parseRulesXml(rulesXml);
    }
    return rootRule;
  }

  public int getActionId() {
    return actionId;
  }

  public String getName() {
    return name;
  }

  public ActionCode getActionCode() {
    return actionCode;
  }

  public HashMap<String, String> getParameters() {
    return parameters;
  }

  private Rule parseRulesXml(String rulesXml) {
    return RuleParser.parseRules(rulesXml);
  }

  public ResponseEntity<String> executeAction(){
    switch (actionCode){
      case ATTACH_VOUCHER:
        //TODO Attach Voucher action
      case COMPENSATION:
        //TODO Compensation action
      case SEND_MAIL:
        //TODO Send Mail action
      default:
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

  }
}
