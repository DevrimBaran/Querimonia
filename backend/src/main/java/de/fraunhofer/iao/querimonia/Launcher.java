package de.fraunhofer.iao.querimonia;

import de.fraunhofer.iao.querimonia.utility.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.CrossOrigin;
import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * The main class of the backend, which starts the server.
 */
@CrossOrigin(methods = {POST, PUT, PATCH, GET, DELETE})
@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class Launcher {

  public static void main(String[] args) {
    SpringApplication.run(Launcher.class, args);
  }
}
