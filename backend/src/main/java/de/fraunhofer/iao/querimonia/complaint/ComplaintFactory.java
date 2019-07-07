package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.analyze.StopWordFilter;
import de.fraunhofer.iao.querimonia.nlp.classifier.Classifier;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierFactory;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorFactory;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzer;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerFactory;
import de.fraunhofer.iao.querimonia.response.generation.ResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This factory is used to create complaint objects.
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
   * This factory method creates a complaint out of the plain text, which contains information about
   * the date, sentiment and subject.
   *
   * @param complaintText the text of the complaint.
   * @return the generated complaint object.
   */
  public Complaint createComplaint(String complaintText, Configuration configuration) {
    Complaint complaint = new Complaint(
        complaintText,
        makePreview(complaintText),
        LocalDate.now(),
        LocalTime.now());
    return analyzeComplaint(complaint, configuration, false);
  }

  /**
   * Runs the analyze process on a given complaint.
   *
   * @param complaint           the complaint, which gets modified.
   * @param configuration       the configuration that should be used for the analysis.
   * @param keepUserInformation if true, no information gets overwritten where setByUser is true.
   * @return the modified complaint.
   */
  public Complaint analyzeComplaint(Complaint complaint, Configuration configuration,
                                    boolean keepUserInformation) {
    // get the analysis tools from the configuration
    Classifier classifier = ClassifierFactory.getFromDefinition(configuration.getClassifier());
    SentimentAnalyzer sentimentAnalyzer =
        SentimentAnalyzerFactory.getFromDefinition(configuration.getSentimentAnalyzer());
    // the text of the complaint
    String complaintText = complaint.getText();

    // analysis
    Map<String, Double> subjectMap = classifier.classifyText(complaintText);
    List<NamedEntity> entities =
        extractEntities(complaint, configuration, keepUserInformation);
    Map<String, Integer> words = stopWordFilter.filterStopWords(complaintText);
    Map<String, Double> sentimentMap = sentimentAnalyzer.analyzeSentiment(words);
    complaint.getSubject().updateValueProbabilities(subjectMap, keepUserInformation);
    complaint.getSentiment().updateValueProbabilities(sentimentMap, keepUserInformation);

    // generate response
    ComplaintData complaintData = new ComplaintData(complaintText, subjectMap, sentimentMap,
        entities, LocalDateTime.of(complaint.getReceiveDate(), complaint.getReceiveTime()));
    ResponseSuggestion responseSuggestion = createResponse(complaintData);

    return complaint
        .setResponseSuggestion(responseSuggestion)
        .setWordList(words);
  }

  /**
   * Uses the response generator of the factory to generate a response suggestion.
   *
   * @param complaintData contains the necessary information about the complaint to generate the
   *                      answer.
   * @return a response suggestion for the complaint.
   */
  public ResponseSuggestion createResponse(ComplaintData complaintData) {
    return responseGenerator.generateResponse(complaintData);
  }

  /**
   * Analyzes the complaint for named entities.
   */
  private List<NamedEntity> extractEntities(Complaint complaint,
                                            Configuration configuration,
                                            boolean keepUserInformation) {
    String complaintText = complaint.getText();
    // use entity extractors
    Stream<NamedEntity> entityStream = configuration
        .getExtractors()
        .stream()
        .map(ExtractorFactory::getFromDefinition)
        .map(entityExtractor -> entityExtractor.extractEntities(complaintText))
        .flatMap(List::stream)
        .distinct();

    // keep old entities if necessary
    Stream<NamedEntity> oldEntityStream = complaint.getEntities()
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
   * @return the first two lines of the complaint, empty lines ignored, limited to 500 characters.
   */
  private String makePreview(String text) {
    String preview = Arrays.stream(text.split("\n", 8))
        // don't use empty lines for the preview
        .filter(line -> !line.trim().isEmpty())
        .limit(2)
        .collect(Collectors.joining("\n"));

    // check for too long string
    return preview.substring(0, Math.min(500, preview.length()));
  }

}
