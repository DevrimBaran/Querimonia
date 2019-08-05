package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import tec.uom.lib.common.function.Identifiable;

import javax.persistence.*;
import java.util.List;

/**
 * A completed response component is a {@link ResponseComponent} where all the placeholders were
 * replaced by the actual content of the entities in the complaint.
 */
@Entity
@JsonPropertyOrder(value = {"id", "component"})
public class CompletedResponseComponent implements Identifiable<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  @Column(name = "id")
  private long responsePartId;

  @JoinColumn(name = "component_id")
  @ManyToOne(cascade = CascadeType.MERGE)
  private ResponseComponent component;

  @OneToMany(cascade = CascadeType.MERGE)
  @JoinColumn(name = "component_id")
  private List<NamedEntity> entities;

  @JsonCreator
  public CompletedResponseComponent(ResponseComponent component, List<NamedEntity> entities) {
    this.entities = entities;
    this.component = component;
  }

  @SuppressWarnings("unused")
  public CompletedResponseComponent() {
  }

  public List<NamedEntity> getEntities() {
    return entities;
  }

  public long getResponsePartId() {
    return responsePartId;
  }

  public ResponseComponent getComponent() {
    return component;
  }

  @Override
  public Long getId() {
    return responsePartId;
  }
}
