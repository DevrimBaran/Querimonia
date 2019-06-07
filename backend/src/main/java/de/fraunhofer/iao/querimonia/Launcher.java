package de.fraunhofer.iao.querimonia;

import de.fraunhofer.iao.querimonia.property.FileStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.File;

/**
 * The main class of the backend, which starts the server.
 */
@SpringBootApplication
@EnableConfigurationProperties({
    FileStorageProperties.class
})
public class Launcher {

  public static void main(String[] args) {
    SpringApplication.run(Launcher.class, args);
  }
}
