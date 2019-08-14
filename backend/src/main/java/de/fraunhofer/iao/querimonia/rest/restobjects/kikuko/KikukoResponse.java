package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {
    "pipelines",
    "content"
})
public class KikukoResponse {

  private LinkedHashMap<String, List<FoundEntity>> pipelines;
  private String content;

  @SuppressWarnings("unused")
  @JsonCreator
  public KikukoResponse(@JsonProperty("pipelines")
                            LinkedHashMap<String, List<FoundEntity>> pipelines,
                        @JsonProperty("content")
                            String content) {
    this.pipelines = pipelines;
    this.content = content;
  }

  public LinkedHashMap<String, List<FoundEntity>> getPipelines() {
    return pipelines;
  }

  public void setPipelines(LinkedHashMap<String, List<FoundEntity>> pipelines) {
    this.pipelines = pipelines;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return MessageFormat.format("KikukoResponse'{'pipelines={0}, content=''{1}'''}'",
        pipelines, content);
  }
}