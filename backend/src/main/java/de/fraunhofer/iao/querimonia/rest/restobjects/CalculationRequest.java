package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * simple wrapper vor Requests for vector calculations.
 */
public class CalculationRequest {
  private final String vector1;
  private final String operator1;
  private final String vector2;
  private final String corpus;

  private final String operator2;
  private final String vector3;

  @JsonCreator
  public CalculationRequest(@JsonProperty String vector1, @JsonProperty String operator1,
                            @JsonProperty String vector2, @JsonProperty String corpus,
                            @JsonProperty(defaultValue = " ") String operator2,
                            @JsonProperty(defaultValue = " ") String vector3) {
    this.vector1 = vector1;
    this.operator1 = operator1;
    this.vector2 = vector2;
    this.corpus = corpus;
    this.operator2 = operator2;
    this.vector3 = vector3;
  }

  public String getVector1() {
    return vector1;
  }

  public String getOperator1() {
    return operator1;
  }

  public String getVector2() {
    return vector2;
  }

  public String getCorpus() {
    return corpus;
  }

  public String getOperator2() {
    return operator2;
  }

  public String getVector3() {
    return vector3;
  }
}