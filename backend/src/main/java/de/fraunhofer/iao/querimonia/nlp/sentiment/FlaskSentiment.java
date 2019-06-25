package de.fraunhofer.iao.querimonia.nlp.sentiment;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SentimentAnalyzer that gets its information from our python-flask server.
 *
 * @author Samuel
 */
public class FlaskSentiment implements SentimentAnalyzer {

  private final String URL = "https://querimonia.iao.fraunhofer.de/sentiment_analyse";

  @Override
  public Map<String, Double> analyzeSentiment(Map<String, Integer> nonStopWords) {
    String text = createPseudoText(nonStopWords);


    HttpHeaders header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_JSON);
    JSONObject jsonText = new JSONObject();
    try {
      jsonText.put("text", text);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    HttpEntity<String> request = new HttpEntity<>(jsonText.toString(), header);
    String response = new RestTemplate().exchange(URL, HttpMethod.POST, request, String.class).getBody();


    ObjectMapper mapper = new ObjectMapper();
    Map<String, Double> map;
    try {
      map = mapper.readValue(response, HashMap.class);
    } catch (IOException e) {
      e.printStackTrace();
      map = new HashMap<>();
    }

    return map;
  }

  private String createPseudoText(Map<String, Integer> nonStopWords) {
    final StringBuilder builder = new StringBuilder();
    nonStopWords.forEach((key, value) -> {
      for (int i = 0; i < value; i++) {
        builder.append(key).append(" ");
      }
    });
    return builder.toString().trim();
  }
}
