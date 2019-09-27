package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.Sentiment;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.utility.WebSocketController;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.utility.log.ComplaintLog;
import de.fraunhofer.iao.querimonia.utility.log.LogCategory;
import de.fraunhofer.iao.querimonia.utility.log.LogEntry;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is the builder class for {@link Complaint complaints}. This contains the same
 * attributes as a complaint but is mutable.
 *
 * <p>Usage:</p>
 * <pre>
 * {@code var complaint = new ComplaintBuilder(exampleText)
 *      .setPreview(examplePreview)
 *      .setState(ComplaintState.IN_PROGRESS)
 *      ...
 *      .createComplaint();}</pre>
 */
public class ComplaintBuilder {
  @NonNull
  private String text;
  @Nullable
  private String preview;
  @NonNull
  private ComplaintState state = ComplaintState.ERROR;
  @NonNull
  private List<ComplaintProperty> properties = new ArrayList<>();
  @NonNull
  private Sentiment sentiment = Sentiment.getDefaultSentiment();
  @NonNull
  private List<NamedEntity> entities = new ArrayList<>();
  @NonNull
  private ResponseSuggestion responseSuggestion = ResponseSuggestion.getEmptyResponse();
  @NonNull
  private Map<String, Integer> wordList = new HashMap<>();
  @NonNull
  private LocalDate receiveDate = LocalDate.now();
  @NonNull
  private LocalTime receiveTime = LocalTime.now();

  private LocalDate closeDate = LocalDate.now();

  private LocalTime closeTime = LocalTime.now();
  @Nullable
  private Configuration configuration = Configuration.FALLBACK_CONFIGURATION;
  @NonNull
  private List<LogEntry> log = new ArrayList<>();
  private long id = 0;
  @Nullable
  private String callbackRoute;

  /**
   * Creates a new complaint builder.
   * <p>Note: To create a complaint only from its text use a {@link ComplaintFactory}</p>
   *
   * @param text the complaint text.
   */
  public ComplaintBuilder(@NonNull String text) {
    this.text = text;
  }

  /**
   * Creates a new complaint builder with all the properties of the given complaint.
   *
   * @param complaint all the properties of this complaint get used for this builder.
   */
  public ComplaintBuilder(@NonNull Complaint complaint) {
    this.id = complaint.getId();
    this.text = complaint.getText();
    this.preview = complaint.getPreview();
    this.state = complaint.getState();
    this.properties = new ArrayList<>(complaint.getProperties());
    this.sentiment = complaint.getSentiment();
    this.entities = new ArrayList<>(complaint.getEntities());
    this.responseSuggestion = complaint.getResponseSuggestion();
    this.wordList = complaint.getWordCounts();
    this.receiveDate = complaint.getReceiveDate();
    this.receiveTime = complaint.getReceiveTime();
    this.configuration = complaint.getConfiguration();
    this.log = new ArrayList<>(complaint.getLog());
    this.closeDate = complaint.getCloseDate();
    this.closeTime = complaint.getCloseTime();
    this.callbackRoute = complaint.getCallbackRoute();
  }

  /**
   * Sets the complaint text of the complaint. This can be omitted since the text is set in the
   * constructor.
   *
   * @param text the complaint text. Must not be longer than {@value Complaint#TEXT_MAX_LENGTH}
   *             characters.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setText(@NonNull String text) {
    this.text = Objects.requireNonNull(text);
    return this;
  }

  /**
   * Sets the preview of the complaint. The preview should be a short part of the complaint text.
   *
   * @param preview the preview of the the {@link #getText() complaint text}.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setPreview(@Nullable String preview) {
    this.preview = preview;
    return this;
  }

  /**
   * Sets the {@link ComplaintState state} of a complaint. The default value is
   * {@link ComplaintState#NEW}.
   *
   * @param state the new complaint state.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setState(@NonNull ComplaintState state) {
    this.state = Objects.requireNonNull(state);
    return this;
  }

  /**
   * Sets the {@link Complaint#getProperties() properties} of the complaint. By default, the
   * properties are empty.
   *
   * @param properties the new list of complaint properties.
   *
   * @return this complaint builder instance.
   */
  public ComplaintBuilder setProperties(@NonNull List<ComplaintProperty> properties) {
    this.properties = Objects.requireNonNull(properties);
    return this;
  }

  /**
   * Sets the value of a complaint property. If it exists in the property list, it gets replaced,
   * else a new complaint property is added to the list. The
   * {@link ComplaintProperty#isSetByUser() setByUser}-flag is set to true for this property.
   *
   * @param propertyName the {@link ComplaintProperty#getName() name} of the property.
   * @param value        the value of the property.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setValueOfProperty(String propertyName, String value) {
    var possibleProperties = Stream.ofNullable(properties)
        .flatMap(List::stream)
        .filter(complaintProperty -> complaintProperty.getName().equals(propertyName))
        .collect(Collectors.toList());

    ComplaintProperty newProperty = new ComplaintProperty(propertyName, value);
    if (!possibleProperties.isEmpty()) {
      possibleProperties.forEach(properties::remove);
      Map<String, Double> probabilities = possibleProperties
          .stream()
          .findFirst()
          .orElseThrow()
          .getProbabilities();
      Map<String, Double> newProbabilities = new HashMap<>();
      probabilities.keySet()
          .stream()
          .filter(property -> !property.equals(value))
          .forEach(property -> newProbabilities.put(property, 0.0));
      newProbabilities.put(value, 1.0);

      newProperty =
          new ComplaintProperty(propertyName, value, newProbabilities, true);
    }
    properties.add(newProperty);
    return this;
  }

  /**
   * Sets the {@link Sentiment} of the complaint. Has no default value, complaint creating fails
   * if this is not set.
   *
   * @param sentiment the sentiment that the complaint should have.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setSentiment(@NonNull Sentiment sentiment) {
    this.sentiment = sentiment;
    return this;
  }

  /**
   * Sets the entities of the complaint. The default value is an empty list.
   *
   * @param entities the list of named entities.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setEntities(@NonNull List<NamedEntity> entities) {
    this.entities = entities;
    return this;
  }

  /**
   * Sets the {@link ResponseSuggestion} of the complaint. The default value is an empty response.
   *
   * @param responseSuggestion the response for the complaint.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setResponseSuggestion(@NonNull ResponseSuggestion responseSuggestion) {
    this.responseSuggestion = responseSuggestion;
    return this;
  }

  /**
   * Sets the {@link Complaint#getWordCounts() the word count map} for the complaint. This should
   * contain the words of the complaint text mapped to their frequency. The default value is an
   * empty map.
   *
   * @param wordList the map that maps the words to their counts.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setWordList(Map<String, Integer> wordList) {
    this.wordList = wordList;
    return this;
  }

  /**
   * Sets the receive date of the complaint. The default value is the system date when creating
   * the complaint builder.
   *
   * @param receiveDate the date when the complaint was added to the database.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setReceiveDate(@NonNull LocalDate receiveDate) {
    this.receiveDate = receiveDate;
    return this;
  }

  /**
   * Sets the receive time of the complaint. The default value is the system time when creating
   * the complaint builder.
   *
   * @param receiveTime the time when the complaint was added to the database.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setReceiveTime(@NonNull LocalTime receiveTime) {
    this.receiveTime = receiveTime;
    return this;
  }

  /**
   * Sets the close date of the complaint.
   *
   * @param closeDate the date when the complaint was added to the database.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setCloseDate(@NonNull LocalDate closeDate) {
    this.closeDate = closeDate;
    return this;
  }

  /**
   * Sets the receive time of the complaint. The default value is the system time when creating
   * the complaint builder.
   *
   * @param closeTime the time when the complaint was added to the database.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setCloseTime(@NonNull LocalTime closeTime) {
    this.closeTime = closeTime;
    return this;
  }

  /**
   * Sets the {@link Complaint#getConfiguration() configuration} of the complaint. The default
   * value is {@link Configuration#FALLBACK_CONFIGURATION}.
   *
   * @param configuration the configuration that was used to analyze the complaint.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setConfiguration(@Nullable Configuration configuration) {
    this.configuration = configuration;
    return this;
  }

  /**
   * Sets the id of the complaint. <b>Use with caution!</b> IDs for new complaints are set by the
   * database if this is set to 0. The default value is 0.
   *
   * @param id the id that the complaint should have.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setId(long id) {
    this.id = id;
    return this;
  }

  /**
   * Sets the log of the complaint.
   *
   * @param log the list of log entries.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder setLog(@NonNull List<LogEntry> log) {
    this.log = log;
    return this;
  }

  /**
   * Sets tha callback route for the complaint.
   *
   * @param callbackRoute a valid route.
   * @return this complaint builder.
   */
  public ComplaintBuilder setCallbackRoute(@Nullable String callbackRoute) {
    this.callbackRoute = callbackRoute;
    return this;
  }

  /**
   * Adds a new log entry to the complaint and logs it in the console.
   *
   * @param category the category of the log entry.
   * @param message  the log message.
   *
   * @return this complaint builder.
   */
  public ComplaintBuilder appendLogItem(LogCategory category, String message) {
    log.add(ComplaintLog.createLogEntry(category, id, message));
    return this;
  }

  /**
   * Creates a complaint from the attributes of this builder.
   *
   * @return a complaint with the attributes of this builder.
   *
   * @throws NullPointerException if any necessary attribute is not set.
   */
  public Complaint createComplaint() {
    return new Complaint(id, text, Objects.requireNonNull(preview), state,
        Objects.requireNonNull(properties), Objects.requireNonNull(sentiment),
        Objects.requireNonNull(entities), Objects.requireNonNull(responseSuggestion),
        Objects.requireNonNull(wordList), receiveDate, receiveTime, closeDate, closeTime,
        configuration, log, callbackRoute);
  }

  /**
   * Returns the complaint text.
   *
   * @return the complaint text.
   */
  @NonNull
  public String getText() {
    return text;
  }

  /**
   * Returns the preview of the complaint text. The preview consists usually of the first to
   * paragraphs or sentences of the complaint, limited to {@value Complaint#PREVIEW_MAX_LENGTH}
   * characters.
   *
   * @return the preview of the complaint text.
   */
  @Nullable
  public String getPreview() {
    return preview;
  }

  /**
   * Returns the {@link ComplaintState state} of the complaint.
   *
   * @return the {@link ComplaintState state} of the complaint.
   */
  @NonNull
  public ComplaintState getState() {
    return state;
  }

  /**
   * Returns the {@link ComplaintProperty properties} of the complaint. The properties represent
   * the results of the analyzers that were used to analyze the complaint.
   *
   * @return the {@link ComplaintProperty properties} of the complaint.
   */
  @NonNull
  public List<ComplaintProperty> getProperties() {
    return properties;
  }

  /**
   * Returns the {@link Sentiment sentiment} of the complaint text.
   *
   * @return the {@link Sentiment sentiment} of the complaint text.
   */
  @NonNull
  public Sentiment getSentiment() {
    return sentiment;
  }

  /**
   * Returns the list of {@link NamedEntity named entities} that occur in the complaint text.
   *
   * @return the list of {@link NamedEntity named entities} that occur in the complaint text.
   */
  @NonNull
  public List<NamedEntity> getEntities() {
    return entities;
  }

  /**
   * Returns the generated {@link ResponseSuggestion response} of the complaint.
   *
   * @return the generated {@link ResponseSuggestion response} of the complaint.
   */
  @NonNull
  public ResponseSuggestion getResponseSuggestion() {
    return responseSuggestion;
  }

  /**
   * Returns a map that maps the words of the complaint text to their frequency. Note that not
   * possibly not every word is listed here as key, since stop words get filtered out in the
   * analysis process.
   *
   * @return a map that maps the words of the complaint text to their frequency.
   */
  @NonNull
  public Map<String, Integer> getWordList() {
    return wordList;
  }

  /**
   * Returns the date when the complaint was imported into querimonia.
   *
   * @return the date when the complaint was imported into querimonia.
   */
  @NonNull
  public LocalDate getReceiveDate() {
    return receiveDate;
  }

  /**
   * Returns the time when the complaint was imported into querimonia.
   *
   * @return the time when the complaint was imported into querimonia.
   */
  @NonNull
  public LocalTime getReceiveTime() {
    return receiveTime;
  }

  /**
   * Returns the configuration that was used to analyze this complaint. The configuration
   * contains information about the tools that where used in the analysis process.
   *
   * @return the configuration used to analyze this complaint.
   */
  @Nullable
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Returns the log of the complaint. The complaint log contains all information about the
   * analysis and edits of the complaint. It also logs errors.
   *
   * @return a list of {@link LogEntry log entries}.
   */
  @NonNull
  public List<LogEntry> getLog() {
    return log;
  }

  /**
   * Returns the unique id of the complaint. This is the primary key in the database. Each
   * complaint can be identified with this id.
   *
   * @return the unique id of the complaint.
   */
  public long getId() {
    return id;
  }

  @Nullable
  public String getCallbackRoute() {
    return callbackRoute;
  }
}