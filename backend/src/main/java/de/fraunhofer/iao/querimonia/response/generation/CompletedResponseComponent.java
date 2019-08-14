package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.springframework.lang.NonNull;
import tec.uom.lib.common.function.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A completed response component is a part of the response to the complaint. It consists of the
 * generic {@link ResponseComponent} and the list of (complaint specific) entities that should be
 * used as the placeholders.
 */
@Entity
@JsonPropertyOrder(value = {"id", "component", "entities"})
public class CompletedResponseComponent implements Identifiable<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  @Column(name = "id")
  private long id;

  @JoinColumn(name = "component_id")
  @ManyToOne(cascade = CascadeType.MERGE)
  @NonNull
  private ResponseComponent component = new ResponseComponent();

  @OneToMany(cascade = CascadeType.MERGE)
  @JoinColumn(name = "component_id")
  @NonNull
  private List<NamedEntity> entities = new ArrayList<>();

  /**
   * Creates a new completed response component. Can also be used for JSON creation.
   *
   * @param component the generic response component.
   * @param entities  the entities of the complaint.
   */
  @JsonCreator
  public CompletedResponseComponent(
      @NonNull
      @JsonProperty("component")
          ResponseComponent component,
      @NonNull
      @JsonProperty("entities")
          List<NamedEntity> entities
  ) {
    this.entities = entities;
    this.component = component;
  }

  @SuppressWarnings("unused")
  private CompletedResponseComponent() {
    // for hibernate
  }

  /**
   * Returns the entities that should be used to fill the placeholders in the component.
   *
   * @return the entities that should be used to fill the placeholders in the component.
   */
  @NonNull
  public List<NamedEntity> getEntities() {
    return entities;
  }

  /**
   * Returns the component that is used in this completed component.
   *
   * @return the component that is used in this completed component.
   */
  @NonNull
  public ResponseComponent getComponent() {
    return component;
  }

  @Override
  public Long getId() {
    return id;
  }
}
