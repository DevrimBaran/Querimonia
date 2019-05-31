package de.fraunhofer.iao.querimonia.db;

import de.fraunhofer.iao.querimonia.nlp.classifier.Classifier;
import de.fraunhofer.iao.querimonia.nlp.extractor.EntityExtractor;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;


/**
 * This factory is used to create complaint objects.
 */
public class ComplaintFactory {

  private Classifier classifier;
  private SentimentAnalyzer sentimentAnalyzer;
  private EntityExtractor entityExtractor;

  /**
   * Creates a new complaint factory which is used to create complaint objects.
   *
   * @param classifier a {@link Classifier} that returns the subject of the given text.
   * @param sentimentAnalyzer a {@link SentimentAnalyzer} that analyzes the sentiment of the text.
   * @param entityExtractor a {@link EntityExtractor} to extract the named entities.
   */
  public ComplaintFactory(@NonNull Classifier classifier,
                          @NonNull SentimentAnalyzer sentimentAnalyzer,
                          @NonNull EntityExtractor entityExtractor) {
    this.classifier = classifier;
    this.sentimentAnalyzer = sentimentAnalyzer;
    this.entityExtractor = entityExtractor;
  }

  /**
   * This factory method creates a complaint out of the plain text, which contains information
   * about the date, sentiment and subject.
   *
   * @param complaintText the text of the complaint.
   * @return the generated complaint object.
   */
  public Complaint createComplaint(String complaintText) {
    String preview = makePreview(complaintText);

    return new Complaint(complaintText, preview,
        sentimentAnalyzer.analyzeSentiment(complaintText),
        classifier.classifyText(complaintText),
        LocalDate.now(), LocalTime.now(),
        entityExtractor.extractEntities(complaintText));
  }

  private String makePreview(String text) {
    return Arrays.stream(text.split("\n"))
        .filter(line -> !line.trim().isEmpty())
        .limit(2)
        .collect(Collectors.joining("\n"));
  }
}
