package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.rules.Rule;
import de.fraunhofer.iao.querimonia.response.rules.RuleParser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;
import tec.uom.lib.common.function.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class PersistentResponseComponent implements Identifiable<Long> {

  /**
   * The unique primary key of the component.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "componentId")
  @JsonProperty("id")
  private long id;

  /**
   * A unique identifier for components.
   */
  @Column(name = "name", nullable = false)
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
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "alternate_component_text_table",
                   joinColumns = @JoinColumn(name = "component_id"))
  @Column(length = 5000, nullable = false)
  @JsonProperty("texts")
  @NonNull
  private List<String> componentTexts = new ArrayList<>();

  /**
   * The list of actions.
   */
  @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SUBSELECT)
  @JoinColumn(name = "persisted_component_id")
  @Column(name = "action_id")
  @NonNull
  private List<Action> actions = new ArrayList<>();

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
  private final Rule rootRule;

  /**
   * The slices of each component text.
   */
  @Transient
  @JsonIgnore
  @NonNull
  private final List<List<ResponseSlice>> componentSlices;

  PersistentResponseComponent(long id,
                              @NonNull String componentName,
                              int priority,
                              @NonNull List<String> componentTexts,
                              @NonNull List<Action> actions,
                              @NonNull String rulesXml
  ) {
    this.id = id;
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
  PersistentResponseComponent(
      @NonNull
      @JsonProperty("name")
          String componentName,
      @JsonProperty("priority")
          int priority,
      @NonNull
      @JsonProperty(value = "texts", defaultValue = "[]")
          List<String> componentTexts,
      @NonNull
      @JsonProperty("rulesXml")
          String rulesXml,
      @NonNull
      @JsonProperty(value = "actions", defaultValue = "[]")
          List<Action> actions,
      @JsonProperty("id")
          long id,
      @NonNull
      @JsonProperty("requiredEntities")
          String[] requiredEntities
  ) {
    this(0, componentName, priority, componentTexts, actions, rulesXml);
  }

  @SuppressWarnings("unused")
  PersistentResponseComponent() {
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
    return createSlices()
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
    return id;
  }

  /**
   * Returns the name of the component. The name is used to give the user a way to identify
   * components.
   *
   * @return the name of the component.
   */
  @NonNull
  public String getComponentName() {
    return componentName;
  }

  /**
   * Returns the list of alternative texts of the component. This list contains alternative
   * variations of the text that this part of the response should have. In these texts,
   * placeholders are allowed like described in the wiki.
   *
   * @return the list of alternative texts of the component.
   */
  @NonNull
  public List<String> getComponentTexts() {
    return componentTexts;
  }

  /**
   * Returns the rules of the component in the xml format. This string must match the xsd schema
   * that is used for the rules. The rules define if and when this component may be used in the
   * response generation.
   *
   * @return the rules of the component in the xml format.
   */
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

  /**
   * Returns the priority of the component. Components with higher priority should be checked
   * first in the response generation. With different values of priority, the sequence of
   * components can be defined.
   *
   * @return the priority of the component.
   */
  public int getPriority() {
    return priority;
  }

  private Rule parseRulesXml(String rulesXml) {
    return RuleParser.parseRules(rulesXml);
  }

  private List<List<ResponseSlice>> createSlices() {
    return componentTexts.stream()
        .map(ResponseSlice::createSlices)
        .collect(Collectors.toList());
  }

  /**
   * Returns the actions of the component. The actions gets executed when a complaint that uses
   * this component gets closed.
   *
   * @return the actions of the component.
   */
  @NonNull
  public List<Action> getActions() {
    return actions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PersistentResponseComponent that = (PersistentResponseComponent) o;

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
        .append("componentId", id)
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
