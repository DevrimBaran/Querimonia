package de.fraunhofer.iao.querimonia.nlp.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * This class represents a response component that was already filled out using the entities of a
 * certain complaint.
 */
@Entity
@JsonPropertyOrder({
                        "completedComponentId",
                        "completedText",
                        "entities",
                        "component"
                    })
public class SingleCompletedComponent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int completedComponentId;

  @Column(length = 5000)
  private String completedText;

  @JoinColumn(name = "component_id")
  @ManyToOne
  private ResponseComponent component;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "response_entity_table",
                   joinColumns = @JoinColumn(name = "completed_component_id"))
  private List<NamedEntity> entities;

  /**
   * Basic constructor for JSON serialization.
   */
  @JsonCreator
  public SingleCompletedComponent(
      @JsonProperty String completedText,
      @JsonProperty ResponseComponent component,
      @JsonProperty List<NamedEntity> entities) {
    this.completedText = completedText;
    this.component = component;
    this.entities = entities;
  }

  @SuppressWarnings("unused")
  public SingleCompletedComponent() {

  }

  public String getCompletedText() {
    return completedText;
  }

  public ResponseComponent getComponent() {
    return component;
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

  public int getCompletedComponentId() {
    return completedComponentId;
  }

}
