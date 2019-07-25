package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@SuppressWarnings("ALL")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {
    "Pipeline"
})
public class Pipelines {

  private List<Pipeline> pipeline = null;


  @JsonCreator
  public Pipelines(@JsonProperty("Pipeline") List<Pipeline> pipeline) {
    this.pipeline = pipeline;
  }

  public List<Pipeline> getPipeline() {
    return pipeline;
  }

  public void setPipeline(List<Pipeline> pipeline) {
    this.pipeline = pipeline;
  }

}