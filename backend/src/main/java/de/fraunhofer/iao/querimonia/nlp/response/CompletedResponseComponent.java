package de.fraunhofer.iao.querimonia.nlp.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class CompletedResponseComponent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int completedResponseId;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<SingleCompletedComponent> responseComponents;

  @JsonCreator
  public CompletedResponseComponent(List<SingleCompletedComponent> responseComponents) {
    this.responseComponents = responseComponents;
  }

  @SuppressWarnings("unused")
  public CompletedResponseComponent() {
  }

  @JsonProperty
  public List<SingleCompletedComponent> getResponseComponents() {
    return responseComponents;
  }
}
