package de.fraunhofer.iao.querimonia.response.generation;

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
import java.util.List;

/**
 * This class represents a response component that was already filled out using the entities of a
 * certain complaint.
 */
@Entity
@JsonPropertyOrder(value = {
    "id",
    "completedText",
    "entities",
    "component"
})
public class SingleCompletedComponent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonProperty("id")
  private int completedComponentId;

  @Column(length = 5000)
  private String completedText;

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
      @JsonProperty List<NamedEntity> entities) {
    this.completedText = completedText;
    this.entities = entities;
  }

  @SuppressWarnings("unused")
  public SingleCompletedComponent() {

  }

  public String getCompletedText() {
    return completedText;
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

  public int getCompletedComponentId() {
    return completedComponentId;
  }

}
