package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "TempPipeline"
})
public class Pipelines {

    @JsonCreator
    public Pipelines(@JsonProperty("TempPipeline") List<TempPipeline> tempPipeline){
        this.tempPipeline = tempPipeline;
    }


    private List<TempPipeline> tempPipeline = null;

    public List<TempPipeline> getTempPipeline() {
        return tempPipeline;
    }

    public void setTempPipeline(List<TempPipeline> tempPipeline) {
        this.tempPipeline = tempPipeline;
    }

}