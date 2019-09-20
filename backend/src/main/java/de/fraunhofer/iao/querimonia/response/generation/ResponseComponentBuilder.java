package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.response.action.Action;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder class for {@link ResponseComponent response components}.
 */
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

  /**
   * Creates a new response component builder without any attributes set.
   */
  public ResponseComponentBuilder() {
    // empty
  }

  /**
   * Creates a response component builder out of a response component. The builder has the same
   * attribute values as the given response component.
   *
   * @param responseComponent the response component which should be copied.
   */
  public ResponseComponentBuilder(ResponseComponent responseComponent) {
    this.componentName = responseComponent.getComponentName();
    this.componentTexts = new ArrayList<>(responseComponent.getComponentTexts());
    this.rulesXml = responseComponent.getRulesXml();
    this.actions = new ArrayList<>(responseComponent.getActions());
    this.id = responseComponent.getId();
    this.priority = responseComponent.getPriority();
  }

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

  /**
   * Creates a new response component with the attributes of this builder.
   *
   * @return the new response component.
   */
  public ResponseComponent createResponseComponent() {
    return new ResponseComponent(id, Objects.requireNonNull(componentName), priority,
        componentTexts, Objects.requireNonNull(actions), Objects.requireNonNull(rulesXml));
  }
}