package de.fraunhofer.iao.querimonia.rest.restcontroller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * Only for debugging ignore.,
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class TestController {

  @GetMapping("/api/test")
  public String test() {
    return new File("").getAbsolutePath();
  }
}
