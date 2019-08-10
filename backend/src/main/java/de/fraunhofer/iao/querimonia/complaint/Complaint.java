package de.fraunhofer.iao.querimonia.complaint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.log.LogEntry;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.Sentiment;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;
import tec.uom.lib.common.function.Identifiable;
import org.eclipse.persistence.jaxb.JAXBContextFactory;


import javax.persistence.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is represents a complaint. A complaint is the main data structure of querimonia
 * that contains all information about a complaint or a different kind of message. This includes
 * the results of the analysis processes of Querimonia.
 * <p>
 * To create a complaint, use a {@link ComplaintBuilder} to create or modify a complaint with all
 * properties. For creating a complaint from only its text, use a {@link ComplaintFactory}.
 * </p>
 * <p><h2>{@link ComplaintState States of complaints}</h2></p>
 * <p>Complaints have different states during the workflow:</p>
 * <ul>
 *   <li>{@link ComplaintState#ANALYSING}: Complaint that get uploaded start in this state. The
 *   analysis has started, but is not finished yet. The complaint has no entities, categories,
 *   sentiment and response yet. Deleting and editing is not allowed in this state.
 *   </li>
 *   <li>{@link ComplaintState#ERROR}: If the analysis could not be finished, complaints will
 *   be in this state. They can't be edited in this state, but removed and reanalysed.</li>
 *   <li>{@link ComplaintState#NEW}: If the analysis has finished, complaints are in this state.
 *   All editing and deleting is allowed.</li>
 *   <li>{@link ComplaintState#IN_PROGRESS}: The complaint can be manually set to this state to
 *   indicate, that the work by the user on this complaint has begun. All editing and deleting is
 *   allowed.</li>
 *   <li>{@link ComplaintState#CLOSED}: The work on the complaint is finished. All actions are
 *   executed. Editing is not allowed in this state, but deleting.</li>
 * </ul>
 */
@XmlRootElement(name = "fraunhoferTextDocument")
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@JsonPropertyOrder(value = {
    "id",
    "text",
    "preview",
    "state",
    "configuration",
    "receiveDate",
    "receiveTime",
    "sentiment",
    "properties",
    "entities"
})
public class Complaint implements Identifiable<Long> {

  /**
   * This is the size of the text column in the database. Complaint texts must not be longer than
   * this.
   */
  public static final int TEXT_MAX_LENGTH = 10000;

  /**
   * This is the size of the preview column in the database. The preview string may not be longer
   * than this.
   */
  public static final int PREVIEW_MAX_LENGTH = 250;

  /**
   * The primary key for the complaints in the database.
   */
  @Id
  @JsonProperty("id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  private long complaintId;

  /**
   * The complaint message, is limited to {@value TEXT_MAX_LENGTH} characters.
   */
  @Column(length = TEXT_MAX_LENGTH)
  @NonNull
  private String text = "";

  /**
   * A preview of the complaint message, should be the first two lines. Length is limited to
   * {@value PREVIEW_MAX_LENGTH} characters
   */
  @Column(length = PREVIEW_MAX_LENGTH)
  @NonNull
  private String preview = "";

  /**
   * The state of the complaint. A complaint is either new, in progress or closed.
   */
  @Enumerated(EnumType.STRING)
  @NonNull
  private ComplaintState state = ComplaintState.NEW;

  /**
   * Additional properties for the complaint like the category, that get found by classifiers.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "complaint_id")
  @NonNull
  private List<ComplaintProperty> properties = new ArrayList<>();

  /**
   * A value that represents the sentiment of the text. Negative values represent negative
   * sentiment, positive value means positive sentiment.
   */
  @OneToOne(cascade = CascadeType.ALL)
  @NonNull
  private Sentiment sentiment = new Sentiment();

  /**
   * The list of all named entities in the complaint text.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "complaint_id")
  @NonNull
  private List<NamedEntity> entities = new ArrayList<>();

  /**
   * The response for the complaint.
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JsonIgnore
  @NonNull
  private ResponseSuggestion responseSuggestion = new ResponseSuggestion();

  /**
   * A list of all words in the complaint text, which are not stop words, mapped to their absolute
   * count.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "word_list_table", joinColumns = @JoinColumn(name = "complaintId"))
  @MapKeyColumn(name = "words")
  @Column(name = "count")
  @NonNull
  @JsonIgnore
  private Map<String, Integer> wordList = new HashMap<>();

  /**
   * The date when the complaint was received.
   */
  @NonNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate receiveDate = LocalDate.now();
  @NonNull
  private LocalTime receiveTime = LocalTime.now();

  /**
   * The configuration which was used to analyze the complaint.
   */
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "config_id")
  @NonNull
  private Configuration configuration = Configuration.FALLBACK_CONFIGURATION;

  /**
   * Contains the log of the complaint.
   */
  @NonNull
  @JsonIgnore
  @OneToMany(cascade = CascadeType.ALL)
  private List<LogEntry> log = new ArrayList<>();

  /**
   * Constructor for builder.
   */
  Complaint(
      long id,
      @NonNull String text,
      @NonNull String preview,
      @NonNull ComplaintState state,
      @NonNull List<ComplaintProperty> properties,
      @NonNull Sentiment sentiment,
      @NonNull List<NamedEntity> entities,
      @NonNull ResponseSuggestion responseSuggestion,
      @NonNull Map<String, Integer> wordList,
      @NonNull LocalDate receiveDate,
      @NonNull LocalTime receiveTime,
      @NonNull Configuration configuration,
      @NonNull List<LogEntry> log) {

    ComplaintUtility.checkStringLength(text, TEXT_MAX_LENGTH);
    ComplaintUtility.checkStringLength(preview, PREVIEW_MAX_LENGTH);

    this.complaintId = id;
    this.text = text;
    this.preview = preview;
    this.state = state;
    this.properties = properties;
    this.sentiment = sentiment;
    this.entities = entities;
    this.responseSuggestion = responseSuggestion;
    this.wordList = wordList;
    this.receiveDate = receiveDate;
    this.receiveTime = receiveTime;
    this.configuration = configuration;
    this.log = log;
  }

  /**
   * Empty default constructor (only used for hibernate).
   */
  @SuppressWarnings("unused")
  private Complaint() {

  }

  /**
   * Returns the property of the complaint which is labeled with category.
   *
   * @return the property of the complaint which is labeled with category.
   */
  @JsonIgnore
  @XmlPath("output/analysis[2][@type='classifier'/output")
  public ComplaintProperty getSubject() {
    return ComplaintUtility.getPropertyOfComplaint(this, "Kategorie");
  }

  /**
   * Returns the unique id of the complaint. This is the primary key in the database. Each
   * complaint can be identified with this id.
   *
   * @return the unique id of the complaint.
   */
  @JsonProperty("id")
  @Override
  @NonNull
  public Long getId() {
    return complaintId;
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
   * paragraphs or sentences of the complaint, limited to {@value PREVIEW_MAX_LENGTH} characters.
   *
   * @return the preview of the complaint text.
   */
  @NonNull
  public String getPreview() {
    return preview;
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
   * Returns the list of {@link NamedEntity named entities} that occur in the complaint text.
   *
   * @return the list of {@link NamedEntity named entities} that occur in the complaint text.
   */
  @NonNull
  @XmlPath("output/analysis[1][@type='extractor']/output/annotation")
  public List<NamedEntity> getEntities() {
    return new ArrayList<>(entities);
  }

  /**
   * Returns a map that maps the words of the complaint text to their frequency. Note that not
   * possibly not every word is listed here as key, since stop words get filtered out in the
   * analysis process.
   *
   * @return a map that maps the words of the complaint text to their frequency.
   */
  @NonNull
  @JsonIgnore
  public Map<String, Integer> getWordCounts() {
    return new HashMap<>(wordList);
  }

  /**
   * Returns the generated {@link ResponseSuggestion response} of the complaint.
   *
   * @return the generated {@link ResponseSuggestion response} of the complaint.
   */
  @NonNull
  @JsonIgnore
  public ResponseSuggestion getResponseSuggestion() {
    return responseSuggestion;
  }

  /**
   * Returns the {@link ComplaintState state} of the complaint.
   *
   * @return the {@link ComplaintState state} of the complaint.
   */
  @XmlPath("input/metadata/metadata[@key='state']/text()")
  @NonNull
  public ComplaintState getState() {
    return state;
  }
  /**
   * Returns a new complaint with all the properties of this complaint but with the given state
   * as the complaint state attribute.
   *
   * @param state the new state, which the copied complaint should have.
   *
   * @return a new complaint with all the properties of this complaint but with the given state
   *     as the complaint state attribute.
   */
  @NonNull
  public Complaint withState(@NonNull ComplaintState state) {
    return new ComplaintBuilder(this)
        .setState(state)
        .createComplaint();
  }

  /**
   * Returns the configuration that was used to analyze this complaint. The configuration
   * contains information about the tools that where used in the analysis process.
   *
   * @return the configuration used to analyze this complaint.
   */
  @NonNull
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Returns a new complaint with all the properties of this complaint but with the given
   * configuration as attribute.
   *
   * @param configuration the new configuration, which the copied complaint should have.
   *
   * @return a new complaint with all the properties of this complaint but with the given
   *     configuration as attribute.
   */
  public Complaint withConfiguration(Configuration configuration) {
    return new ComplaintBuilder(this)
        .setConfiguration(configuration)
        .createComplaint();
  }

  /**
   * Returns the {@link ComplaintProperty properties} of the complaint. The properties represent
   * the results of the analyzers that were used to analyze the complaint.
   *
   * @return the {@link ComplaintProperty properties} of the complaint.
   */
  @NonNull
  public List<ComplaintProperty> getProperties() {
    return new ArrayList<>(properties);
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
   * Returns the log of the complaint. The complaint log contains all information about the
   * analysis and edits of the complaint. It also logs errors.
   *
   * @return a list of {@link LogEntry log entries}.
   */
  @NonNull
  public List<LogEntry> getLog() {
    return new ArrayList<>(log);
  }

  /**
   * Formatting the receiveDate and Time for xml creation
   * @return String to display in the xml
   */
  @XmlPath("input/metadata/date[@type='creation']/text()")
  private String getDate(){
    return this.receiveDate.atTime(receiveTime).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+('Z');
  }

  /**
   * Checks if two complaints are considered equal. A complaint is equal to another if their
   * text, state, subject, emotion, entities and their response is equal.
   *
   * @param o the other complaint. Must be a complaint object.
   *
   * @return true, if the given object is a complaint object and the two complaint objects are
   *     equal.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Complaint complaint = (Complaint) o;

    return new EqualsBuilder()
        .append(sentiment, complaint.sentiment)
        .append(text, complaint.text)
        .append(state, complaint.state)
        .append(properties, complaint.properties)
        .append(entities, complaint.entities)
        .append(responseSuggestion, complaint.responseSuggestion)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 19)
        .append(text)
        .append(state)
        .append(properties)
        .append(sentiment)
        .append(entities)
        .append(responseSuggestion)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("complaintId", complaintId)
        .append("text", text)
        .append("preview", preview)
        .append("state", state)
        .append("properties", properties)
        .append("sentiment", sentiment)
        .append("entities", entities)
        .append("responseSuggestion", responseSuggestion)
        .append("wordList", wordList)
        .append("receiveDate", receiveDate)
        .append("receiveTime", receiveTime)
        .append("configuration", configuration)
        .toString();
  }

  public static void main(String[] args) throws JAXBException, IOException {
    // create JAXB context and instantiate marshaller
    JAXBContext context = JAXBContextFactory.createContext(new Class[]{Complaint.class}, null);
    System.out.println(context.getClass().getName());
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(Marshaller.JAXB_FRAGMENT, true);


    RestTemplate template = new RestTemplate();

    String complaintStr = template.getForObject("https://querimonia.iao.fraunhofer" +
        ".de/dev/api/complaints/434", String.class);
    System.out.println(complaintStr);
//    complaintStr = complaintStr.replaceFirst("2019-07-31", "31.07.2019");
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    Complaint complaint = mapper.readValue(complaintStr, Complaint.class);

    m.marshal(complaint, System.out);


  }
}
