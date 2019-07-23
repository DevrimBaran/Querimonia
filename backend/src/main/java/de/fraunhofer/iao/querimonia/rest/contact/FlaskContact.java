package de.fraunhofer.iao.querimonia.rest.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FlaskContact {

  private static final String URL = "https://querimonia.iao.fraunhofer.de/python/";

  // TODO add javadoc comment
  public static Map<String, Double> receiveJson(JSONObject body, String path) {
    HttpHeaders header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>(body.toString(), header);
    String response;
    try {
      response = new RestTemplate()
          .exchange(URL + path, HttpMethod.POST, request, String.class).getBody();
    } catch (RestClientException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Flask-Server konnte nicht"
          + " erreicht werden", e, "Flask-Server nicht erreichbar");
    }


    ObjectMapper mapper = new ObjectMapper();
    Map<String, Double> map;
    try {
      //noinspection unchecked
      map = mapper.readValue(response, Map.class);
    } catch (IOException e) {
      e.printStackTrace();
      map = new HashMap<>();
    }

    return map;
  }
}
