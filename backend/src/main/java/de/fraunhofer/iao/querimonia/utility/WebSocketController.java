package de.fraunhofer.iao.querimonia.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebSocketController {

  @Autowired
  SimpMessagingTemplate template;

  public void stateChange(String message) {
    template.convertAndSend("", message);
  }

}
