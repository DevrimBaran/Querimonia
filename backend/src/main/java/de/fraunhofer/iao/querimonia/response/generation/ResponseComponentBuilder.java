package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.response.action.Action;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResponseComponentBuilder {
  @NonNull
  private String componentName = "";
  @NonNull
  private List<String> componentTexts = new ArrayList<>();
  @NonNull
  private String rulesXml = "";
  @NonNull
  private List<Action> actions = new ArrayList<>();
  private long id;
  private int priority;

  public ResponseComponentBuilder setComponentName(@NonNull String componentName) {
    this.componentName = componentName;
    return this;
  }

  public ResponseComponentBuilder setComponentTexts(@NonNull List<String> componentTexts) {
    this.componentTexts = componentTexts;
    return this;
  }

  public ResponseComponentBuilder setRulesXml(@NonNull String rulesXml) {
    this.rulesXml = rulesXml;
    return this;
  }

  public ResponseComponentBuilder setActions(@NonNull List<Action> actions) {
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
    return new ResponseComponent(id, Objects.requireNonNull(componentName), priority,
        componentTexts, Objects.requireNonNull(actions), Objects.requireNonNull(rulesXml));
  }
}