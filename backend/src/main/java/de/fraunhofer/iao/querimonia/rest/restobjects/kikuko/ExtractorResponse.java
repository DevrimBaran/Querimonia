package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.MessageFormat;

public class ExtractorResponse {


  private ExtractorPipelines pipelines;
  private String content;

  @SuppressWarnings("unused")
  @JsonCreator
  public ExtractorResponse(@JsonProperty("pipelines") ExtractorPipelines pipelines,
                           @JsonProperty("content") String content) {
    this.pipelines = pipelines;
    this.content = content;
  }

  public ExtractorPipelines getPipelines() {
    return pipelines;
  }

  public void setPipelines(ExtractorPipelines pipelines) {
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
