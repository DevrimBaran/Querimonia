package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.complaint.ComplaintState;
import de.fraunhofer.iao.querimonia.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.rest.restobjects.ComplaintUpdateRequest;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * This controller is used to manage the complaints of Querimonia. Complaints can be returned,
 * added, modified and deleted. For additional information see the OpenAPI.yaml file in the
 * documentation.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ComplaintController {

  private final ComplaintManager complaintManager;

  /**
   * Constructor gets only called by spring. Sets up the complaint manager.
   */
  public ComplaintController(ComplaintManager complaintManager) {
    this.complaintManager = complaintManager;
  }

  /**
   * Returns the complaints of the database. With the given parameters the complaints get filtered,
   * sorted and pagination can be used.
   *
   * @param count    used for pagination: The amount of complaints per page. It is the number of
   *                 complaints, that should be returned at most. If this is not given, all
   *                 complaints will be returned (that match the other filters)
   * @param page     for pagination: the current page index (starts at 0). If not given, 0 is
   *                 assumed as page number.
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
   *
   * @return a response entity with the following contents:
   *     <ul>
   *     <li>status code 200 and a list of the sorted, filtered complaints matching the pagination
   *     setting as response body on success.</li>
   *     <li>status code 400 and a the exception as response body when the sorting parameters are
   *     invalid</li>
   *     <li>status code 500 and the exception as response body on an unexpected server error.</li>
   *     </ul>
   */
  @GetMapping("/api/complaints")
  public ResponseEntity<?> getComplaints(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("state") Optional<String[]> state,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("emotion") Optional<String[]> emotion,
      @RequestParam("subject") Optional<String[]> subject,
      @RequestParam("keywords") Optional<String[]> keywords
  ) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.getComplaints(count, page, sortBy,
        state, dateMin, dateMax, emotion, subject, keywords));
  }

  /**
   * Returns the complaints of the database in xml format. With the given parameters the complaints
   * get filtered, sorted.
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
   *
   * @return a response entity with the following contents:
   *     <ul>
   *     <li>status code 200 and a list of the sorted, filtered complaints matching the pagination
   *     setting as response body on success.</li>
   *     <li>status code 400 and a the exception as response body when the sorting parameters are
   *     invalid</li>
   *     <li>status code 500 and the exception as response body on an unexpected server error.</li>
   *     </ul>
   */
  @GetMapping("/api/complaints/xml")
  public ResponseEntity<?> getXmls(
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("state") Optional<String[]> state,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("emotion") Optional<String[]> emotion,
      @RequestParam("subject") Optional<String[]> subject,
      @RequestParam("keywords") Optional<String[]> keywords
  ) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.getXmls(sortBy,
        state, dateMin, dateMax, emotion, subject, keywords));
  }

  /**
   * This endpoint is used for uploading files with complaint texts. It accepts txt, word and pdf
   * files and extracts the text from them. Then the complaint gets analyzed with a given
   * configuration or the currently active configuration.
   *
   * @param file     the multipart file which gets transferred via http.
   * @param configId the ID of the configuration that should be used to analyze the complaint. If
   *                 not given, the currently active configuration is used.
   *
   * @return a response entity with the following contents:
   *     <ul>
   *     <li>status code 201 and the new created complaint as response body on success.</li>
   *     <li>status code 404 with the exception as body when the config with the given id does
   *     not exist.</li>
   *     <li>status code 400 with the exception as body when the text could not be extracted
   *     from the file.</li>
   *     <li>status code 500 and the exception as response body on an unexpected server error.</li>
   *     </ul>
   */
  @PostMapping(value = "/api/complaints/import", produces = "application/json",
               consumes = "multipart/form-data")
  public ResponseEntity<?> uploadComplaint(
      @RequestParam("file") MultipartFile file,
      @RequestParam("configId") Optional<Long> configId,
      @RequestParam("respond_to") Optional<String> respondTo) {
    return ControllerUtility.tryAndCatch(
        () -> complaintManager.uploadComplaint(file, configId, respondTo), HttpStatus.CREATED);
  }

  /**
   * This endpoint is used for importing raw complaint texts. The complaint gets analyzed with a
   * given configuration or the currently active configuration.
   *
   * @param input    the complaint text that should be imported.
   * @param configId the ID of the configuration that should be used to analyze the complaint. If
   *                 not given, the currently active configuration is used.
   *
   * @return a response entity with the following contents:
   *     <ul>
   *     <li>status code 201 and the new created an analyzed complaint as body on success</li>
   *     <li>status code 400 and the querimonia exception body when the text is too long</li>
   *     <li>status code 404 and the querimonia exception body when no config with the given id
   *     exists</li>
   *     <li>status code 500 with the querimonia exception body on an unexpected server error</li>
   *     </ul>
   */
  @PostMapping(value = "/api/complaints/import", produces = "application/json",
               consumes = "application/json")
  public ResponseEntity<?> uploadText(@RequestBody TextInput input,
                                      @RequestParam Optional<Long> configId,
                                      @RequestParam("respond_to") Optional<String> respondTo) {
    return ControllerUtility.tryAndCatch(
        () -> complaintManager.uploadText(input, configId, respondTo), HttpStatus.CREATED);
  }

  // TODO @Samuel Javadoc
  @PostMapping(value = "/api/complaints/import", produces = "application/json",
               consumes = "application/xml")
  public ResponseEntity<?> uploadXml(@RequestBody Complaint xmlInput) {
    return new ResponseEntity<>(xmlInput, HttpStatus.OK);
    // TODO save in database?
  }

  /**
   * Returns a specific complaint with the given id.
   *
   * @param complaintId the id of the complaint.
   *
   * @return a response entity with the following contents:
   *     <ul>
   *     <li>status code 200 and the complaint with the given id on success</li>
   *     <li>status code 404 and the querimonia exception body when no complaint with that id
   *     exists.</li>
   *     <li>status code 500 and the querimonia exception body on an unexpected server error</li>
   *     </ul>
   */
  @GetMapping(value = "/api/complaints/{complaintId}", produces = "application/json")
  public ResponseEntity<?> getComplaint(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.getComplaint(complaintId));
  }

  /**
   * Returns the text of a complaint.
   *
   * @param complaintId the id of the complaint.
   *
   * @return a response entity with the following contents:
   *     <ul>
   *       <li>status code 200 with the complaint text on success</li>
   *       <li>status code 404 when the complaint with the given id does not exist</li>
   *       <li>status code 500 on an unexpected server error.</li>
   *     </ul>
   */
  @GetMapping("/api/complaints/{complaintId}/text")
  public ResponseEntity<?> getText(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.getText(complaintId));
  }

  @GetMapping(value = "/api/complaints/{complaintId}/xml", produces = "application/xml")
  public ResponseEntity<?> getXml(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() ->
        complaintManager.getXml(complaintId)
    );
  }

  /**
   * With this method sentiment, subject and state of a complaint can be updated.
   *
   * @param complaintId   the id of the complaint that should be updated.
   * @param updateRequest the request body with the new values for the attributes.
   *
   * @return a response entity with the following contents:
   *     <ul>
   *     <li>status code 200 and the updated complaint on success</li>
   *     <li>status code 400 and the querimonia exception body when the complaint is already closed
   *     </li>
   *     <li>status code 404 and the querimonia exception body when no complaint with the given id
   *     exists</li>
   *     <li>status code 500 and the querimonia exception body on an unexpected server error</li>
   *     </ul>
   */
  @PatchMapping("/api/complaints/{complaintId}")
  public ResponseEntity<?> updateComplaint(
      @PathVariable long complaintId,
      @RequestBody ComplaintUpdateRequest updateRequest) {

    return ControllerUtility.tryAndCatch(() ->
        complaintManager.updateComplaint(complaintId, updateRequest));
  }

  /**
   * Deletes the complaint with the given id.
   *
   * @param complaintId the id of the complaint that should be deleted.
   *
   * @return a response entity with the following contents:
   *     <ul>
   *      <li>status code 204 on success</li>
   *      <li>status code 404 and the querimonia exception body when no complaint with the given id
   *      exists</li>
   *      <li>status code 500 with the querimonia exception body on an unexpected server error</li>
   *     </ul>
   */
  @DeleteMapping("/api/complaints/{complaintId}")
  public ResponseEntity<?> deleteComplaint(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.deleteComplaint(complaintId));
  }

  /**
   * Reruns the analyze and response generation process and overwrites the current information of
   * the complaint.
   *
   * @param complaintId         the id of the complaint with should be refreshed.
   * @param keepUserInformation if this is true, no information with the flag
   *                            <code>isSetByUser</code> should be overwritten.
   * @param configId            the id of the configuration that should be used to analyze the
   *                            complaint. If not given, the currently active configuration is
   *                            used.
   *
   * @return a response entity with
   *     <ul>
   *     <li>status code 200 with the refreshed complaint on success.</li>
   *     <li>status code 404: the ids do not belong to existing objects.</li>
   *     <li>status code 400: malformed parameters</li>
   *     <li>status code 500: Bad configuration, external services can not be accessed or some
   *     unexpected server error occurred.</li>
   *     </ul>
   */
  @PatchMapping("api/complaints/{complaintId}/refresh")
  public ResponseEntity<?> refreshComplaint(
      @PathVariable long complaintId,
      @RequestParam Optional<Boolean> keepUserInformation,
      @RequestParam Optional<Long> configId) {

    return ControllerUtility.tryAndCatch(() ->
        complaintManager.refreshComplaint(complaintId, keepUserInformation, configId));
  }

  /**
   * Closes a complaint. The state of the complaint will be set to {@link ComplaintState#CLOSED},
   * so the complaint can not be edited anymore. Also, all actions, that were assigned to the
   * complaint get executed.
   *
   * @param complaintId the unique id of the complaint, that will be closed.
   *
   * @return a response entity with:
   *     <ul>
   *     <li>status code 200 and the complaint as response body on success.</li>
   *     <li>status code 400 with the error es response body when the complaint is already
   *     closed.</li>
   *     <li>status code 404 with the error as response body when the complaint with the given id
   *     does not exist.
   *     </li>
   *     <li>status code 500 and the error as response body on some unexpected server error.</li>
   *     </ul>
   */
  @PatchMapping("api/complaints/{complaintId}/close")
  public ResponseEntity<?> closeComplaint(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() ->
        complaintManager.closeComplaint(complaintId));
  }


  /**
   * This function is used to count the number of complaints, that match a certain filter.
   *
   * @param state     If given, only complaints in that state will be returned.
   * @param dateMin   If given, no complaints that were uploaded before that date will be returned.
   * @param dateMax   If given, no complaints that were uploaded after that date will be returned.
   * @param sentiment If given, only complaints with this sentiment will be returned.
   * @param subject   If given, only complaints with this subject will be returned.
   * @param keywords  If given, only complaints that contain the keywords will returned.
   *
   * @return a response entity with
   *     <ul>
   *     <li>status code 200 with the count of the complaints that match the given filter
   *     parameters on success.</li>
   *     <li>status code 400: with empty response body, if the parameters are malformed.</li>
   *     <li>status code 500: some unexpected server error occurred.</li>
   *     </ul>
   */
  @GetMapping("api/complaints/count")
  public ResponseEntity<?> countComplaints(
      @RequestParam("state") Optional<String[]> state,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("sentiment") Optional<String[]> sentiment,
      @RequestParam("subject") Optional<String[]> subject,
      @RequestParam("keywords") Optional<String[]> keywords
  ) {
    return ControllerUtility.tryAndCatch(() ->
        complaintManager.countComplaints(state, dateMin, dateMax, sentiment, subject, keywords));
  }

  /**
   * This endpoint is used to receive the log of a complaint.
   *
   * @param complaintId the id of the complaint which log should be returned.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 and the log as response body on success</li>
   *       <li>status code 404 when the complaint with the given id does not exist.</li>
   *       <li>status code 500 on an unexpected server error</li>
   *     </ul>
   */
  @GetMapping("api/complaints/{complaintId}/log")
  public ResponseEntity<?> getLog(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.getLog(complaintId));
  }

  /**
   * Returns the entities of a complaint.
   *
   * @param complaintId the id of the complaint.
   *
   * @return a response entity with:
   *     <ul>
   *     <li>status code 200 with a list of all entities of the complaint as response body on
   *     success</li>
   *     <li>status code 404 with the error as response body when no complaint with the given id
   *     exists</li>
   *     <li>status code 500 with the error as response body on an unexpected server error.</li>
   *     </ul>
   */
  @GetMapping("api/complaints/{complaintId}/entities")
  public ResponseEntity<?> getEntities(@PathVariable long complaintId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.getEntities(complaintId));
  }

  /**
   * Adds a new named entity to a complaint.
   *
   * @param complaintId the id of the complaint.
   * @param namedEntity the entity that should be added to the complaint.
   *
   * @return a response entity with:
   *     <ul>
   *     <li>status code 201 and the entities of the complaint on success</li>
   *     <li>status code 404: the complaint with the given id does not exist.</li>
   *     <li>status code 400: the named entity that should be add is invalid or already there.</li>
   *     <li>status code 500: some unexpected server error occurred.</li>
   *     </ul>
   */
  @PostMapping("api/complaints/{complaintId}/entities")
  public ResponseEntity<?> addEntity(
      @PathVariable long complaintId,
      @RequestBody NamedEntity namedEntity) {
    return ControllerUtility.tryAndCatch(() ->
        complaintManager.addEntity(complaintId, namedEntity), HttpStatus.CREATED);
  }

  /**
   * Replaces an existing named entity of a complaint with a new entity.
   *
   * @param complaintId the id of the complaint.
   * @param entityId    the id of the entity.
   * @param namedEntity the new entity.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200: The entity was updated. The body is the new list of entities of the
   *       complaint.</li>
   *       <li>status code 400: The complaint can not be edited.</li>
   *       <li>status code 404: Either the complaint or the entity with the id does not exist.</li>
   *       <li>status code 500: Some unexpected error occurred.</li>
   *     </ul>
   */
  @PutMapping("api/complaints/{complaintId}/entities/{entityId}")
  public ResponseEntity<?> updateEntity(@PathVariable long complaintId,
                                        @PathVariable long entityId,
                                        @RequestBody NamedEntity namedEntity) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.updateEntity(complaintId,
        entityId, namedEntity));
  }

  /**
   * Deletes a named entity of a complaint.
   *
   * @param complaintId the id of the complaint.
   * @param entityId    the id of the entity.
   *
   * @return a response entity with:
   *     <ul>
   *     <li>status code 200: The entity was deleted, a list of all remaining entities of the
   *     complaint gets returned.</li>
   *     <li>status code 404: The named entity or the complaint does not exist.</li>
   *     <li>status code 500: some unexpected server error occurred.</li>
   *     </ul>
   */
  @DeleteMapping("api/complaints/{complaintId}/entities/{entityId}")
  public ResponseEntity<?> removeEntity(
      @PathVariable long complaintId,
      @PathVariable long entityId) {
    return ControllerUtility.tryAndCatch(() ->
        complaintManager.removeEntity(complaintId, entityId));
  }

  /**
   * Adds example complaint texts to the database, that get analyzed.
   *
   * @param count    the amount of text that should be added. The default value is 150.
   * @param configId the id of the config that should be used for the analysis.
   *
   * @return a response entity with:
   *     <ul>
   *       <li>status code 200 on success and the added complaints as body</li>
   *       <li>status code 500 on an unexpected server error</li>
   *     </ul>
   */
  @PostMapping("api/complaints/default")
  public ResponseEntity<?> addDefaultComplaints(Optional<Integer> count, Optional<Long> configId) {
    return ControllerUtility.tryAndCatch(() -> complaintManager.addExampleComplaints(count,
        configId));
  }

  /**
   * Deletes all complaints in the database.
   *
   * @return a response entity with
   *     <ul>
   *     <li>status code 204 on success</li>
   *     <li>status code 500 on a unexpected server error with the querimonia exception as
   *     response body.</li>
   *     </ul>
   */
  @DeleteMapping("/api/complaints/all")
  public ResponseEntity<?> deleteAllComplaints() {
    return ControllerUtility.tryAndCatch(complaintManager::deleteAllComplaints);
  }

}
