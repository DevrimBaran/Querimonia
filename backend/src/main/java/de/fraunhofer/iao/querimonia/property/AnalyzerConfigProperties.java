package de.fraunhofer.iao.querimonia.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This properties contain the currently used configuration id.
 */
@ConfigurationProperties(prefix = "config")
public class AnalyzerConfigProperties {

  private int id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
