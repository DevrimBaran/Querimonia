package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.rules.Rule;
import de.fraunhofer.iao.querimonia.response.rules.RuleParser;
import de.fraunhofer.iao.querimonia.response.rules.RuledInterface;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import tec.uom.lib.common.function.Identifiable;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This represents a component of the response, which can have a list of alternative texts and
 * rules, when this component should be used.
 */
@Entity
@JsonPropertyOrder(value = {
    "id",
    "name",
    "priority",
    "rulesXml",
    "requiredEntities",
    "componentTexts",
    "actions"
})
public class ResponseComponent implements RuledInterface, Identifiable<Long> {

  /**
   * The unique primary key of the component.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private long componentId;

  /**
   * A unique identifier for components.
   */
  @Column(name = "name", unique = true, nullable = false)
  @NonNull
  private String componentName = "";

  /**
   * Components with high priority get used first in the response generation.
   */
  @Column(nullable = false)
  private int priority;

  /**
   * The component texts for the component.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "component_text_table",
                   joinColumns = @JoinColumn(name = "component_id"))
  @Column(length = 5000, nullable = false)
  @NonNull
  private List<String> componentTexts = List.of();

  /**
   * The list of actions.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "component_id")
  @Column(name = "action_id")
  @NonNull
  private List<Action> actions = List.of();

  /**
   * The rules in xml format.
   */
  @Column(length = 5000, nullable = false)
  @NonNull
  private String rulesXml = "";

  /**
   * The root rule, gets parsed from xml.
   */
  @Transient
  @JsonIgnore
  @Nullable
  private Rule rootRule;

  /**
   * The slices of each component text.
   */
  @Transient
  @JsonIgnore
  @Nullable
  private List<List<ResponseSlice>> componentSlices;

  // constructor for builder
  ResponseComponent(long id,
                    @NonNull String componentName,
                    int priority,
                    @NonNull List<String> componentTexts,
                    @NonNull List<Action> actions,
                    @NonNull String rulesXml
  ) {
    this.componentId = id;
    this.componentName = componentName;
    this.priority = priority;
    this.componentTexts = componentTexts;
    this.actions = actions;
    this.rulesXml = rulesXml;
    this.rootRule = parseRulesXml(rulesXml);
    this.componentSlices = createSlices();
  }

  @SuppressWarnings("unused")
  @JsonCreator
  public ResponseComponent(@JsonProperty() String componentName,
                           @JsonProperty() int priority,
                           @JsonProperty(defaultValue = "[]") List<String> componentTexts,
                           @JsonProperty() String rulesXml,
                           @JsonProperty(defaultValue = "[]") List<Action> actions,
                           @JsonProperty(defaultValue = "[]") List<String> requiredEntities) {
    // work around to allow json creation with required entities property
    this(0, componentName, priority, componentTexts, actions, rulesXml);
  }

  public ResponseComponent() {
    // required for hibernate
  }

  /**
   * Returns the all placeholder/variable names that are required to fill out this response
   * component.
   *
   * @return a list of all required placeholders, that occur in the texts of this response
   *     component.
   */
  @Transient
  @NonNull
  @JsonProperty(value = "requiredEntities")
  public List<String> getRequiredEntities() {
    return getResponseSlices()
        .stream()
        // get all slices
        .flatMap(List::stream)
        .filter(ResponseSlice::isPlaceholder)
        // map the placeholders to their identifiers
        .map(ResponseSlice::getContent)
        .collect(Collectors.toList());
  }

  @JsonProperty("id")
  @Override
  public Long getId() {
    return componentId;
  }

  public void setComponentId(long componentId) {
    this.componentId = componentId;
  }

  @NonNull
  public String getComponentName() {
    return componentName;
  }

  @NonNull
  public List<String> getComponentTexts() {
    return componentTexts;
  }

  @NonNull
  public ResponseComponent setComponentTexts(@NonNull List<String> componentTexts) {
    this.componentTexts = componentTexts;
    this.componentSlices = createSlices();
    return this;
  }

  @NonNull
  public String getRulesXml() {
    return rulesXml;
  }

  @NonNull
  private ResponseComponent setRulesXml(String rulesXml) {
    this.rulesXml = rulesXml;
    rootRule = parseRulesXml(rulesXml);
    return this;
  }

  /**
   * Returns the rule of the response component. A response component can only be used, when this
   * rule is respected.
   *
   * @return the rule of the component.
   */
  @Override
  @JsonIgnore
  @NonNull
  public Rule getRootRule() {
    if (rootRule == null) {
      rootRule = parseRulesXml(rulesXml);
    }
    return rootRule;
  }

  public int getPriority() {
    return priority;
  }

  @NonNull
  public ResponseComponent setPriority(int priority) {
    this.priority = priority;
    return this;
  }

  /**
   * Returns all response slices of the component.
   */
  @JsonIgnore
  @Transient
  @NonNull
  private List<List<ResponseSlice>> getResponseSlices() {
    if (componentSlices == null) {
      componentSlices = createSlices();
    }
    return componentSlices;
  }

  private Rule parseRulesXml(String rulesXml) {
    return RuleParser.parseRules(rulesXml);
  }

  private List<List<ResponseSlice>> createSlices() {
    return componentTexts.stream()
        .map(ResponseSlice::createSlices)
        .collect(Collectors.toList());
  }

  @NonNull
  public List<Action> getActions() {
    return actions;
  }

  @NonNull
  public ResponseComponent setActions(
      List<Action> actions) {
    this.actions = actions;
    return this;
  }
}
