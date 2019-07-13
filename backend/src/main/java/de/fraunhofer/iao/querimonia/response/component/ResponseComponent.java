package de.fraunhofer.iao.querimonia.response.component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.rules.Rule;
import de.fraunhofer.iao.querimonia.response.rules.RuleParser;
import de.fraunhofer.iao.querimonia.response.rules.RuledInterface;

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
    "templateTexts",
    "actions"
})
public class ResponseComponent implements RuledInterface {

  /**
   * The unique primary key of the component.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonProperty("id")
  private long componentId;

  public void setComponentId(long componentId) {
    this.componentId = componentId;
  }

  /**
   * A unique identifier for components.
   */
  @Column(name = "name", unique = true)
  private String componentName;

  /**
   * Components with high priority get used first in the response generation.
   */
  @Column
  private int priority;

  /**
   * The component texts for the component.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "template_text_table",
                   joinColumns = @JoinColumn(name = "component_id"))
  @Column(length = 5000)
  private List<String> templateTexts;

  @OneToMany(cascade = CascadeType.ALL)
  private List<Action> actions;

  /**
   * The rules in xml format.
   */
  @Column(length = 5000)
  private String rulesXml;

  /**
   * The root rule, gets parsed from xml.
   */
  @Transient
  @JsonIgnore
  private Rule rootRule;

  /**
   * The slices of each component text.
   */
  @Transient
  @JsonIgnore
  private List<List<ResponseSlice>> templateSlices;

  @JsonCreator
  public ResponseComponent(@JsonProperty String componentName,
                           @JsonProperty List<String> templateTexts,
                           @JsonProperty String rulesXml) {
    setComponentName(componentName);
    setTemplateTexts(templateTexts);
    setRulesXml(rulesXml);
  }

  @JsonCreator
  public ResponseComponent(@JsonProperty String componentName,
                           @JsonProperty List<String> templateTexts,
                           @JsonProperty String rulesXml,
                          @JsonProperty List<String> requiredEntities) {
    // work around to allow json creation with required entities property
    this(componentName, templateTexts, rulesXml);
  }

  public ResponseComponent() {
    // required for hibernate
  }

  /**
   * Returns the all placeholder/variable names that are required to fill out this response
   * component.
   *
   * @return a list of all required placeholders, that occur in the texts of this response
   * component.
   */
  @Transient
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

  public long getComponentId() {
    return componentId;
  }

  public String getComponentName() {
    return componentName;
  }

  public List<String> getTemplateTexts() {
    return templateTexts;
  }

  public String getRulesXml() {
    return rulesXml;
  }

  /**
   * Returns the rule of the response component. A response component can only be used, when this
   * rule is respected.
   *
   * @return the rule of the component.
   */
  @Override
  public Rule getRootRule() {
    if (rootRule == null) {
      rootRule = parseRulesXml(rulesXml);
    }
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
  private List<List<ResponseSlice>> getResponseSlices() {
    if (templateSlices == null) {
      templateSlices = createSlices();
    }
    return templateSlices;
  }

  public ResponseComponent setComponentName(String componentName) {
    this.componentName = componentName;
    return this;
  }

  public ResponseComponent setTemplateTexts(List<String> templateTexts) {
    this.templateTexts = templateTexts;
    this.templateSlices = createSlices();
    return this;
  }

  private ResponseComponent setRulesXml(String rulesXml) {
    this.rulesXml = rulesXml;
    rootRule = parseRulesXml(rulesXml);
    return this;
  }

  public ResponseComponent setPriority(int priority) {
    this.priority = priority;
    return this;
  }

  private Rule parseRulesXml(String rulesXml) {
    return RuleParser.parseRules(rulesXml);
  }

  private List<List<ResponseSlice>> createSlices() {
    return templateTexts.stream()
        .map(ResponseSlice::createSlices)
        .collect(Collectors.toList());
  }

  public List<Action> getActions() {
    return actions;
  }
}