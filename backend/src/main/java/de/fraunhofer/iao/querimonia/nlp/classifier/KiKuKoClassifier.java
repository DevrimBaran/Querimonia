package de.fraunhofer.iao.querimonia.nlp.classifier;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.rest.contact.KiKuKoContact;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;

import java.util.Map;


/**
 * This class sends a text to the KIKuKo API to classify the text with a given classifier.
 */
public class KiKuKoClassifier extends KiKuKoContact implements Classifier {

  private final String categoryName;

  public KiKuKoClassifier(String domainName, String categoryName) {
    super("pipeline", domainName);
    this.categoryName = categoryName;
  }

  /**
   * Returns the classification if the given text using the KIKuKo classifier into the three given
   * categories.
   *
   * @param input the text which should be classified.
   *
   * @return the type of the text.
   */
  @Override
  public ComplaintProperty classifyText(String input) {
    KikukoResponse response = executeKikukoRequest(input);

    Map<String, Double> probs =
        response.getPipelines()
            .get(domainName)
            .get(0)
            .getTyp();

    return new ComplaintProperty(categoryName, probs);
  }

}
