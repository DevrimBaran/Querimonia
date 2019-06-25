package de.fraunhofer.iao.querimonia.nlp.sentiment;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.nlp.FlaskContact;
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

  @Override
  public Map<String, Double> analyzeSentiment(Map<String, Integer> nonStopWords) {
    String text = createPseudoText(nonStopWords);

    JSONObject jsonText = new JSONObject();
    try {
      jsonText.put("text", text);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return FlaskContact.recieveJSON(jsonText, "sentiment_analyse");
  }

  /**
   * creates a Text that contains each key from the map with an amount of the mapped value.
   * @param nonStopWords map with the words and their appearance value
   * @return
   */
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
