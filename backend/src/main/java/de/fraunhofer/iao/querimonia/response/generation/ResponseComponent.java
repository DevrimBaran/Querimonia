package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.rules.Rule;
import de.fraunhofer.iao.querimonia.response.rules.RuleParser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
import java.util.ArrayList;
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
public class ResponseComponent implements Identifiable<Long> {

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
  @JsonProperty("name")
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
  @JsonProperty("texts")
  @NonNull
  private List<String> componentTexts = new ArrayList<>();

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
  @NonNull
  private Rule rootRule;

  /**
   * The slices of each component text.
   */
  @Transient
  @JsonIgnore
  @NonNull
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
  public ResponseComponent(@JsonProperty("name") String componentName,
                           @JsonProperty("priority") int priority,
                           @JsonProperty(value = "texts", defaultValue = "[]")
                               List<String> componentTexts,
                           @JsonProperty("rulesXml") String rulesXml,
                           @JsonProperty(value = "actions", defaultValue = "[]")
                               List<Action> actions) {
    this(0, componentName, priority, componentTexts, actions, rulesXml);
  }

  @SuppressWarnings("unused")
  private ResponseComponent() {
    // required for hibernate
    this.componentSlices = createSlices();
    this.rootRule = parseRulesXml(this.rulesXml);
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
        .distinct()
        .collect(Collectors.toList());
  }

  @JsonProperty("id")
  @Override
  public Long getId() {
    return componentId;
  }

  public ResponseComponent withId(long componentId) {
    return new ResponseComponentBuilder(this)
        .setId(componentId)
        .createResponseComponent();
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
  public String getRulesXml() {
    return rulesXml;
  }

  /**
   * Returns the rule of the response component. A response component can only be used, when this
   * rule is respected.
   *
   * @return the rule of the component.
   */
  @JsonIgnore
  @NonNull
  public Rule getRootRule() {
    return rootRule;
  }

  public int getPriority() {
    return priority;
  }

  /**
   * Returns all response slices of the component.
   */
  @JsonIgnore
  @Transient
  @NonNull
  private List<List<ResponseSlice>> getResponseSlices() {
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

  public void setID(long id) {
    this.componentId = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ResponseComponent that = (ResponseComponent) o;

    return new EqualsBuilder()
        .append(priority, that.priority)
        .append(componentName, that.componentName)
        .append(componentTexts, that.componentTexts)
        .append(actions, that.actions)
        .append(rulesXml, that.rulesXml)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(componentName)
        .append(priority)
        .append(componentTexts)
        .append(actions)
        .append(rulesXml)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("componentId", componentId)
        .append("componentName", componentName)
        .append("priority", priority)
        .append("componentTexts", componentTexts)
        .append("actions", actions)
        .append("rulesXml", rulesXml)
        .append("rootRule", rootRule)
        .append("componentSlices", componentSlices)
        .toString();
  }
}
