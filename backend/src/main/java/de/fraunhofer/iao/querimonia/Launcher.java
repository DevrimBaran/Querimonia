package de.fraunhofer.iao.querimonia;

import de.fraunhofer.iao.querimonia.property.AnalyzerConfigProperties;
import de.fraunhofer.iao.querimonia.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * The main class of the backend, which starts the server.
 */
@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class, AnalyzerConfigProperties.class})
public class Launcher {

  public static void main(String[] args) {
    SpringApplication.run(Launcher.class, args);
  }
}
