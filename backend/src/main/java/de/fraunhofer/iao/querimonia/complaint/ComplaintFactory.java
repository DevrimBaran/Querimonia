package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.nlp.Sentiment;
import de.fraunhofer.iao.querimonia.nlp.analyze.StopWordFilter;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierFactory;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerFactory;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorFactory;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerFactory;
import de.fraunhofer.iao.querimonia.response.generation.ResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.utility.log.LogCategory;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This factory is used to create complaint objects. It manages the analysis and response
 * generation process.
 * <p>Plain complaints can be created with {@link #createBaseComplaint(String, Configuration)
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
   * Returns all entities of a complaint and adds entities for the upload date and time.
   */
  @NonNull
  private static List<NamedEntity> getAllEntities(ComplaintBuilder complaintBuilder,
                                                  List<NamedEntity> entities) {
    List<NamedEntity> allEntities = new ArrayList<>(entities);

    // add upload date entities
    String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .withLocale(Locale.GERMAN)
        .format(complaintBuilder.getReceiveDate());
    String formattedTime = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN)
        .format(complaintBuilder.getReceiveTime());
    // get first extractors that is used for the entities that are not in the text
    String extractor = Objects.requireNonNull(complaintBuilder
        .getConfiguration())
        .getExtractors()
        .stream()
        .findFirst()
        .map(ExtractorDefinition::getName)
        .orElse("");

    allEntities.add(new NamedEntityBuilder()
        .setLabel("Eingangsdatum")
        .setStart(0)
        .setEnd(0)
        .setSetByUser(false)
        .setExtractor(extractor)
        .setValue(formattedDate)
        .setColor(getColorOfEntity(complaintBuilder, "Eingangsdatum"))
        .createNamedEntity());
    allEntities.add(new NamedEntityBuilder()
        .setLabel("Eingangszeit")
        .setStart(0)
        .setEnd(0)
        .setSetByUser(false)
        .setExtractor(extractor)
        .setValue(formattedTime)
        .setColor(getColorOfEntity(complaintBuilder, "Eingangszeit"))
        .createNamedEntity());
    return allEntities;
  }

  private static String getColorOfEntity(ComplaintBuilder complaintBuilder, String label) {
    return Objects.requireNonNull(complaintBuilder
        .getConfiguration())
        .getExtractors()
        .stream()
        .filter(extractorDefinition -> extractorDefinition.getLabel().equals(label))
        .map(ExtractorDefinition::getColor)
        .findAny()
        // fallback color
        .orElse("#cc22cc");
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
        .setReceiveDate(LocalDate.now(ZoneId.of("Europe/Berlin")))
        .setReceiveTime(LocalTime.now(ZoneId.of("Europe/Berlin")));
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
    var sentimentAnalyzerDefinition = Objects.requireNonNull(configuration).getSentimentAnalyzer();
    var emotionAnalyzerDefinition = configuration.getEmotionAnalyzer();

    // the text of the complaint
    String complaintText = complaintBuilder.getText();

    // analysis
    var complaintProperties = new ArrayList<ComplaintProperty>();
    List<ClassifierDefinition> classifiers = configuration
        .getClassifiers();
    for (int i = 0; i < classifiers.size(); i++) {
      ClassifierDefinition classifierDefinition = classifiers.get(i);
      ComplaintProperty complaintProperty = classifyComplaint(complaintBuilder, complaintText,
          classifierDefinition);
      complaintProperties.add(complaintProperty);
    }

    var emotionProperty = extractEmotion(complaintBuilder, emotionAnalyzerDefinition);
    extractEntities(complaintBuilder, configuration, keepUserInformation);
    var tendency = extractTendency(complaintBuilder, sentimentAnalyzerDefinition);
    var sentiment = new Sentiment(emotionProperty, tendency);
    var wordList = stopWordFilter.filterStopWords(complaintText);
    complaintBuilder
        .setSentiment(sentiment)
        .setWordList(wordList)
        .setProperties(complaintProperties);
    createResponse(complaintBuilder);

    return complaintBuilder;
  }

  @NonNull
  private ComplaintProperty extractEmotion(ComplaintBuilder complaintBuilder,
                                           EmotionAnalyzerDefinition emotionAnalyzerDefinition) {
    String complaintText = complaintBuilder.getText();
    var emotionProperty =
        EmotionAnalyzerFactory.getFromDefinition(emotionAnalyzerDefinition)
            .analyzeEmotion(complaintText);
    complaintBuilder.appendLogItem(LogCategory.ANALYSIS,
        "Emotionsanalysator '" + emotionAnalyzerDefinition.getName() + "': "
            + "Emotion auf '" + emotionProperty.getValue() + "' gesetzt");
    return emotionProperty;
  }

  private double extractTendency(ComplaintBuilder complaintBuilder,
                                 SentimentAnalyzerDefinition sentimentAnalyzerDefinition) {
    String complaintText = complaintBuilder.getText();
    var tendency =
        SentimentAnalyzerFactory.getFromDefinition(sentimentAnalyzerDefinition)
            .analyzeSentiment(complaintText);
    complaintBuilder.appendLogItem(LogCategory.ANALYSIS,
        "Sentiment-Analysator '" + sentimentAnalyzerDefinition.getName()
            + "': Tendenz auf '" + tendency + "' gesetzt");
    return tendency;
  }

  @NonNull
  private ComplaintProperty classifyComplaint(ComplaintBuilder complaintBuilder,
                                              String complaintText,
                                              ClassifierDefinition classifierDefinition) {
    var classifier = ClassifierFactory.getFromDefinition(classifierDefinition);
    var result = classifier.classifyText(complaintText);
    // log results
    complaintBuilder.appendLogItem(LogCategory.ANALYSIS,
        "Klassifikator '" + classifierDefinition.getName() + "': Eigenschaft '"
            + classifierDefinition.getCategoryName() + "' auf '" + result.getValue() + "' gesetzt");
    return result;
  }

  /**
   * Uses the response generator of the factory to generate a response suggestion.
   *
   * @param complaintBuilder contains the necessary information about the complaint to generate the
   *                         answer.
   */
  public void createResponse(ComplaintBuilder complaintBuilder) {
    var response = responseGenerator.generateResponse(complaintBuilder);
    complaintBuilder
        .setResponseSuggestion(response)
        .appendLogItem(LogCategory.ANALYSIS,
            "Antwort mit " + response.getResponseComponents().size() + " Komponenten generiert.");
  }

  /**
   * Analyzes the complaint for named entities.
   */
  private void extractEntities(ComplaintBuilder complaintBuilder,
                               Configuration configuration,
                               boolean keepUserInformation) {
    String complaintText = complaintBuilder.getText();
    // use entity extractors
    Stream<NamedEntity> entityStream = configuration
        .getExtractors()
        .stream()
        .map(definition -> extractEntitiesAndLog(complaintBuilder, complaintText, definition))
        .flatMap(List::stream)
        .distinct();

    // keep old entities if necessary
    if (keepUserInformation) {
      List<NamedEntity> oldEntities = complaintBuilder.getEntities();
      Stream<NamedEntity> oldEntityStream =
          oldEntities
              .stream()
              .filter(NamedEntity::isSetByUser);
      entityStream = Stream.concat(entityStream, oldEntityStream).distinct();
    }
    // sort entities
    entityStream = entityStream.sorted();
    var entities = entityStream.collect(Collectors.toList());
    complaintBuilder.setEntities(getAllEntities(complaintBuilder, entities));
  }

  @NonNull
  private List<NamedEntity> extractEntitiesAndLog(ComplaintBuilder complaintBuilder,
                                                  String complaintText,
                                                  ExtractorDefinition definition) {
    var extractor = ExtractorFactory.getFromDefinition(definition);
    var result = extractor.extractEntities(complaintText);
    complaintBuilder.appendLogItem(LogCategory.ANALYSIS,
        "Extraktor " + definition.getName() + ": " + result.size() + " Entitäten gefunden.");
    return result;
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
