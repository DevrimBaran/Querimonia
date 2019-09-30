package de.fraunhofer.iao.querimonia.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

  private static Logger log = LoggerFactory.getLogger(WebSocketController.class);

  @Autowired
  private SimpMessagingTemplate template;

  public void sendStateChange(String message) {
    if (template != null) {
      log.info("sending message " + message);
      template.convertAndSend("/complaints/state", message);
    }
  }

}
