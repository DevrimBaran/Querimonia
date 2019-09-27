package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
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
  @Column(name = "id")
  private long id;

  @JoinColumn(name = "component_id")
  @ManyToOne(cascade = CascadeType.ALL)
  @NonNull
  private PersistentResponseComponent component = new ResponseComponent().toPersistableComponent();

  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "component_id")
  @NonNull
  private List<NamedEntity> entities = new ArrayList<>();

  private boolean used = false;

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
          PersistentResponseComponent component,
      @NonNull
      @JsonProperty("entities")
          List<NamedEntity> entities,
      @JsonProperty("used")
          boolean used
  ) {
    this.entities = entities;
    this.component = component;
    this.used = used;
  }

  public CompletedResponseComponent(
      @NonNull PersistentResponseComponent component,
      @NonNull List<NamedEntity> entities) {
    this.component = component;
    this.entities = entities;
    this.used = false;
  }

  private CompletedResponseComponent(
      long id,
      @NonNull PersistentResponseComponent component,
      @NonNull List<NamedEntity> entities,
      boolean used) {
    this.id = id;
    this.component = component;
    this.entities = entities;
    this.used = used;
  }

  @SuppressWarnings("unused")
  public CompletedResponseComponent() {
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
  public PersistentResponseComponent getComponent() {
    return component;
  }

  @Override
  public Long getId() {
    return id;
  }

  /**
   * This is true, if the component is already used in the response text.
   *
   * @return true, if the component is used in the response text, else false.
   */
  public boolean isUsed() {
    return used;
  }

  public CompletedResponseComponent withUsed(boolean used) {
    return new CompletedResponseComponent(id, component, entities, used);
  }
}
