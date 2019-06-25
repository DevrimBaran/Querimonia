package de.fraunhofer.iao.querimonia.rest.restcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * This class does analysation functions of texts.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class AnalyseController {
    private static final Logger logger = LoggerFactory.getLogger(ComplaintController.class);

    /**
     * This function calculates the nearest neighbour, addirtions and subtractions of wordvectors.
     */
    @PostMapping("api/analyse/vectors")
    public void vectorController() {
        try {
            Runtime.getRuntime().exec("skript.py");
        } catch (IOException e) {
            logger.error("No such file found.");
        }
    }
}
