package de.fraunhofer.iao.querimonia.db.manager;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.complaint.ComplaintFactory;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.db.manager.filter.ComplaintFilter;
import de.fraunhofer.iao.querimonia.db.repository.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repository.LineStopCombinationRepository;
import de.fraunhofer.iao.querimonia.db.repository.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.log.LogCategory;
import de.fraunhofer.iao.querimonia.log.LogEntry;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.nlp.analyze.TokenAnalyzer;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.generation.DefaultResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.rest.restcontroller.ComplaintController;
import de.fraunhofer.iao.querimonia.rest.restobjects.Combination;
import de.fraunhofer.iao.querimonia.rest.restobjects.ComplaintUpdateRequest;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.fraunhofer.iao.querimonia.complaint.ComplaintState.*;

/**
 * Manager class for complaints.
 */
@Service
public class ComplaintManager {

  private static final Logger logger = LoggerFactory.getLogger(ComplaintManager.class);
  private static final int DEFAULT_TEXTS_DEFAUL_COUNT = 150;
  private final FileStorageService fileStorageService;
  private final ComplaintRepository complaintRepository;
  private final ComplaintFactory complaintFactory;
  private final ConfigurationManager configurationManager;
  private final LineStopCombinationRepository lineStopCombinationRepository;

  /**
   * Constructor gets only called by spring. Sets up the complaint manager.
   */
  @Autowired
  public ComplaintManager(FileStorageService fileStorageService,
                          ComplaintRepository complaintRepository,
                          @Qualifier("responseComponentRepository")
                              ResponseComponentRepository templateRepository,
                          ConfigurationManager configurationManager,
                          LineStopCombinationRepository lineStopCombinationRepository) {

    this.fileStorageService = fileStorageService;
    this.complaintRepository = complaintRepository;
    this.configurationManager = configurationManager;
    this.lineStopCombinationRepository = lineStopCombinationRepository;

    complaintFactory =
        new ComplaintFactory(new DefaultResponseGenerator(templateRepository),
            new TokenAnalyzer());
  }

  private static QuerimoniaException getNotFoundException(long complaintId) {
    return new NotFoundException("Es existiert keine Beschwerde mit der ID " + complaintId,
        complaintId);
  }

  /**
   * Returns the complaints of the database with filtering and sorting.
   *
   * @see ComplaintController#getComplaints(Optional, Optional, Optional, Optional, Optional,
   *     Optional, Optional, Optional, Optional) getComplaints
   */
  public synchronized List<Complaint> getComplaints(
      Optional<Integer> count, Optional<Integer> page, Optional<String[]> sortBy,
      Optional<String[]> state, Optional<String> dateMin, Optional<String> dateMax,
      Optional<String[]> sentiment, Optional<String[]> subject, Optional<String[]> keywords) {

    ArrayList<Complaint> result = new ArrayList<>();
    complaintRepository.findAll().forEach(result::add);

    Stream<Complaint> filteredResult =
        result.stream()
            .filter(complaint -> ComplaintFilter.filterByState(complaint, state))
            .filter(complaint -> ComplaintFilter.filterByDate(complaint, dateMin, dateMax))
            .filter(complaint -> ComplaintFilter.filterByEmotion(complaint, sentiment))
            .filter(complaint -> ComplaintFilter.filterBySubject(complaint, subject))
            .filter(complaint -> ComplaintFilter.filterByKeywords(complaint, keywords))
            .sorted(ComplaintFilter.createComplaintComparator(sortBy));

    if (count.isPresent()) {
      if (page.isPresent()) {
        // skip pages
        filteredResult = filteredResult
            .skip(page.get() * count.get());
      }
      // only take count amount of entries
      filteredResult = filteredResult.limit(count.get());
    }

    return filteredResult.collect(Collectors.toList());
  }

  /**
   * Upload method for complaints from files.
   *
   * @see ComplaintController#uploadComplaint(MultipartFile, Optional) uploadComplaint
   */
  public synchronized Complaint uploadComplaint(MultipartFile file, Optional<Long> configId) {
    String fileName = fileStorageService.storeFile(file);

    String text = fileStorageService.getTextFromData(fileName);
    return uploadText(new TextInput(text), configId);
  }

  /**
   * Methods for uploading raw text.
   *
   * @see ComplaintController#uploadText(TextInput, Optional) uploadText
   */
  public Complaint uploadText(TextInput input, Optional<Long> configId) {
    Configuration configuration = getConfigurationFromId(configId);

    var complaintBuilder = complaintFactory.createBaseComplaint(input.getText(), configuration);
    complaintBuilder.setState(ANALYSING);

    var complaint = complaintBuilder.createComplaint();
    // store unfinished state
    storeComplaint(complaint);
    // update id (otherwise the id would be 0 when returned)
    complaintBuilder.setId(complaint.getId());

    // run analysis
    runAnalysisAsync(complaintBuilder, false, ComplaintState.NEW);

    return complaint;
  }

  private Configuration getConfigurationFromId(Optional<Long> configId) {
    return configId
        // if given use the configuration with that id
        .map(configurationManager::getConfiguration)
        // use the currently active configuration else
        .orElseGet(configurationManager::getCurrentConfiguration);
  }

  /**
   * It called when the analysis throws an exception. It stores the complaint with the error state.
   */
  private void onException(ComplaintBuilder complaintBuilder, Throwable e) {
    complaintBuilder
        .setState(ERROR)
        .appendLogItem(LogCategory.ERROR, "Fehler bei Analyse: " + e.getMessage());
    storeComplaint(complaintBuilder.createComplaint());
  }


  /**
   * Method for getting a complaint with an id.
   *
   * @see ComplaintController#getComplaint(long) getComplaint
   */
  public synchronized Complaint getComplaint(long complaintId) {
    return complaintRepository.findById(complaintId)
        .orElseThrow(() -> getNotFoundException(complaintId));
  }

  /**
   * Returns the text of a complaint.
   *
   * @param complaintId the id of the complaint
   *
   * @return the text of the complaint.
   */
  public TextInput getText(long complaintId) {
    return new TextInput(getComplaint(complaintId).getText());
  }

  /**
   * Method for updating complaints.
   *
   * @see ComplaintController#updateComplaint(long, ComplaintUpdateRequest) updateComplaint
   */
  @SuppressWarnings("OptionalIsPresent")
  public Complaint updateComplaint(long complaintId, ComplaintUpdateRequest updateRequest) {
    Complaint complaint = getComplaint(complaintId);
    checkForbiddenStates(complaint, ERROR, CLOSED);
    ComplaintBuilder builder = new ComplaintBuilder(complaint);

    Optional<String> newEmotion = updateRequest.getNewEmotion();
    if (newEmotion.isPresent()) {
      var sentiment = complaint.getSentiment().withEmotion(new ComplaintProperty("Emotion",
          newEmotion.get()));
      builder
          .setSentiment(sentiment)
          .appendLogItem(LogCategory.GENERAL, "Emotion auf " + newEmotion.get() + " gesetzt");
    }

    Optional<Double> newTendency = updateRequest.getNewTendency();
    if (newTendency.isPresent()) {
      var sentiment = complaint.getSentiment().withTendency(newTendency.get());
      builder
          .setSentiment(sentiment)
          .appendLogItem(LogCategory.GENERAL, "Tendency auf " + newTendency.get() + " gesetzt");
    }

    Optional<String> newSubject = updateRequest.getNewSubject();
    if (newSubject.isPresent()) {
      builder
          .setValueOfProperty("Kategorie", newSubject.get())
          .appendLogItem(LogCategory.GENERAL, "Kategorie auf " + newSubject.get() + " gesetzt");
    }

    Optional<ComplaintState> newState = updateRequest.getNewState();
    if (newState.isPresent()) {
      builder
          .setState(newState.get())
          .appendLogItem(LogCategory.GENERAL, "Status der Beschwerde auf " + newState.get()
              + " gesetzt");
    }
    complaint = builder.createComplaint();
    storeComplaint(complaint);
    return complaint;
  }

  /**
   * Deletes a complaint with the given id.
   *
   * @see ComplaintController#deleteComplaint(long) deleteComplaint
   */
  public synchronized void deleteComplaint(long complaintId) {
    if (complaintRepository.existsById(complaintId)) {
      complaintRepository.deleteById(complaintId);
      logger.info("Deleted complaint with id {}", complaintId);
    } else {
      throw getNotFoundException(complaintId);
    }
  }

  /**
   * Reanalyzes a complaint.
   *
   * @see ComplaintController#refreshComplaint(long, Optional, Optional) refreshComplaint
   */
  public synchronized Complaint refreshComplaint(
      long complaintId,
      Optional<Boolean> keepUserInformation,
      Optional<Long> configId) {

    Complaint complaint = getComplaint(complaintId);
    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    checkForbiddenStates(complaint, ANALYSING, CLOSED);

    builder
        .setConfiguration(getConfigurationFromId(configId))
        .setState(ANALYSING);

    // run analysis
    runAnalysisAsync(builder, keepUserInformation.orElse(false), complaint.getState());
    complaint = builder.createComplaint();
    storeComplaint(complaint);
    return complaint;
  }

  /**
   * Runs the analysis of a complaint asynchronously.
   *
   * @param builder             the modifiable complaint.
   * @param keepUserInformation if this is true, values set by the user wont be overwritten.
   * @param state               the state the complaint should have after analysis.
   */
  private void runAnalysisAsync(ComplaintBuilder builder,
                                boolean keepUserInformation,
                                ComplaintState state) {
    Executors.newCachedThreadPool().submit(() -> {
      var future = CompletableFuture.runAsync(() -> {
        try {
          var newBuilder
              = complaintFactory.analyzeComplaint(builder, keepUserInformation);
          newBuilder.setState(state);
          storeComplaint(newBuilder.createComplaint());
        } catch (Exception e) {
          onException(builder, e);
        }
      }).orTimeout(10, TimeUnit.MINUTES);
      // time out the analysis after 10 minutes
      if (future.isCompletedExceptionally()) {
        onException(builder, new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Zeitüberschreitung bei Analyse", "Timeout"));
      }
    });
  }

  /**
   * Sets the state of a complaint to closed and executes all actions.
   *
   * @param complaintId the id of the complaint that should be closed.
   *
   * @return the closed complaint.
   *
   * @throws QuerimoniaException if the complaint with the given id does not exist or the
   *                             complaint cannot be closed in its state.
   * @see ComplaintController#closeComplaint(long) closeComplaint
   */
  public synchronized Complaint closeComplaint(long complaintId) {
    Complaint complaint = getComplaint(complaintId);
    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    checkForbiddenStates(complaint, ANALYSING, CLOSED);
    builder.setState(CLOSED);

    // execute actions of the complaint
    complaint
        .getResponseSuggestion()
        .getActions()
        .forEach(Action::executeAction);

    builder.appendLogItem(LogCategory.GENERAL, "Beschwerde geschlossen");

    complaint = builder.createComplaint();
    storeComplaint(complaint);

    return complaint;
  }

  /**
   * Counts the complaints.
   *
   * @see ComplaintController#countComplaints(Optional, Optional, Optional, Optional, Optional,
   *     Optional)  countComplaints
   */
  public synchronized String countComplaints(Optional<String[]> state, Optional<String> dateMin,
                                             Optional<String> dateMax, Optional<String[]> sentiment,
                                             Optional<String[]> subject, Optional<String[]> keywords
  ) {
    return "" + (getComplaints(Optional.empty(), Optional.empty(), Optional.empty(), state, dateMin,
        dateMax, sentiment, subject, keywords).size());
  }

  /**
   * Returns the log of a complaint.
   *
   * @param complaintId the id of the complaint.
   *
   * @return the log of a complaint.
   */
  public List<LogEntry> getLog(long complaintId) {
    return getComplaint(complaintId).getLog();
  }

  /**
   * Returns all entities of a complaint.
   *
   * @param complaintId the id of the complaint.
   *
   * @return all entities of a complaint.
   */
  public List<NamedEntity> getEntities(long complaintId) {
    return getComplaint(complaintId).getEntities();
  }

  /**
   * Adds a named entity to a complaint.
   *
   * @see ComplaintController#addEntity(long, NamedEntity) addEntity
   */
  public List<NamedEntity> addEntity(long complaintId, NamedEntity entity) {
    Complaint complaint = getComplaint(complaintId);
    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    checkForbiddenStates(complaint, ERROR, ANALYSING, CLOSED);
    checkValidityOfEntity(entity, complaint);

    List<NamedEntity> complaintEntities = builder.getEntities();
    if (!complaintEntities.contains(entity)) {
      // todo value formatting!
      complaintEntities.add(entity);
      builder.appendLogItem(LogCategory.GENERAL, "Entität " + entity + " hinzugefügt");
    } else {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
          "Die Entität mit Label " + entity.getLabel()
              + "existiert bereits!", "Entität bereits vorhanden");
    }

    complaint = builder.createComplaint();
    storeComplaint(complaint);
    // reload for entity id that is set by the database (otherwise the new entity has id 0)
    return getComplaint(complaintId).getEntities();
  }

  /**
   * Replaces an existing named entity of a complaint with a new entity.
   *
   * @param complaintId the id of the complaint.
   * @param entityId    the id of the entity.
   * @param entity      the new entity that replaces the entity with the given id.
   *
   * @return a updated list of entities of the given complaint.
   */
  public List<NamedEntity> updateEntity(long complaintId, long entityId, NamedEntity entity) {
    Complaint complaint = getComplaint(complaintId);
    checkForbiddenStates(complaint, ERROR, ANALYSING, CLOSED);
    checkValidityOfEntity(entity, complaint);

    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    NamedEntityBuilder entityBuilder = new NamedEntityBuilder(entity);
    entityBuilder.setId(entityId);
    var entities = builder.getEntities();

    // replace the entity with the given id with the new entity
    entities.replaceAll(namedEntity -> {
      if (namedEntity.getId() == entityId) {
        return entityBuilder.createNamedEntity();
      }
      return namedEntity;
    });
    // store changes
    complaint = builder.createComplaint();
    storeComplaint(complaint);
    // reload for entity id that is set by the database (otherwise the new entity has id 0)
    return getComplaint(complaintId).getEntities();
  }

  private void checkValidityOfEntity(NamedEntity entity, Complaint complaint) {
    // check validity of entity
    int start = entity.getStartIndex();
    int end = entity.getEndIndex();
    if (start < 0 || end <= start || end > complaint.getText().length()) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Die Entität ist ungültig. Alle "
          + "Indices müssen größer gleich null sein, der Startindex muss kleiner als der Endindex"
          + " sein und die Indices dürfen die Textgrenze nicht überschreiten,", "Ungültige "
          + "Entität");
    }
  }

  /**
   * Removes an entity in the database.
   *
   * @see ComplaintController#removeEntity(long, long) removeEntity
   */
  public List<NamedEntity> removeEntity(long complaintId, long entityId) {
    Complaint complaint = getComplaint(complaintId);
    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    checkForbiddenStates(complaint, ERROR, ANALYSING, CLOSED);

    List<NamedEntity> complaintEntities = builder.getEntities();
    List<NamedEntity> entitiesToRemove = complaintEntities
        .stream()
        .filter(namedEntity -> namedEntity.getId() == entityId)
        .collect(Collectors.toList());
    if (entitiesToRemove.isEmpty()) {
      throw new QuerimoniaException(HttpStatus.NOT_FOUND, "Die gegebene Entität existiert nicht "
          + "in der Beschwerde.", "Ungültige Entity");
    }
    for (NamedEntity namedEntity : entitiesToRemove) {
      complaintEntities.remove(namedEntity);
      builder.appendLogItem(LogCategory.GENERAL, "Entität " + namedEntity + " gelöscht");
    }

    storeComplaint(builder.createComplaint());
    return complaintEntities;
  }

  /**
   * Returns all entity combinations of a complaint. Entity combinations are combinations of
   * entities from the same context.
   *
   * @param complaintId the id of the complaint.
   *
   * @return the list of the combinations.
   */
  public List<Combination> getCombinations(long complaintId) {
    Complaint complaint = getComplaint(complaintId);
    var result = new HashSet<Combination>();

    // group by labels
    List<NamedEntity> lineEntities = complaint.getEntities().stream()
        .filter(namedEntity -> namedEntity.getLabel().equals("Linie"))
        .collect(Collectors.toList());
    List<NamedEntity> placeEntities = complaint.getEntities().stream()
        .filter(namedEntity -> namedEntity.getLabel().equals("Ort"))
        .collect(Collectors.toList());
    List<NamedEntity> stopEntities = complaint.getEntities().stream()
        .filter(namedEntity -> namedEntity.getLabel().equals("Haltestelle"))
        .collect(Collectors.toList());

    for (NamedEntity line : lineEntities) {
      for (NamedEntity place : placeEntities) {
        for (NamedEntity stop : stopEntities) {
          // combination of three
          if (lineStopCombinationRepository.existsByLineAndPlaceAndStop(line.getValue(),
              place.getValue(), stop.getValue())) {
            result.add(new Combination(List.of(line, place, stop)));
            // combination of line and place
          } else if (lineStopCombinationRepository.existsByLineAndPlace(line.getValue(),
              place.getValue())) {
            result.add(new Combination(List.of(line, place)));
            // combination of line and stop
          } else if (lineStopCombinationRepository.existsByLineAndStop(line.getValue(),
              stop.getValue())) {
            result.add(new Combination(List.of(line, stop)));

            // combination of line and stop
          } else if (lineStopCombinationRepository.existsByPlaceAndStop(place.getValue(),
              stop.getValue())) {
            result.add(new Combination(List.of(place, stop)));
          }
        }
      }
    }
    return new ArrayList<>(result);
  }

  /**
   * Uploads example complaint texts to the database and starts the analysis.
   *
   * @param count    how many example texts should be added. If this not given, {@value
   *                 #DEFAULT_TEXTS_DEFAUL_COUNT} texts will be added.
   * @param configId the id of the config that should be used for the analysis. If this is not
   *                 given, the active config will be used.
   *
   * @return the list of added complaints.
   *
   * @throws QuerimoniaException on an unexpected server error or if no config with the given id
   *                             exists in the database.
   *
   * @see ComplaintController#addDefaultComplaints(Optional, Optional) addDefaultComplaints
   */
  public List<Complaint> addExampleComplaints(Optional<Integer> count, Optional<Long> configId) {
    var textList = fileStorageService.getJsonObjectsFromFile(TextInput[].class,
        "DefaultTexts.json");
    return textList.stream()
        // only import count amount of texts
        .limit(count.orElse(DEFAULT_TEXTS_DEFAUL_COUNT))
        .map(textInput -> uploadText(textInput, configId))
        .collect(Collectors.toList());
  }

  /**
   * Removes all complaints.
   *
   * @see ComplaintController#deleteAllComplaints() deleteAllComplaints
   */
  public void deleteAllComplaints() {
    complaintRepository.deleteAll();
  }

  /**
   * Refreshed the response for a complaint.
   */
  public ResponseSuggestion refreshResponse(long complaintId) {
    Complaint complaint = getComplaint(complaintId);
    checkForbiddenStates(complaint, ERROR, ANALYSING, CLOSED);

    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    complaintFactory.createResponse(builder);

    storeComplaint(builder.createComplaint());
    return builder.getResponseSuggestion();
  }

  /**
   * Stores the complaint in the database.
   *
   * @param complaint the complaint that gets saved.
   */
  private synchronized void storeComplaint(Complaint complaint) {
    // store the configuration
    try {
      complaintRepository.save(complaint);
    } catch (Exception e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Speichern der "
          + "Beschwerde: " + e.getMessage(), e, "Beschwerde");
    }
    logger.info("Saved complaint with id {}", complaint.getId());
  }

  /**
   * Checks the state of the complaint. Throws an exception if the complaint state is one the
   * given forbidden states.
   *
   * @param complaint       the complaint to check.
   * @param forbiddenStates the states that are not allowed.
   *
   * @throws QuerimoniaException if the state of the complaint is one of the forbidden states.
   */
  private void checkForbiddenStates(Complaint complaint, ComplaintState... forbiddenStates) {
    var forbiddenStatesList = Arrays.asList(forbiddenStates);
    var exception = new IllegalStateException();

    if (forbiddenStatesList.contains(CLOSED)
        && complaint.getState().equals(CLOSED)) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Beschwerde kann nicht modifiziert "
          + "werden, da sie bereits geschlossen ist.", exception, "Beschwerde geschlossen");
    }
    if (forbiddenStatesList.contains(ANALYSING)
        && complaint.getState().equals(ANALYSING)) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Beschwerde kann nicht modifiziert "
          + "werden, während sie analysiert wird!", exception, "Beschwerde wird analysiert");
    }
    if (forbiddenStatesList.contains(ERROR)
        && complaint.getState().equals(ERROR)) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Beschwerde kann nicht modifiziert "
          + "werden, da die Analyse nicht abgeschlossen werden konnte. Starten Sie die Analyse "
          + "neu mit einer gültigen Konfiguration, um mit der Bearbeitung zu beginnen.", exception,
          "Beschwerde wurde nicht analysiert.");
    }
  }
}
