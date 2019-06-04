package de.fraunhofer.iao.querimonia.db;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.analyze.StopWordFilter;
import de.fraunhofer.iao.querimonia.nlp.classifier.Classifier;
import de.fraunhofer.iao.querimonia.nlp.extractor.EntityExtractor;
import de.fraunhofer.iao.querimonia.nlp.response.ResponseGenerator;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.fraunhofer.iao.querimonia.response.ResponseSuggestion;
import org.springframework.lang.NonNull;


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
   * Creates a new complaint factory which is used to create complaint objects.
   *
   * @param classifier        a {@link Classifier} that returns the subject of the given text.
   * @param sentimentAnalyzer a {@link SentimentAnalyzer} that analyzes the sentiment of the text.
   * @param entityExtractor   a {@link EntityExtractor} to extract the named entities.
   * @param responseGenerator a {@link ResponseGenerator} to generate a response to the complaint.
   * @param stopWordFilter    a {@link StopWordFilter} that extracts all non stop-words from the
   *                          text.
   */
  public ComplaintFactory(@NonNull Classifier classifier,
                          @NonNull SentimentAnalyzer sentimentAnalyzer,
                          @NonNull EntityExtractor entityExtractor,
                          @NonNull ResponseGenerator responseGenerator,
                          @NonNull StopWordFilter stopWordFilter) {
    this.classifier = classifier;
    this.sentimentAnalyzer = sentimentAnalyzer;
    this.entityExtractor = entityExtractor;
    this.responseGenerator = responseGenerator;
    this.stopWordFilter = stopWordFilter;
  }

  /**
   * Creates a new complaint factory which is used to create complaint objects.
   * With this constructor the complaint factory is not configured, classifier,
   * sentiment analyzer, entity extractor and response generator must be set.
   */
  public ComplaintFactory() {
  }

  /**
   * This factory method creates a complaint out of the plain text, which contains information
   * about the date, sentiment and subject.
   *
   * @param complaintText the text of the complaint.
   * @return the generated complaint object.
   */
  public Complaint createComplaint(String complaintText) {
    Objects.requireNonNull(classifier, "classifier not initialized");
    Objects.requireNonNull(sentimentAnalyzer, "sentiment analyzer not initialized");
    Objects.requireNonNull(entityExtractor, "entity extractor not initialized");
    Objects.requireNonNull(responseGenerator, "response generator not initialized");

    String preview = makePreview(complaintText);

    Map<String, Double> sentimentMap = sentimentAnalyzer.analyzeSentiment(complaintText);
    Map<String, Double> subjectMap = classifier.classifyText(complaintText);
    List<NamedEntity> entities = entityExtractor.extractEntities(complaintText);
    ResponseSuggestion responseSuggestion = responseGenerator.generateResponse(complaintText,
        subjectMap, sentimentMap, entities);
    Map<String, Integer> words = stopWordFilter.filterStopWords(complaintText);

    return new Complaint(complaintText, preview, sentimentMap, subjectMap,
        entities, LocalDate.now(), LocalTime.now(), responseSuggestion, words);
  }

  private String makePreview(String text) {
    String subtext =  Arrays.stream(text.split("\n", 8))
        // don't use empty lines for the preview
        .filter(line -> !line.trim().isEmpty())
        .limit(2)
        .collect(Collectors.joining("\n"));

    // check for too long string
    return subtext.length() > 500 ? subtext.substring(0,500):subtext;
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
