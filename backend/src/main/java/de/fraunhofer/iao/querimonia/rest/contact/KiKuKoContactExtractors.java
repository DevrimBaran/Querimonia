package de.fraunhofer.iao.querimonia.rest.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class KiKuKoContactExtractors {

  private static final String URL =
      "https://kikuko.iao.fraunhofer.de/request?url=getAllDomainType&data[type]=";

  /**
   * executes a Request to KiKuKo.
   *
   * @param type type of the extractors
   *
   * @return Array of all KiKuKo extractors of the given type
   */
  public String[] executeKikukoRequest(String type) {

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

    HttpEntity<MultiValueMap> request = new HttpEntity<>(map);

    // add authentication token
    RestTemplate template = new RestTemplateBuilder()
        .basicAuthentication("admin", "KIKuKoPass2018")
        .build();

    // exception for illegal answers
    QuerimoniaException kikukoException =
        new QuerimoniaException(HttpStatus.CONFLICT,
            "Keine Antwort von KIKiKo verfügbar", "KIKuKo nicht "
            + "erreichbar");

    // get response
    String returnValue;
    try {
      returnValue = template.exchange(URL + type, HttpMethod.GET,
          request, String.class).getBody();
    } catch (RestClientException e) {
      throw kikukoException;
    }

    ObjectMapper mapper = new ObjectMapper();
    String[] extractorArray;
    try {
      String extractorString = (String) new JSONObject(returnValue).get("body");
      extractorArray = mapper.readValue(extractorString, String[].class);
    } catch (IOException | JSONException e) {
      e.printStackTrace();
      extractorArray = new String[0];
    }

    return extractorArray;
  }
}
