package de.fraunhofer.iao.querimonia;

import de.fraunhofer.iao.querimonia.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * The main class of the backend, which starts the server.
 */
@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class Launcher {

    public static void main(String[] args) {
        setupDatabase();
        SpringApplication.run(Launcher.class, args);
    }

    private static void setupDatabase() {

    }
}
