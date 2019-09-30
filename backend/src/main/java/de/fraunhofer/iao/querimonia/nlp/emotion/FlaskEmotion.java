package de.fraunhofer.iao.querimonia.nlp.emotion;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.rest.contact.FlaskContact;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class FlaskEmotion implements EmotionAnalyzer {

  @Override
  public ComplaintProperty analyzeEmotion(String text) {
    JSONObject jsonText = new JSONObject();
    try {
      jsonText.put("text", text);
    } catch (JSONException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e,
          "Unerwarteter JSON-Fehler");
    }

    Map<String, Double> flaskResult = FlaskContact.receiveJson(jsonText, "emotion_analyse");

    return new ComplaintProperty("Emotion", flaskResult);
  }
}
