package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.analyze.StopWordFilter;
import de.fraunhofer.iao.querimonia.nlp.classifier.Classifier;
import de.fraunhofer.iao.querimonia.nlp.extractor.EntityExtractor;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzer;
import de.fraunhofer.iao.querimonia.response.generation.ResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    Complaint complaint = new Complaint(
        complaintText,
        makePreview(complaintText),
        LocalDate.now(),
        LocalTime.now());
    return analyzeComplaint(complaint, true);
  }

  public Complaint analyzeComplaint(Complaint complaint, boolean keepUserInformation) {
    Objects.requireNonNull(classifier, "classifier not initialized");
    Objects.requireNonNull(sentimentAnalyzer, "sentiment analyzer not initialized");
    Objects.requireNonNull(entityExtractor, "entity extractor not initialized");
    Objects.requireNonNull(responseGenerator, "response generator not initialized");
    Objects.requireNonNull(stopWordFilter, "stop word filter not initialized");

    String complaintText = complaint.getText();

    Map<String, Double> subjectMap = classifier.classifyText(complaintText);

    List<NamedEntity> entities =
        extractEntities(complaint, keepUserInformation, complaintText);

    Map<String, Integer> words = stopWordFilter.filterStopWords(complaintText);
    Map<String, Double> sentimentMap = sentimentAnalyzer.analyzeSentiment(words);

    ResponseSuggestion responseSuggestion = responseGenerator.generateResponse(
        new ComplaintData(complaintText, subjectMap, sentimentMap, entities, LocalDateTime.now()));

    complaint.getSubject().updateValueProbabilities(subjectMap, keepUserInformation);
    complaint.getSentiment().updateValueProbabilities(sentimentMap, keepUserInformation);

    return complaint
        .setResponseSuggestion(responseSuggestion)
        .setWordList(words);
  }

  /**
   * Analyzes the complaint for named entities.
   */
  private List<NamedEntity> extractEntities(Complaint complaint, boolean keepUserInformation,
                                            String complaintText) {
    Stream<NamedEntity> entityStream = entityExtractor.extractEntities(complaintText)
        .stream();
    Stream<NamedEntity> oldEntityStream = complaint.getEntities()
        .stream()
        .filter(NamedEntity::isSetByUser);
    if (keepUserInformation) {
      entityStream = Stream.concat(entityStream, oldEntityStream).distinct();
    }
    return entityStream.collect(Collectors.toList());
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
