package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.Sentiment;
import de.fraunhofer.iao.querimonia.nlp.analyze.StopWordFilter;
import de.fraunhofer.iao.querimonia.nlp.classifier.Classifier;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierFactory;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzer;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerFactory;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorFactory;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzer;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerFactory;
import de.fraunhofer.iao.querimonia.response.generation.ResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This factory is used to create complaint objects. It manages the analysis and response
 * generation process.
 * <p>Plaint complaints can be created with {@link #createBaseComplaint(String, Configuration)
 * createBaseComplaint}. These can be analyzed with
 * {@link #analyzeComplaint(ComplaintBuilder, boolean) analyzeComplaint}.</p>
 */
public class ComplaintFactory {

  private final ResponseGenerator responseGenerator;
  private final StopWordFilter stopWordFilter;

  /**
   * Creates a new complaint factory which is used to create complaint objects.
   *
   * @param responseGenerator the response generator generates a {@link ResponseSuggestion} for
   *                          the complaints.
   * @param stopWordFilter    the stop word filter is used to filter out stop words from the
   *                          complaint text.
   */
  public ComplaintFactory(ResponseGenerator responseGenerator, StopWordFilter stopWordFilter) {
    this.responseGenerator = responseGenerator;
    this.stopWordFilter = stopWordFilter;
  }


  /**
   * This factory method creates a complaint out of the plain text, which contains the basic
   * information: receive date and time, preview, configuration.
   *
   * @param complaintText the text of the complaint.
   *
   * @return the generated complaint builder with the basic information but without any analysis.
   */
  public ComplaintBuilder createBaseComplaint(String complaintText, Configuration configuration) {
    return new ComplaintBuilder(complaintText)
        .setConfiguration(configuration)
        .setPreview(makePreview(complaintText))
        .setReceiveDate(LocalDate.now())
        .setReceiveTime(LocalTime.now());
  }

  /**
   * Runs the analysis process on a given complaint.
   *
   * @param complaintBuilder    the complaint builder, which gets modified with the results of
   *                            the analysis.
   * @param keepUserInformation if true, no information gets overwritten where setByUser is true.
   *
   * @return the analyzed complaint.
   */
  public ComplaintBuilder analyzeComplaint(ComplaintBuilder complaintBuilder,
                                           boolean keepUserInformation) {

    Configuration configuration = complaintBuilder.getConfiguration();
    // get the analysis tools from the configuration
    List<Classifier> classifiers =
        ClassifierFactory.getFromDefinition(configuration.getClassifiers());
    SentimentAnalyzer sentimentAnalyzer =
        SentimentAnalyzerFactory.getFromDefinition(configuration.getSentimentAnalyzer());
    EmotionAnalyzer emotionAnalyzer
        = EmotionAnalyzerFactory.getFromDefinition(configuration.getEmotionAnalyzer());

    // the text of the complaint
    String complaintText = complaintBuilder.getText();

    // analysis
    var complaintProperties = classifiers.stream().parallel()
        .map(classifier -> classifier.classifyText(complaintText))
        .collect(Collectors.toList());
    var emotionProperty = emotionAnalyzer.analyzeEmotion(complaintText);
    var entities = extractEntities(complaintBuilder, configuration, keepUserInformation);
    var tendency = sentimentAnalyzer.analyzeSentiment(complaintText);
    var sentiment = new Sentiment(emotionProperty, tendency);
    var wordList = stopWordFilter.filterStopWords(complaintText);

    ResponseSuggestion responseSuggestion = createResponse(complaintBuilder);
    return complaintBuilder
        .setEntities(entities)
        .setSentiment(sentiment)
        .setResponseSuggestion(responseSuggestion)
        .setProperties(complaintProperties)
        .setWordList(wordList);
  }

  /**
   * Uses the response generator of the factory to generate a response suggestion.
   *
   * @param complaintBuilder contains the necessary information about the complaint to generate the
   *                         answer.
   *
   * @return a response suggestion for the complaint.
   */
  public ResponseSuggestion createResponse(ComplaintBuilder complaintBuilder) {
    return responseGenerator.generateResponse(complaintBuilder);
  }

  /**
   * Analyzes the complaint for named entities.
   */
  private List<NamedEntity> extractEntities(ComplaintBuilder complaintBuilder,
                                            Configuration configuration,
                                            boolean keepUserInformation) {
    String complaintText = complaintBuilder.getText();
    // use entity extractors
    Stream<NamedEntity> entityStream = configuration
        .getExtractors()
        .stream()
        .parallel()
        .map(ExtractorFactory::getFromDefinition)
        .map(entityExtractor -> entityExtractor.extractEntities(complaintText))
        .flatMap(List::stream)
        .distinct();

    // keep old entities if necessary
    List<NamedEntity> oldEntities = complaintBuilder.getEntities();
    Stream<NamedEntity> oldEntityStream =
        oldEntities
            .stream()
            .filter(NamedEntity::isSetByUser);
    if (keepUserInformation) {
      entityStream = Stream.concat(entityStream, oldEntityStream).distinct();
    }
    // sort entities
    entityStream = entityStream.sorted();

    return entityStream.collect(Collectors.toList());
  }

  /**
   * Generates a small preview of the complaint text.
   *
   * @param text the full text of the complaint.
   *
   * @return the first two lines of the complaint, empty lines ignored, limited to 500 characters.
   */
  private String makePreview(String text) {
    String preview = Arrays.stream(text.split("\n", 8))
        // don't use empty lines for the preview
        .filter(line -> !line.trim().isEmpty())
        .limit(2)
        .collect(Collectors.joining("\n"));

    // check for too long string
    return preview.substring(0, Math.min(Complaint.PREVIEW_MAX_LENGTH, preview.length()));
  }

}
