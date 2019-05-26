
package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("ALL")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder( {
    "Fahrt nicht erfolgt",
    "Sonstiges",
    "Fahrer unfreundlich"
})
public class Typ {

  @JsonCreator
  public Typ(@JsonProperty("Fahrt nicht erfolgt") double fahrtNichtErfolgt,
             @JsonProperty("Sonstiges") int sonstiges,
             @JsonProperty("Fahrer unfreundlich") double fahrerUnfreundlich) {
    this.fahrtNichtErfolgt = fahrtNichtErfolgt;
    this.sonstiges = sonstiges;
    this.fahrerUnfreundlich = fahrerUnfreundlich;
  }

  private double fahrtNichtErfolgt;
  private int sonstiges;
  private double fahrerUnfreundlich;

  public double getFahrtNichtErfolgt() {
    return fahrtNichtErfolgt;
  }

  public void setFahrtNichtErfolgt(double fahrtNichtErfolgt) {
    this.fahrtNichtErfolgt = fahrtNichtErfolgt;
  }

  public int getSonstiges() {
    return sonstiges;
  }

  public void setSonstiges(int sonstiges) {
    this.sonstiges = sonstiges;
  }

  public double getFahrerUnfreundlich() {
    return fahrerUnfreundlich;
  }

  public void setFahrerUnfreundlich(double fahrerUnfreundlich) {
    this.fahrerUnfreundlich = fahrerUnfreundlich;
  }

}