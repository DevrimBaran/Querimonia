package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.text.MessageFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pipelines",
    "content"
})
public class KikukoResponse {

  private Pipelines pipelines;
  private String content;

  @JsonCreator
  public KikukoResponse(@JsonProperty("pipelines") Pipelines pipelines,
                        @JsonProperty("content") String content) {
    this.pipelines = pipelines;
    this.content = content;
  }

  public Pipelines getPipelines() {
    return pipelines;
  }

  public void setPipelines(Pipelines pipelines) {
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