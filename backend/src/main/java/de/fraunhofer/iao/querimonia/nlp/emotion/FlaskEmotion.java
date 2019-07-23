package de.fraunhofer.iao.querimonia.nlp.emotion;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.rest.contact.FlaskContact;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Map;

public class FlaskEmotion implements EmotionAnalyzer {

  @Override
  public ComplaintProperty analyzeEmotion(String text) {
    JSONObject jsonText = new JSONObject();
    try {
      jsonText.put("text", text);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Map<String, Double> flaskResult = FlaskContact.receiveJson(jsonText, "emotion_analyse");

    return new ComplaintProperty("Emotion", flaskResult);
  }
}
