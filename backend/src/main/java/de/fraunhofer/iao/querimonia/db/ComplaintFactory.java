package de.fraunhofer.iao.querimonia.db;

import de.fraunhofer.iao.querimonia.nlp.classifier.KIKuKoClassifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This factory is used to create complaint objects.
 */
public class ComplaintFactory {

  /**
   * This factory method creates a complaint out of the plain text, which contains information
   * about the date, sentiment and subject.
   *
   * @param complaintText the text of the complaint.
   * @return the generated complaint object.
   */
  public Complaint createComplaint(String complaintText) {
    String preview = makePreview(complaintText);
    String subject = getSubject(complaintText);

    return new Complaint(complaintText, preview, "NORMAL", subject, LocalDate.now());
  }

  private String makePreview(String text) {
    return Arrays.stream(text.split("\n"))
        .filter(line -> !line.trim().isEmpty())
        .limit(2)
        .collect(Collectors.joining("\n"));
  }

  private String getSubject(String text) {
    HashMap<String, Double> typeMap = new KIKuKoClassifier().classifyText(text);

    return typeMap.entrySet()
        .stream()
        .max(Comparator.comparingDouble(Map.Entry::getValue))
        .map(Map.Entry::getKey)
        .orElse("UNKNOWN");
  }
}
