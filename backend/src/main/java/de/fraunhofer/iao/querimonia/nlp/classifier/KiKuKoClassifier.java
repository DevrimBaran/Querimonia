package de.fraunhofer.iao.querimonia.nlp.classifier;

import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.rest.contact.KiKuKoContact;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;


/**
 * This class sends a text to the KIKuKo API to classify the text with a given classifier.
 */
public class KiKuKoClassifier extends KiKuKoContact<KikukoResponse> implements Classifier {

  private String categoryName;

  public KiKuKoClassifier(String categoryName) {
    super("tool", "Beschwerde3Klassifikator2");
    this.categoryName = categoryName;
  }

  public KiKuKoClassifier(String domainName, String categoryName) {
    super("tool", domainName);
    this.categoryName = categoryName;
  }

  /**
   * Returns the classification if the given text using the KIKuKo classifier into the three given
   * categories.
   *
   * @param input the text which should be classified.
   * @return the type of the text.
   */
  @Override
  public ComplaintProperty classifyText(String input) {
    KikukoResponse response = executeKikukoRequest(input, KikukoResponse[].class);

    return new ComplaintProperty(categoryName, response.getPipelines()
        .getTempPipeline()
        .get(0)
        .getTyp());
  }

}
