package de.fraunhofer.iao.querimonia.manager;

import de.fraunhofer.iao.querimonia.complaint.*;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.manager.filter.ComplaintFilter;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.nlp.analyze.TokenAnalyzer;
import de.fraunhofer.iao.querimonia.repository.CombinationRepository;
import de.fraunhofer.iao.querimonia.repository.ComplaintRepository;
import de.fraunhofer.iao.querimonia.repository.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.DefaultResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.PersistentResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.rest.restcontroller.ComplaintController;
import de.fraunhofer.iao.querimonia.rest.restcontroller.ResponseController;
import de.fraunhofer.iao.querimonia.rest.restobjects.CallbackResponse;
import de.fraunhofer.iao.querimonia.rest.restobjects.ComplaintUpdateRequest;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.utility.FileStorageService;
import de.fraunhofer.iao.querimonia.utility.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.utility.log.LogCategory;
import de.fraunhofer.iao.querimonia.utility.log.LogEntry;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.fraunhofer.iao.querimonia.complaint.ComplaintState.*;

/**
 * Manager class for complaints. Complaints can be added, edited and deleted.
 */
@Service
@EnableAsync
public class ComplaintManager {

  private static final Logger logger = LoggerFactory.getLogger(ComplaintManager.class);
  private static final int DEFAULT_TEXTS_DEFAULT_COUNT = 150;

  private final FileStorageService fileStorageService;
  private final ComplaintRepository complaintRepository;
  private final ComplaintFactory complaintFactory;
  private final ConfigurationManager configurationManager;
  private final CombinationRepository lineStopCombinationRepository;

  @Autowired
  private SimpMessagingTemplate template;

  /**
   * Constructor gets only called by spring. Sets up the complaint manager.
   */
  @Autowired
  public ComplaintManager(FileStorageService fileStorageService,
                          ComplaintRepository complaintRepository,
                          @Qualifier("responseComponentRepository")
                              ResponseComponentRepository templateRepository,
                          ConfigurationManager configurationManager,
                          CombinationRepository combinationRepository) {

    this.fileStorageService = fileStorageService;
    this.complaintRepository = complaintRepository;
    this.configurationManager = configurationManager;
    this.lineStopCombinationRepository = combinationRepository;

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
   * Optional, Optional, Optional, Optional) getComplaints
   */
  public synchronized List<Complaint> getComplaints(
      Optional<Integer> count, Optional<Integer> page, Optional<String[]> sortBy,
      Optional<String[]> state, Optional<String> dateMin, Optional<String> dateMax,
      Optional<String[]> sentiment, Optional<String[]> subject, Optional<String[]> keywords) {

    Stream<Complaint> filteredResult = getFilteredComplaints(sortBy, state, dateMin, dateMax,
        sentiment, subject, keywords);

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

  private Stream<Complaint> getFilteredComplaints(
      Optional<String[]> sortBy, Optional<String[]> state, Optional<String> dateMin,
      Optional<String> dateMax, Optional<String[]> sentiment, Optional<String[]> subject,
      Optional<String[]> keywords) {
    ArrayList<Complaint> result = new ArrayList<>();
    complaintRepository.findAll().forEach(result::add);

    return result.stream()
        .filter(complaint -> ComplaintFilter.filterByState(complaint, state))
        .filter(complaint -> ComplaintFilter.filterByDate(complaint, dateMin, dateMax))
        .filter(complaint -> ComplaintFilter.filterByEmotion(complaint, sentiment))
        .filter(complaint -> ComplaintFilter.filterBySubject(complaint, subject))
        .filter(complaint -> ComplaintFilter.filterByKeywords(complaint, keywords))
        .sorted(ComplaintFilter.createComplaintComparator(sortBy));
  }

  /**
   * Upload method for complaints from files. The text of the file gets extracted and the text
   * gets analyzed.
   *
   * @param file     the file that should be uploaded.
   * @param configId the id of the config that should be used for analysis, if not given the
   *                 active configuration gets used.
   * @return the new created complaint.
   * @throws QuerimoniaException on errors during upload, text extraction or text analysis.
   * @see ComplaintController#uploadComplaint(MultipartFile, Optional, Optional) uploadComplaint
   */
  public synchronized Complaint uploadComplaint(MultipartFile file, Optional<Long> configId,
                                                Optional<String> callbackUrl) {
    String fileName = fileStorageService.storeFile(file);
    String text = fileStorageService.getTextFromData(fileName);
    return uploadText(new TextInput(text), configId, callbackUrl);
  }

  /**
   * Method for uploading complaint texts.
   *
   * @param input    the text that should be uploaded.
   * @param configId the id of the config that should be used for analysis, if not given the
   *                 active configuration gets used.
   * @return the new created complaint.
   * @throws QuerimoniaException on errors during upload, text extraction or text analysis.
   * @see ComplaintController#uploadText(TextInput, Optional, Optional) uploadText
   */
  public synchronized Complaint uploadText(TextInput input, Optional<Long> configId,
                                           Optional<String> callbackUrl) {
    Configuration configuration = getConfigurationFromId(configId);

    var complaintBuilder = complaintFactory.createBaseComplaint(input.getText(), configuration);
    complaintBuilder.setCallbackRoute(callbackUrl.orElse(null));
    complaintBuilder.setState(ANALYSING);
    var complaint = complaintBuilder.createComplaint();
    // store unfinished state (otherwise the id would be 0 when returned)
    storeComplaint(complaint);
    // update id for builder
    complaintBuilder
        .setId(complaint.getId())
        .appendLogItem(LogCategory.GENERAL, "Beschwerde erstellt.");

    // run analysis
    runAnalysisAsync(complaintBuilder, false, ComplaintState.NEW);

    sendStateChange(complaint.getId(), null, ANALYSING);
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
   * This is called when the analysis throws an exception. It stores the complaint with the error
   * state.
   */
  private void onException(ComplaintBuilder complaintBuilder, Throwable e) {
    e.printStackTrace();
    sendStateChange(complaintBuilder.getId(), complaintBuilder.getState(), ERROR);
    complaintBuilder
        .setState(ERROR)
        .appendLogItem(LogCategory.ERROR, "Fehler bei Analyse: " + e.getMessage());
    storeComplaint(complaintBuilder.createComplaint());
  }


  /**
   * Method for getting a complaint with an id.
   *
   * @param complaintId the id of the complaint.
   * @return the complaint with the given id.
   * @throws NotFoundException when no complaint with the given id exists.
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
   * @return the text of the complaint.
   * @throws NotFoundException if no complaint with the given id exists.
   * @see ComplaintController#getText(long) getText
   */
  public TextInput getText(long complaintId) {
    return new TextInput(getComplaint(complaintId).getText());
  }

  /**
   * Returns the xml String of a complaint.
   *
   * @param complaintId the id of the complaint
   * @return the xml of the complaint
   * @throws NotFoundException if no complaint with the given id exists
   */
  public String getXml(long complaintId) {
    try {
      return getComplaint(complaintId).toXml();
    } catch (JAXBException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Xml konnte nicht erstellt "
          + "werden", "Xml-Converter Fehler");
    }
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
    // update emotion
    Optional<String> newEmotion = updateRequest.getNewEmotion();
    if (newEmotion.isPresent()) {
      var sentiment = complaint.getSentiment().withEmotion(new ComplaintProperty("Emotion",
          newEmotion.get()));
      builder
          .setSentiment(sentiment)
          .appendLogItem(LogCategory.GENERAL, "Emotion auf " + newEmotion.get() + " gesetzt");
    }
    // update tendency
    Optional<Double> newTendency = updateRequest.getNewTendency();
    if (newTendency.isPresent()) {
      var sentiment = complaint.getSentiment().withTendency(newTendency.get());
      builder
          .setSentiment(sentiment)
          .appendLogItem(LogCategory.GENERAL, "Tendency auf " + newTendency.get() + " gesetzt");
    }
    // update category
    Optional<String> newSubject = updateRequest.getNewSubject();
    if (newSubject.isPresent()) {
      builder
          .setValueOfProperty("Kategorie", newSubject.get())
          .appendLogItem(LogCategory.GENERAL, "Kategorie auf " + newSubject.get() + " gesetzt");
    }
    // update state
    Optional<ComplaintState> newState = updateRequest.getNewState();
    if (newState.isPresent()) {
      sendStateChange(builder.getId(), builder.getState(), newState.get());
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
  public Complaint refreshComplaint(
      long complaintId,
      Optional<Boolean> keepUserInformation,
      Optional<Long> configId) {

    Complaint complaint = getComplaint(complaintId);
    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    checkForbiddenStates(complaint, CLOSED);

    sendStateChange(builder.getId(), builder.getState(), ANALYSING);
    builder
        .setConfiguration(getConfigurationFromId(configId))
        .setState(ANALYSING);
    // run analysis
    var state = complaint.getState();
    if (state == ERROR || state == ANALYSING) {
      state = NEW;
    }
    runAnalysisAsync(builder, keepUserInformation.orElse(false), state);
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
          sendStateChange(builder.getId(), builder.getState(), state);
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
   * @return the closed complaint.
   * @throws QuerimoniaException if the complaint with the given id does not exist or the
   *                             complaint cannot be closed in its state.
   * @see ComplaintController#closeComplaint(long) closeComplaint
   */
  public synchronized Complaint closeComplaint(long complaintId) {
    Complaint complaint = getComplaint(complaintId);
    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    checkForbiddenStates(complaint, ANALYSING, CLOSED);
    sendStateChange(complaintId, builder.getState(), CLOSED);
    builder.setState(CLOSED)
        .setCloseDate(LocalDate.now(ZoneId.of("Europe/Berlin")))
        .setCloseTime(LocalTime.now(ZoneId.of("Europe/Berlin")));

    // execute actions of the complaint
    complaint
        .getResponseSuggestion()
        .getResponseComponents()
        .stream()
        .filter(CompletedResponseComponent::isUsed)
        .map(CompletedResponseComponent::getComponent)
        .map(PersistentResponseComponent::getActions)
        .flatMap(List::stream)
        .forEach(Action::executeAction);
    builder.appendLogItem(LogCategory.GENERAL, "Beschwerde geschlossen");
    executeCallback(builder);
    complaint = builder.createComplaint();
    storeComplaint(complaint);

    return complaint;
  }

  private void executeCallback(ComplaintBuilder builder) {
    var url = builder.getCallbackRoute();

    if (url != null) {
      var complaintResponse = new CallbackResponse(builder.getId(),
          builder.getResponseSuggestion().getResponse());
      try {

        var template = new RestTemplateBuilder()
            .basicAuthentication("admin", "QuerimoniaPass2019")
            .build();
        var response = template.postForEntity(url, complaintResponse, String.class);
        if (response.getStatusCodeValue() >= 500) {
          throw new RuntimeException(
              "Callback meldet StatusCode " + response.getStatusCodeValue() + "; "
                  + response.getBody());
        }
        builder.appendLogItem(LogCategory.GENERAL, "Callback erfolgreich: " + response.getBody());
      } catch (RuntimeException e) {
        builder.appendLogItem(LogCategory.ERROR,
            "Fehler beim Abschließen der Beschwerde: " + e.getMessage());
      }
    }
  }

  /**
   * Updates the response of a complaint.
   *
   * @param complaintId the id of the complaint.
   * @param body        the new response.
   * @return the new response.
   */
  public synchronized ResponseSuggestion saveResponse(long complaintId, TextInput body) {
    Complaint complaint = getComplaint(complaintId);
    checkForbiddenStates(complaint, ERROR, ANALYSING, CLOSED);
    var newSuggestion = complaint.getResponseSuggestion().withResponse(body.getText());
    storeComplaint(complaint.withResponseSuggestion(newSuggestion));
    return newSuggestion;
  }

  /**
   * Sets the used flag of a response component.
   *
   * @param complaintId the id of the complaint.
   * @param componentId the id of the component.
   * @param used        if it is used.
   * @return the updated response.
   */
  public synchronized ResponseSuggestion setUsed(long complaintId, long componentId, boolean used) {
    var complaint = getComplaint(complaintId);
    checkForbiddenStates(complaint, ERROR, ANALYSING, CLOSED);
    var response = complaint.getResponseSuggestion();
    var components = new ArrayList<>(response.getResponseComponents());

    // update the component with the given id
    components.replaceAll(completedResponseComponent -> {
      if (completedResponseComponent.getId() == componentId) {
        return completedResponseComponent.withUsed(used);
      }
      return completedResponseComponent;
    });

    var newResponse = response.withResponseComponents(components);
    storeComplaint(complaint.withResponseSuggestion(newResponse));
    return newResponse;
  }

  /**
   * Return the amount of complaints that match the given parameters.
   *
   * @see ComplaintController#countComplaints(Optional, Optional, Optional, Optional, Optional,
   * Optional)  countComplaints
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
   * @return the log of a complaint.
   * @throws NotFoundException if no complaint with the given id exists.
   * @see ComplaintController#getLog(long) getLog
   */
  public List<LogEntry> getLog(long complaintId) {
    return getComplaint(complaintId).getLog();
  }

  /**
   * Returns all entities of a complaint.
   *
   * @param complaintId the id of the complaint.
   * @return all entities of a complaint.
   * @throws NotFoundException if no complaint with the given id exists.
   * @see ComplaintController#getEntities(long) getEntities
   */
  public List<NamedEntity> getEntities(long complaintId) {
    var complaint = getComplaint(complaintId);
    return complaint.getEntities();
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

    if (entity.isPreferred()) {
      clearPreferredEntities(builder, entity.getLabel());
    }

    List<NamedEntity> complaintEntities = builder.getEntities();
    if (!complaintEntities.contains(entity)) {
      // only add if not already there, dont allow preferred flag
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

    if (entity.isPreferred()) {
      clearPreferredEntities(builder, entity.getLabel());
    }

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
    if (start < 0 || end < start || end > complaint.getText().length()) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Die Entität ist ungültig. Alle "
          + "Indices müssen größer gleich null sein, der Startindex muss kleiner als der Endindex"
          + " sein und die Indices dürfen die Textgrenze nicht überschreiten,", "Ungültige "
          + "Entität");
    }
  }

  /**
   * Sets the preferred flag of all entities to false.
   *
   * @param complaintBuilder the complaint.
   * @param label            all entities with this label will be set to not preferred.
   */
  private void clearPreferredEntities(ComplaintBuilder complaintBuilder, String label) {
    var entities = complaintBuilder.getEntities();
    // set preferred to false when label is matching
    entities.replaceAll(entity -> {
      if (entity.getLabel().equals(label)) {
        return entity.withPreferred(false);
      }
      return entity;
    });
    complaintBuilder.setEntities(entities);
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
    return getComplaint(complaintId).getEntities();
  }

  /**
   * Returns all entity combinations of a complaint. Entity combinations are combinations of
   * entities from the same context.
   *
   * @param complaintId the id of the complaint.
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
          createCombinations(result, line.getValue(), place.getValue(), stop.getValue());
        }
      }
    }
    // also find combinations of two
    for (NamedEntity place : placeEntities) {
      for (NamedEntity stop : stopEntities) {
        createCombinations(result, null, place.getValue(), stop.getValue());
      }
    }
    for (NamedEntity line : lineEntities) {
      for (NamedEntity stop : stopEntities) {
        createCombinations(result, line.getValue(), null, stop.getValue());
      }
    }
    for (NamedEntity place : placeEntities) {
      for (NamedEntity line : lineEntities) {
        createCombinations(result, line.getValue(), place.getValue(), null);
      }
    }
    return new ArrayList<>(result);
  }

  private void createCombinations(HashSet<Combination> result, String line,
                                  String place, String stop) {
    // combination of three
    if (lineStopCombinationRepository.existsByLineAndPlaceAndStop(line, place,
        stop)) {
      result.add(new Combination(line, stop, place));
      // combination of line and place
    } else if (lineStopCombinationRepository.existsByLineAndPlace(line, place)
        && isCombinationAbsent(result, line, null, place)) {
      result.add(new Combination(line, null, place));
      // combination of line and stop
    } else if (lineStopCombinationRepository.existsByLineAndStop(line, stop)
        && isCombinationAbsent(result, line, stop, null)) {
      result.add(new Combination(line, stop, null));

      // combination of line and stop
    } else if (lineStopCombinationRepository.existsByPlaceAndStop(place, stop)
        && isCombinationAbsent(result, null, stop, place)) {
      result.add(new Combination(null, stop, place));
    }
  }

  private boolean isCombinationAbsent(HashSet<Combination> combinations,
                                      String line, String stop, String place) {
    return combinations.stream()
        .filter(combination -> line == null || Objects.equals(line, combination.getLine()))
        .filter(combination -> place == null || Objects.equals(place, combination.getPlace()))
        .noneMatch(combination -> stop == null || stop.equals(combination.getStop()));
  }

  /**
   * Uploads example complaint texts to the database and starts the analysis.
   *
   * @param count    how many example texts should be added. If this not given, {@value
   *                 #DEFAULT_TEXTS_DEFAULT_COUNT} texts will be added.
   * @param configId the id of the config that should be used for the analysis. If this is not
   *                 given, the active config will be used.
   * @return the list of added complaints.
   * @throws QuerimoniaException on an unexpected server error or if no config with the given id
   *                             exists in the database.
   * @see ComplaintController#addDefaultComplaints(Optional, Optional) addDefaultComplaints
   */
  public List<Complaint> addExampleComplaints(Optional<Integer> count, Optional<Long> configId) {
    var textList = fileStorageService.getJsonObjectsFromFile(TextInput[].class,
        "DefaultTexts.json");
    return textList.stream()
        // only import count amount of texts
        .limit(count.orElse(DEFAULT_TEXTS_DEFAULT_COUNT))
        .map(textInput -> {
          var complaint = uploadText(textInput, configId, Optional.empty());
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            logger.error("Safety pause between default complaints interrupted");
          }
          return complaint;
        })
        .collect(Collectors.toList());
  }

  /**
   * Removes all complaints.
   *
   * @see ComplaintController#deleteAllComplaints() deleteAllComplaints
   */
  public void deleteAllComplaints() {
    complaintRepository.deleteAll();
    logger.info("Deleted all complaints.");
  }

  /**
   * Refreshed the response for a complaint.
   *
   * @param complaintId the id of the complaint.
   * @return the new creates response
   * @throws NotFoundException if no complaint with the given id exists.
   * @see ResponseController#refreshResponse(long) refreshResponse
   */
  public ResponseSuggestion refreshResponse(long complaintId) {
    Complaint complaint = getComplaint(complaintId);
    checkForbiddenStates(complaint, ERROR, ANALYSING, CLOSED);

    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    complaintFactory.createResponse(builder);
    storeComplaint(builder.createComplaint());

    // reloading from db for correct ids
    return getComplaint(complaintId).getResponseSuggestion();
  }

  /**
   * Returns all complaints with the given state.
   *
   * @param state the state of the complaints.
   * @return a list of all complaints that are in the given state.
   */
  public List<Complaint> getComplaintsWithState(ComplaintState state) {
    return complaintRepository.findAllByState(state);
  }

  /**
   * Stores the complaint in the database.
   *
   * @param complaint the complaint that gets saved.
   */
  @Transactional
  public synchronized void storeComplaint(Complaint complaint) {
    // store the configuration
    try {
      complaintRepository.save(complaint);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Speichern der "
          + "Beschwerde: " + e.getMessage(), e, "Beschwerde konnte nicht gespeichert werden");
    }
    logger.info("Saved complaint with id {}", complaint.getId());
  }

  /**
   * Checks the state of the complaint. Throws an exception if the complaint state is one the
   * given forbidden states.
   *
   * @param complaint       the complaint to check.
   * @param forbiddenStates the states that are not allowed.
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

  /**
   * Returns the complaints of the database with filtering and sorting in one xml.
   *
   * @param sortBy   an array of sorting aspects. The order in the array represents the priority
   *                 for the sorting: The complaints get sorted by the first array entry first, and
   *                 then by the second, etc. Valid sort aspects are: <code>upload_date_asc,
   *                 upload_date_desc, subject_asc, subject_desc, sentiment_asc,
   *                 sentiment_desc}</code>
   * @param state    If given, only complaints in that state will be returned.
   * @param dateMin  If given, no complaints that were uploaded before that date will be returned.
   * @param dateMax  If given, no complaints that were uploaded after that date will be returned.
   * @param emotion  If given, only complaints with this emotion will be returned.
   * @param subject  If given, only complaints with this subject will be returned.
   * @param keywords If given, only complaints that contain the keywords will returned.
   * @return a response entity with the following contents:
   * <ul>
   * <li>status code 200 and a xml of the sorted, filtered complaints
   * setting as response body on success.</li>
   * <li>status code 400 and a the exception as response body when the sorting parameters are
   * invalid</li>
   * <li>status code 500 and the exception as response body on an unexpected server error.</li>
   * </ul>
   */
  public String getXmls(Optional<String[]> sortBy, Optional<String[]> state,
                        Optional<String> dateMin, Optional<String> dateMax,
                        Optional<String[]> emotion, Optional<String[]> subject,
                        Optional<String[]> keywords) {
    Stream<Complaint> filteredComplaint = getFilteredComplaints(sortBy, state, dateMin, dateMax,
        emotion, subject, keywords);
    try {
      return Complaint.getXmls(filteredComplaint);
    } catch (JAXBException e) {
      e.printStackTrace();
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Xml konnte nicht erstellt "
          + "werden", "Xml-Converter Fehler");
    }
  }

  private synchronized void sendStateChange(long id, ComplaintState oldState, ComplaintState newState) {
    JSONObject response = new JSONObject();
    try {
      response.put("id", id);
      response.put("oldState", oldState);
      response.put("state", newState);
    } catch (JSONException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e, "Fehler "
          + "bei erstellen der Socket Nachricht");
    }
    if (template != null) {
      template.convertAndSend("/complaints/state", response.toString());
    }

  }

  public Object parse(String xmlInput) {
    // create JAXB context and instantiate marshaller
    JAXBContext context;
    Object obj = null;

    try {
      context = JAXBContextFactory.createContext(new Class[] {ComplaintXml.class,
          ComplaintXml.ComplaintXmls.class}, null);
      Unmarshaller um = context.createUnmarshaller();
      obj = um.unmarshal(new StringReader(xmlInput));
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    ComplaintXml oneXml;
    ComplaintXml.ComplaintXmls multipleXml;
    if (obj instanceof ComplaintXml) {
      oneXml = (ComplaintXml) obj;
      return oneXml;
    } else if (obj instanceof ComplaintXml.ComplaintXmls) {
      multipleXml = (ComplaintXml.ComplaintXmls) obj;
      return multipleXml;
    } else {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Xml parsing error", "Xml");
    }
  }
}
