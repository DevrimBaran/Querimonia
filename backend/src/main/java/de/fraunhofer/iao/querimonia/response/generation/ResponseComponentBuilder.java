package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.response.action.Action;

import java.util.List;

public class ResponseComponentBuilder {
  private String componentName;
  private List<String> componentTexts;
  private String rulesXml;
  private List<Action> actions;
  private long id;
  private int priority;

  public ResponseComponentBuilder setComponentName(String componentName) {
    this.componentName = componentName;
    return this;
  }

  public ResponseComponentBuilder setComponentTexts(List<String> componentTexts) {
    this.componentTexts = componentTexts;
    return this;
  }

  public ResponseComponentBuilder setRulesXml(String rulesXml) {
    this.rulesXml = rulesXml;
    return this;
  }

  public ResponseComponentBuilder setActions(List<Action> actions) {
    this.actions = actions;
    return this;
  }

  public ResponseComponentBuilder setId(long id) {
    this.id = id;
    return this;
  }

  public ResponseComponentBuilder setPriority(int priority) {
    this.priority = priority;
    return this;
  }

  public ResponseComponent createResponseComponent() {
    return new ResponseComponent(id, componentName, priority, componentTexts, actions, rulesXml);
  }
}