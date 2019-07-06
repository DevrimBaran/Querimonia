package de.fraunhofer.iao.querimonia.nlp.wordvector;

import de.fraunhofer.iao.querimonia.rest.contact.FlaskContact;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Map;

public class FlaskCalculations {

  public Map<String, Double> nearestNeighbour(String word, String corpus) {

    JSONObject jsonVector = new JSONObject();
    try {
      jsonVector.put("vector1", word);
      jsonVector.put("textkorpus", corpus);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return FlaskContact.recieveJSON(jsonVector, "word_nn");

  }

  public Map<String, Double> vectorCalculations(String vector1, String operator1, String vector2,
                                                String operator2, String vector3, String corpus) {

    JSONObject jsonVectors = new JSONObject();
    try {
      jsonVectors.put("vector1", vector1);
      jsonVectors.put("operator1", operator1);
      jsonVectors.put("vector2", vector2);
      jsonVectors.put("operator2", operator2);
      jsonVectors.put("vector3", vector3);
      jsonVectors.put("textcorpus", corpus);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return FlaskContact.recieveJSON(jsonVectors, "vector_calculation");
  }

  public Map<String, Double> vectorCalculations(String vector1, String operator1, String vector2,
                                                String corpus) {

    JSONObject jsonVectors = new JSONObject();
    try {
      jsonVectors.put("vector1", vector1);
      jsonVectors.put("operator1", operator1);
      jsonVectors.put("vector2", vector2);
      jsonVectors.put("textcorpus", corpus);

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return FlaskContact.recieveJSON(jsonVectors, "vector_calculation");
  }
}
