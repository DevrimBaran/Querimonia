package de.fraunhofer.iao.querimonia.nlp;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;
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


public class KikukoConatct {

  private static final String URL = "https://kikuko.iao.fraunhofer.de/apitext";
  private final String DOMAIN_TYPE;
  private final String DOMAIN_NAME;

  protected KikukoConatct(final String domainType, final String domainName) {
    if (!domainType.matches("(tool)|(domain)|(pipeline)")) {
        throw new IllegalArgumentException();
    }
      DOMAIN_TYPE = domainType;
      DOMAIN_NAME = domainName;
    }

  /**
  * executes a Request to KiKuKo.
  * @param input text that will be analysed
  * @return a Response Object
  */
  protected KikukoResponse executeKikukoRequest(final String input) {

      HttpHeaders header = new HttpHeaders();
      header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("content", input);
      map.add("type", DOMAIN_TYPE);
      map.add("domainType", DOMAIN_NAME);

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
      KikukoResponse[] responses;
      // exception for illegal answers
      HttpServerErrorException kikukoException =
        new HttpServerErrorException(HttpStatus.CONFLICT,
                      "No response from KiKUKO available");
      try {
          responses = mapper.readValue(returnValue, KikukoResponse[].class);
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
