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
@JsonPropertyOrder({
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

  @Transient
  @JsonProperty("requiredEntities")
  public List<String> getRequiredEntities() {
    return getTemplateSlices()
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

  public Rule getRootRule() {
    if (rootRule == null) {
      rootRule = parseRulesXml(rulesXml);
    }
    return rootRule;
  }

  public int getPriority() {
    return priority;
  }

  public List<List<ResponseSlice>> getTemplateSlices() {
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
