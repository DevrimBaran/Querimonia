package de.fraunhofer.iao.querimonia.response.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.response.rules.Rule;
import de.fraunhofer.iao.querimonia.response.rules.RuleParser;
import de.fraunhofer.iao.querimonia.response.rules.RuledInterface;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

@Entity
@JsonPropertyOrder(value = {
    "name",
    "actionCode",
    "rulesXml",
    "parameters"
})
public class Action implements RuledInterface {

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
   * The rules in xml format.
   */
  @Column(length = 5000, nullable = false)
  @NonNull
  private String rulesXml = "";

  /**
   * Here are parameters for the Action to be executed.
   */
  @Column()
  @NonNull
  private HashMap<String, String> parameters = new HashMap<>();

  /**
   * The root rule, gets parsed from xml.
   */
  @Transient
  @JsonIgnore
  private Rule rootRule;

  @JsonCreator
  @SuppressWarnings("unused")
  public Action(@NonNull @JsonProperty String name,
                @NonNull @JsonProperty ActionCode actionCode,
                @NonNull @JsonProperty String rulesXml,
                @NonNull @JsonProperty HashMap<String, String> parameters) {
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

  @NonNull
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

  private Rule parseRulesXml(String rulesXml) {
    return RuleParser.parseRules(rulesXml);
  }

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
