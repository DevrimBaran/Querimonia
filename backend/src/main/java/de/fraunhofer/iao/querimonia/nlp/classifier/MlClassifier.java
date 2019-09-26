package de.fraunhofer.iao.querimonia.nlp.classifier;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.rest.contact.FlaskContact;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Map;

public class MlClassifier implements Classifier {

  @Override
  public ComplaintProperty classifyText(String text) {
    JSONObject jsonText = new JSONObject();
    try {
      jsonText.put("text", text);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Map<String, Double> flaskResult = FlaskContact.receiveJson(jsonText, "classifier");

    return new ComplaintProperty("Kategorie", flaskResult);
  }
}
