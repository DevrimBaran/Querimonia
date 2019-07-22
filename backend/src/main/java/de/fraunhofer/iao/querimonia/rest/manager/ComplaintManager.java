package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.complaint.ComplaintFactory;
import de.fraunhofer.iao.querimonia.complaint.ComplaintProperty;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import de.fraunhofer.iao.querimonia.config.Configuration;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.exception.NotFoundException;
import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.Sentiment;
import de.fraunhofer.iao.querimonia.nlp.analyze.TokenAnalyzer;
import de.fraunhofer.iao.querimonia.response.action.Action;
import de.fraunhofer.iao.querimonia.response.generation.DefaultResponseGenerator;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;
import de.fraunhofer.iao.querimonia.rest.manager.filter.ComplaintFilter;
import de.fraunhofer.iao.querimonia.rest.restcontroller.ComplaintController;
import de.fraunhofer.iao.querimonia.rest.restobjects.ComplaintUpdateRequest;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manager class for complaints.
 */
@Service
public class ComplaintManager {

  private static final Logger logger = LoggerFactory.getLogger(ComplaintManager.class);
  private final FileStorageService fileStorageService;
  private final ComplaintRepository complaintRepository;
  private final ComplaintFactory complaintFactory;
  private final ConfigurationManager configurationManager;

  /**
   * Constructor gets only called by spring. Sets up the complaint manager.
   */
  @Autowired
  public ComplaintManager(FileStorageService fileStorageService,
                          ComplaintRepository complaintRepository,
                          @Qualifier("responseComponentRepository")
                              ResponseComponentRepository templateRepository,
                          ConfigurationManager configurationManager) {

    this.fileStorageService = fileStorageService;
    this.complaintRepository = complaintRepository;
    this.configurationManager = configurationManager;

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
    Configuration configuration = configId
        // if given use the configuration with that id
        .map(configurationManager::getConfiguration)
        // use the currently active configuration else
        .orElseGet(configurationManager::getCurrentConfiguration);

    Complaint complaint = complaintFactory.createComplaint(input.getText(), configuration);
    storeComplaint(complaint);
    return complaint;
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
   * Method for updating complaints.
   *
   * @see ComplaintController#updateComplaint(long, ComplaintUpdateRequest) updateComplaint
   */
  public Complaint updateComplaint(long complaintId, ComplaintUpdateRequest updateRequest) {
    Complaint complaint = getComplaint(complaintId);
    checkState(complaint);
    ComplaintBuilder builder = new ComplaintBuilder(complaint);

    if (updateRequest.getNewEmotion().isPresent()) {
      builder.setSentiment(complaint.getSentiment().withEmotion(new ComplaintProperty("Emotion",
          updateRequest.getNewEmotion().get())));
    }
    if (updateRequest.getNewSubject().isPresent()) {
      builder.setValueOfProperty("Kategorie", updateRequest.getNewSubject().get());
    }
    if (updateRequest.getNewState().isPresent()) {
      builder.setState(updateRequest.getNewState().get());
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
    checkState(complaint);

    Configuration configuration = configId
        // if given use the configuration with that id
        .map(configurationManager::getConfiguration)
        // use the currently active configuration else
        .orElseGet(configurationManager::getCurrentConfiguration);
    builder.setConfiguration(configuration);

    complaint = complaintFactory.analyzeComplaint(builder,
        keepUserInformation.orElse(false));
    storeComplaint(complaint);
    return complaint;
  }

  /**
   * Sets the state of a complaint to closed and executes all actions.
   *
   * @param complaintId the id of the complaint that should be closed.
   * @return the closed complaint.
   */
  public synchronized Complaint closeComplaint(long complaintId) {
    Complaint complaint = getComplaint(complaintId);
    checkState(complaint);
    complaint = complaint.withState(ComplaintState.CLOSED);

    // execute actions of the complaint
    complaint
        .getResponseSuggestion()
        .getActions()
        .forEach(Action::executeAction);

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
    // check validity of entity
    int start = entity.getStartIndex();
    int end = entity.getEndIndex();
    if (start < 0 || end <= start || end > complaint.getText().length()) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Die Entität ist ungültig. Alle "
          + "Indices müssen größer gleich null sein, der Startindex muss kleiner als der Endindex"
          + " sein und die Indices dürfen die Textgrenze nicht überschreiten,", "Ungültige "
          + "Entität");
    }

    List<NamedEntity> complaintEntities = complaint.getEntities();
    if (!complaintEntities.contains(entity)) {
      complaintEntities.add(entity);
    } else {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
          "Die Entität mit Label " + entity.getLabel()
              + "existiert bereits!", "Entität bereits vorhanden");
    }

    storeComplaint(complaint);
    return complaintEntities;
  }

  /**
   * Removes an entity in the database.
   *
   * @see ComplaintController#removeEntity(long, long) removeEntity
   */
  public List<NamedEntity> removeEntity(long complaintId, long entityId) {
    Complaint complaint = getComplaint(complaintId);


    List<NamedEntity> complaintEntities = complaint.getEntities();
    List<NamedEntity> entitiesToRemove = complaintEntities
        .stream()
        .filter(namedEntity -> namedEntity.getId() == entityId)
        .collect(Collectors.toList());
    if (entitiesToRemove.isEmpty()) {
      throw new QuerimoniaException(HttpStatus.NOT_FOUND, "Die gegebene Entität existiert nicht "
          + "in der Beschwerde.", "Ungültige Entity");
    }
    entitiesToRemove.forEach(complaintEntities::remove);
    storeComplaint(complaint);
    return complaintEntities;
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
    ComplaintBuilder builder = new ComplaintBuilder(complaint);
    var suggestion = complaintFactory.createResponse(builder);
    builder.setResponseSuggestion(suggestion);

    storeComplaint(builder.createComplaint());
    return suggestion;
  }

  private synchronized void storeComplaint(Complaint complaint) {
    // save the components

    try {
      configurationManager.storeConfiguration(complaint.getConfiguration());
    } catch (Exception e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Speichern der "
          + "Konfiguration", e, "Konfiguration");
    }
    try {
      complaintRepository.save(complaint);
    } catch (Exception e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Speichern der "
          + "Beschwerde", e, "Beschwerde");
    }
    logger.info("Saved complaint with id {}", complaint.getId());
  }

  private void checkState(Complaint complaint) {
    if (complaint.getState().equals(ComplaintState.CLOSED)) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Beschwerde kann nicht bearbeitet "
          + "werden, da sie bereits geschlossen ist.", "Beschwerde geschlossen");
    }
  }

}
