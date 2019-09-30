package de.fraunhofer.iao.querimonia.nlp.classifier;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.rest.contact.FlaskContact;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class MlClassifier implements Classifier {

  @Override
  public ComplaintProperty classifyText(String text) {
    JSONObject jsonText = new JSONObject();
    try {
      jsonText.put("text", text);
    } catch (JSONException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e,
          "Unerwarteter JSON-Fehler");
    }

    Map<String, Double> flaskResult = FlaskContact.receiveJson(jsonText, "classifier");
    flaskResult.replaceAll((property, value) -> ((double) Math.round(value * 1000)) / 1000.0);

    return new ComplaintProperty("Kategorie", flaskResult);
  }
}
