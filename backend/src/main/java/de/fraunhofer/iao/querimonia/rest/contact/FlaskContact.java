package de.fraunhofer.iao.querimonia.rest.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * Class for the communication with the python server.
 */
public class FlaskContact {

  private static final String URL = "https://querimonia.iao.fraunhofer.de/python/";

  /**
   * Receive an answer from the flask server.
   *
   * @param body the body for the request.
   * @param path the path of the request.
   *
   * @return the map of the response.
   */
  public static Map<String, Double> receiveJson(JSONObject body, String path) {
    HttpHeaders header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>(body.toString(), header);
    String response;

    RestTemplate template = new RestTemplateBuilder()
        .basicAuthentication("admin", "QuerimoniaPass2019")
        .build();
    try {
      response = template
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
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Flask Fehler bei der "
          + "Analyse", e, "Flask Fehler");
    }

    return map;
  }
}
