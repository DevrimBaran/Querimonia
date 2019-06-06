package de.fraunhofer.iao.querimonia.nlp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


public class KikukoConatct<T> {

  private static final String URL = "https://kikuko.iao.fraunhofer.de/apitext";
  private final String domainType;
  private final String domainName;

  protected KikukoConatct(final String domainType, final String domainName) {
    if (!domainType.matches("(tool)|(domain)|(pipeline)")) {
      throw new IllegalArgumentException();
    }
    this.domainType = domainType;
    this.domainName = domainName;
  }

  /**
   * executes a Request to KiKuKo.
   *
   * @param input text that will be analysed
   * @return a Response Object
   */
  protected T executeKikukoRequest(final String input, final Class<T[]> type) {

    HttpHeaders header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("content", input);
    map.add("type", domainType);
    map.add("domainType", domainName);

    HttpEntity<MultiValueMap> request = new HttpEntity<>(map, header);

    // add authentication token
    RestTemplate template = new RestTemplateBuilder()
        .basicAuthentication("admin", "KIKuKoPass2018")
        .build();

    // get response
    String returnValue = template.exchange(URL, HttpMethod.POST,
        request, String.class).getBody();
    // map string to json
    ObjectMapper mapper = new ObjectMapper();
    T[] responses;
    // exception for illegal answers
    HttpServerErrorException kikukoException =
        new HttpServerErrorException(HttpStatus.CONFLICT,
            "No response from KiKUKO available");

    try {
      responses = mapper.readValue(returnValue, type);
      // check for available response
      if (responses == null || responses.length == 0) {
        throw kikukoException;
      }
    } catch (IOException e) {
      // illegal json format
      throw kikukoException;
    }

    return responses[0];
  }
}
