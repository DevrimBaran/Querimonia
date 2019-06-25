package de.fraunhofer.iao.querimonia.nlp.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@JsonPropertyOrder({"responsePartId", "alternatives", "component"})
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
