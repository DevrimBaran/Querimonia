package de.fraunhofer.iao.querimonia.response.generation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.fraunhofer.iao.querimonia.response.component.ResponseComponent;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * A completed response component is a {@link ResponseComponent} where all the placeholders were
 * replaced by the actual content of the entities in the complaint.
 */
@Entity
@JsonPropertyOrder(value = {"responsePartId", "alternatives", "component"})
public class CompletedResponseComponent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int responsePartId;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<SingleCompletedComponent> alternatives;

  @JoinColumn(name = "component_id")
  @ManyToOne
  private ResponseComponent component;

  @JsonCreator
  public CompletedResponseComponent(List<SingleCompletedComponent> alternatives,
                                    ResponseComponent component) {
    this.alternatives = alternatives;
    this.component = component;
  }

  @SuppressWarnings("unused")
  public CompletedResponseComponent() {
  }

  public List<SingleCompletedComponent> getAlternatives() {
    return alternatives;
  }

  public int getResponsePartId() {
    return responsePartId;
  }

  public ResponseComponent getComponent() {
    return component;
  }
}
