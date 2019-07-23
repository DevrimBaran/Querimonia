package de.fraunhofer.iao.querimonia.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Extractors displays all extractors by tool, pipeline, and domain.
 */

@JsonPropertyOrder(value = {
        "tool",
        "pipeline",
        "domain"
})
public class Extractors {

  private String[] tool;
  private String[] pipeline;
  private String[] domain;

  @JsonCreator
  public Extractors(String[] tool, String[] pipeline, String[] domain) {
    this.tool = tool;
    this.pipeline = pipeline;
    this.domain = domain;
  }

  @SuppressWarnings("unused")
  private Extractors() {
    // for hibernate
  }

  public String[] getTool() {
    return tool;
  }

  public Extractors setTool(String[] tool) {
    this.tool = tool;
    return this;
  }

  public String[] getPipeline() {
    return pipeline;
  }

  public Extractors setPipeline(String[] pipeline) {
    this.pipeline = pipeline;
    return this;
  }

  public String[] getDomain() {
    return domain;
  }

  public Extractors setDomain(String[] domain) {
    this.domain = domain;
    return this;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("tool", tool)
        .append("pipeline", pipeline)
        .append("domain", domain)
        .toString();
  }
}
