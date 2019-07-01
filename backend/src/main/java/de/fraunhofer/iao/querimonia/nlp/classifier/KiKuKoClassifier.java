package de.fraunhofer.iao.querimonia.nlp.classifier;

import de.fraunhofer.iao.querimonia.rest.contact.KiKuKoContact;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;

import java.util.LinkedHashMap;


/**
 * This class sends a text to the KIKuKo API to classify the text with a given classifier.
 */
public class KiKuKoClassifier extends KiKuKoContact<KikukoResponse> implements Classifier {


  public KiKuKoClassifier() {
    super("tool", "Beschwerde3Klassifikator2");
  }

  /**
   * Returns the classification if the given text using the KIKuKo classifier into the three given
   * categories.
   *
   * @param input the text which should be classified.
   * @return the type of the text.
   */
  @Override
  public LinkedHashMap<String, Double> classifyText(String input) {
    KikukoResponse response = executeKikukoRequest(input, KikukoResponse[].class);

    return response.getPipelines()
        .getTempPipeline()
        .get(0)
        .getTyp();
  }

}
