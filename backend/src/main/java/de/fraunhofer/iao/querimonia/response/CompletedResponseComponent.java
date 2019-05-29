package de.fraunhofer.iao.querimonia.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;

import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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
public class CompletedResponseComponent {

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
  public CompletedResponseComponent(
      @JsonProperty String completedText,
      @JsonProperty ResponseComponent component,
      @JsonProperty List<NamedEntity> entities) {
    this.completedText = completedText;
    this.component = component;
    this.entities = entities;
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

  public CompletedResponseComponent() {

  }
}
