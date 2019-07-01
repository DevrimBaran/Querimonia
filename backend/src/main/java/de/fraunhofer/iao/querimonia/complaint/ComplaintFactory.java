package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.analyze.StopWordFilter;
import de.fraunhofer.iao.querimonia.nlp.classifier.Classifier;
import de.fraunhofer.iao.querimonia.nlp.extractor.EntityExtractor;
import de.fraunhofer.iao.querimonia.response.generation.ResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * This factory is used to create complaint objects.
 */
public class ComplaintFactory {

  private Classifier classifier = null;
  private SentimentAnalyzer sentimentAnalyzer = null;
  private EntityExtractor entityExtractor = null;
  private ResponseGenerator responseGenerator = null;
  private StopWordFilter stopWordFilter = null;

  /**
   * Creates a new complaint factory which is used to create complaint objects. With this
   * constructor the complaint factory is not configured, classifier, sentiment analyzer, entity
   * extractor and response generator must be set.
   */
  public ComplaintFactory() {
  }

  /**
   * This factory method creates a complaint out of the plain text, which contains information about
   * the date, sentiment and subject.
   *
   * @param complaintText the text of the complaint.
   * @return the generated complaint object.
   */
  public Complaint createComplaint(String complaintText) {
    Objects.requireNonNull(classifier, "classifier not initialized");
    Objects.requireNonNull(sentimentAnalyzer, "sentiment analyzer not initialized");
    Objects.requireNonNull(entityExtractor, "entity extractor not initialized");
    Objects.requireNonNull(responseGenerator, "response generator not initialized");
    Objects.requireNonNull(stopWordFilter, "stop word filter not initialized");

    String preview = makePreview(complaintText);
    Map<String, Double> subjectMap = classifier.classifyText(complaintText);
    List<NamedEntity> entities = entityExtractor.extractEntities(complaintText)
        .stream()
        .distinct()
        .collect(Collectors.toList());

    Map<String, Integer> words = stopWordFilter.filterStopWords(complaintText);
    Map<String, Double> sentimentMap = sentimentAnalyzer.analyzeSentiment(words);

    ResponseSuggestion responseSuggestion = responseGenerator.generateResponse(
        new ComplaintData(complaintText, subjectMap, sentimentMap, entities, LocalDateTime.now()));

    return new Complaint(complaintText, preview, sentimentMap, subjectMap,
                         entities, LocalDate.now(), LocalTime.now(), responseSuggestion, words);
  }

  private String makePreview(String text) {
    String preview = Arrays.stream(text.split("\n", 8))
        // don't use empty lines for the preview
        .filter(line -> !line.trim().isEmpty())
        .limit(2)
        .collect(Collectors.joining("\n"));

    // check for too long string
    return preview.substring(0, Math.min(500, preview.length()));
  }

  public ComplaintFactory setClassifier(Classifier classifier) {
    this.classifier = classifier;
    return this;
  }

  public ComplaintFactory setSentimentAnalyzer(SentimentAnalyzer sentimentAnalyzer) {
    this.sentimentAnalyzer = sentimentAnalyzer;
    return this;
  }

  public ComplaintFactory setEntityExtractor(EntityExtractor entityExtractor) {
    this.entityExtractor = entityExtractor;
    return this;
  }

  public ComplaintFactory setResponseGenerator(ResponseGenerator responseGenerator) {
    this.responseGenerator = responseGenerator;
    return this;
  }

  public ComplaintFactory setStopWordFilter(StopWordFilter stopWordFilter) {
    this.stopWordFilter = stopWordFilter;
    return this;
  }
}
