package de.fraunhofer.iao.querimonia.response.action;

// TODO implement

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.response.rules.Rule;
import de.fraunhofer.iao.querimonia.response.rules.RuleParser;
import de.fraunhofer.iao.querimonia.response.rules.RuledInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@JsonPropertyOrder( {
    "actionId",
    "name",
    "actionCode",
    "rulesXml",
    "parameters"
})
public class Action implements RuledInterface {

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

  @JsonCreator
  public Action(@JsonProperty String name,
                @JsonProperty ActionCode actionCode,
                @JsonProperty String rulesXml,
                @JsonProperty HashMap<String, String> parameters) {
    this.name = name;
    this.actionCode = actionCode;
    this.rulesXml = rulesXml;
    this.parameters = parameters;
  }

  /**
   * Empty default constructor (only used for hibernate).
   */
  @SuppressWarnings("unused")
  public Action() {

  }

  public String getRulesXml() {
    return rulesXml;
  }

  @Override
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

  public Map<String, String> getParameters() {
    return parameters;
  }

  private Rule parseRulesXml(String rulesXml) {
    return RuleParser.parseRules(rulesXml);
  }

  public ResponseEntity<String> executeAction() {
    switch (actionCode) {
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
