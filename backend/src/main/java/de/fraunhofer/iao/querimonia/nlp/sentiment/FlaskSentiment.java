package de.fraunhofer.iao.querimonia.nlp.sentiment;


import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.rest.contact.FlaskContact;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * SentimentAnalyzer that gets its information from our python-flask server.
 *
 * @author Samuel
 */
public class FlaskSentiment implements SentimentAnalyzer {

  @Override
  public ComplaintProperty analyzeEmotion(String text) {
    double sentimentValue = analyzeSentiment(text);

    Map<String, Double> result = new HashMap<>();

    if (sentimentValue >= 1) {
      result.put("Euphorie", 1.0);
    } else if (sentimentValue >= 0.5) {
      result.put("Freude", 1.0);
    } else if (sentimentValue >= 0) {
      result.put("Neutral", 1.0);
    } else if (sentimentValue >= -0.5) {
      result.put("Unzufriedenheit", 1.0);
    } else {
      result.put("Wut", 1.0);
    }
    return new ComplaintProperty(result, "Emotion");
  }

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

  /**
   * creates a Text that contains each key from the map with an amount of the mapped value.
   *
   * @param nonStopWords map with the words and their appearance value
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
