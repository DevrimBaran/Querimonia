package de.fraunhofer.iao.querimonia.nlp.sentiment;


import de.fraunhofer.iao.querimonia.rest.contact.FlaskContact;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Map;

/**
 * SentimentAnalyzer that gets its information from our python-flask server.
 *
 * @author Samuel
 */
public class FlaskSentiment implements SentimentAnalyzer {


  @Override
  public double analyzeSentiment(String text) {
    JSONObject jsonText = new JSONObject();
    try {
      jsonText.put("text", text);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Map<String, Double> flaskResult = FlaskContact.receiveJson(jsonText, "sentiment_analyse");
    return flaskResult.getOrDefault("sentiment", 0.0);
  }

  /* *
   * creates a Text that contains each key from the map with an amount of the mapped value.
   *
   * @param nonStopWords map with the words and their appearance value
  private String createPseudoText(Map<String, Integer> nonStopWords) {
    final StringBuilder builder = new StringBuilder();
    nonStopWords.forEach((key, value) -> {
      for (int i = 0; i < value; i++) {
        builder.append(key).append(" ");
      }
    });
    return builder.toString().trim();
  }*/
}
