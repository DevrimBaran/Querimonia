package de.fraunhofer.iao.querimonia.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This properties contain the currently used configuration id.
 */
@ConfigurationProperties(prefix = "config")
public class AnalyzerConfigProperties {

  private long id;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
