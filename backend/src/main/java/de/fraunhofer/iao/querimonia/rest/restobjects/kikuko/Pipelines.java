package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@SuppressWarnings("ALL")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {
    "TempPipeline"
})
public class Pipelines {

  private List<TempPipeline> tempPipeline = null;


  @JsonCreator
  public Pipelines(@JsonProperty("TempPipeline") List<TempPipeline> tempPipeline) {
    this.tempPipeline = tempPipeline;
  }

  public List<TempPipeline> getTempPipeline() {
    return tempPipeline;
  }

  public void setTempPipeline(List<TempPipeline> tempPipeline) {
    this.tempPipeline = tempPipeline;
  }

}