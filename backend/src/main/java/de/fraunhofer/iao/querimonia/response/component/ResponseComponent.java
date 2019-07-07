package de.fraunhofer.iao.querimonia.response.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.response.rules.Rule;
import de.fraunhofer.iao.querimonia.response.rules.RuleParser;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This represents a component of the response, which can have a list of alternative texts and
 * rules, when this component should be used.
 */
@Entity
@JsonPropertyOrder(value = {
    "componentId",
    "name",
    "priority",
    "rulesXml",
    "requiredEntities",
    "templateTexts"
})
public class ResponseComponent {

  /**
   * The unique primary key of the component.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int componentId;

  public void setComponentId(int componentId) {
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

  public ResponseComponent(String componentName, List<String> templateTexts,
                           String rulesXml) {
    setComponentName(componentName);
    setTemplateTexts(templateTexts);
    setRulesXml(rulesXml);
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
  @JsonProperty("requiredEntities")
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
  public List<List<ResponseSlice>> getResponseSlices() {
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

  public ResponseComponent setRulesXml(String rulesXml) {
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
}
