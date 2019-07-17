package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ComplaintBuilder {
  @NonNull
  private String text;
  @Nullable
  private String preview;
  @NonNull
  private ComplaintState state = ComplaintState.NEW;
  @Nullable
  private List<ComplaintProperty> properties;
  private double sentiment = 0.0;
  @Nullable
  private List<NamedEntity> entities;
  @Nullable
  private ResponseSuggestion responseSuggestion;
  @Nullable
  private Map<String, Integer> wordList;
  @NonNull
  private LocalDate receiveDate = LocalDate.now();
  @NonNull
  private LocalTime receiveTime = LocalTime.now();
  @NonNull
  private Configuration configuration = Configuration.FALLBACK_CONFIGURATION;
  private long id = 0;

  public ComplaintBuilder() {
    text = "";
  }

  public ComplaintBuilder(@NonNull String text) {
    this.text = text;
  }

  public ComplaintBuilder(@NonNull Complaint complaint) {
    this.id = complaint.getId();
    this.text = complaint.getText();
    this.preview = complaint.getPreview();
    this.state = complaint.getState();
    this.properties = new ArrayList<>(complaint.getProperties());
    this.sentiment = complaint.getSentiment();
    this.entities = new ArrayList<>(complaint.getEntities());
    this.responseSuggestion = complaint.getResponseSuggestion();
    this.wordList = complaint.getWordList();
    this.receiveDate = complaint.getReceiveDate();
    this.receiveTime = complaint.getReceiveTime();
    this.configuration = complaint.getConfiguration();
  }

  public ComplaintBuilder setText(@NonNull String text) {
    this.text = Objects.requireNonNull(text);
    return this;
  }

  public ComplaintBuilder setPreview(@Nullable String preview) {
    this.preview = preview;
    return this;
  }

  public ComplaintBuilder setState(@NonNull ComplaintState state) {
    this.state = Objects.requireNonNull(state);
    return this;
  }

  public ComplaintBuilder setProperties(@Nullable List<ComplaintProperty> properties) {
    this.properties = properties;
    return this;
  }

  public ComplaintBuilder setValueOfProperty(String propertyName, String value) {
    var property = Stream.ofNullable(properties)
        .flatMap(List::stream)
        .filter(complaintProperty -> complaintProperty.getName().equals(propertyName))
        .findFirst();

    ComplaintProperty newProperty = new ComplaintProperty(propertyName, value);
    if (property.isPresent()) {
      assert properties != null;
      properties.remove(property.get());
      newProperty = new ComplaintProperty(propertyName, value, property.get().getProbabilities(),
          true);
    }
    if (properties == null) {
      properties = new ArrayList<>();
    }
    properties.add(newProperty);
    return this;
  }

  public ComplaintBuilder setSentiment(double sentiment) {
    this.sentiment = sentiment;
    return this;
  }

  public ComplaintBuilder setEntities(@Nullable List<NamedEntity> entities) {
    this.entities = entities;
    return this;
  }

  public ComplaintBuilder setResponseSuggestion(@Nullable ResponseSuggestion responseSuggestion) {
    this.responseSuggestion = responseSuggestion;
    return this;
  }

  public ComplaintBuilder setWordList(Map<String, Integer> wordList) {
    this.wordList = wordList;
    return this;
  }

  public ComplaintBuilder setReceiveDate(@NonNull LocalDate receiveDate) {
    this.receiveDate = receiveDate;
    return this;
  }

  public ComplaintBuilder setReceiveTime(@NonNull LocalTime receiveTime) {
    this.receiveTime = receiveTime;
    return this;
  }

  public ComplaintBuilder setConfiguration(@NonNull Configuration configuration) {
    this.configuration = configuration;
    return this;
  }

  public ComplaintBuilder setId(long id) {
    this.id = id;
    return this;
  }

  public Complaint createComplaint() {
    return new Complaint(id, text, Objects.requireNonNull(preview), state,
        Objects.requireNonNull(properties), sentiment, Objects.requireNonNull(entities),
        Objects.requireNonNull(responseSuggestion), Objects.requireNonNull(wordList), receiveDate,
        receiveTime, configuration);
  }

  @NonNull
  public String getText() {
    return text;
  }

  @Nullable
  public String getPreview() {
    return preview;
  }

  @NonNull
  public ComplaintState getState() {
    return state;
  }

  @Nullable
  public List<ComplaintProperty> getProperties() {
    return properties;
  }

  public double getSentiment() {
    return sentiment;
  }

  @Nullable
  public List<NamedEntity> getEntities() {
    return entities;
  }

  @Nullable
  public ResponseSuggestion getResponseSuggestion() {
    return responseSuggestion;
  }

  @Nullable
  public Map<String, Integer> getWordList() {
    return wordList;
  }

  @NonNull
  public LocalDate getReceiveDate() {
    return receiveDate;
  }

  @NonNull
  public LocalTime getReceiveTime() {
    return receiveTime;
  }

  @NonNull
  public Configuration getConfiguration() {
    return configuration;
  }

  public long getId() {
    return id;
  }
}