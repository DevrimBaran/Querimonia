package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaint.Complaint;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintPropertyRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.CompletedResponseComponentRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.rest.manager.ComplaintManager;
import de.fraunhofer.iao.querimonia.rest.restobjects.ComplaintUpdateRequest;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * This controller manages complaint view, import and export.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class ComplaintController {

  private final ComplaintManager complaintManager;

  /**
   * Constructor gets only called by spring. Sets up the complaint factory.
   */
  public ComplaintController(
      FileStorageService fileStorageService,
      ComplaintRepository complaintRepository,
      TemplateRepository templateRepository,
      CompletedResponseComponentRepository
          completedResponseComponentRepository,
      ComplaintPropertyRepository complaintPropertyRepository
  ) {
    complaintManager = new ComplaintManager(fileStorageService, complaintRepository,
        templateRepository, completedResponseComponentRepository, complaintPropertyRepository);
  }

  /**
   * Returns the complaints of the database. With the given parameters the complaints get filtered,
   * sorted and pagination can be used.
   *
   * @param count     used for pagination: The amount of complaints per page. It is the number of
   *                  complaints, that should be returned at most. If this is not given, all
   *                  complaints will be returned (that match the other filters)
   * @param page      for pagination: the current page index (starts at 0). If not given, 0 is
   *                  assumed as page number.
   * @param sortBy    an array of sorting aspects. The order in the array represents the priority
   *                  for the sorting: The complaints get sorted by the first array entry first, and
   *                  then by the second, etc. Valid sort aspects are: <code>upload_date_asc,
   *                  upload_date_desc, subject_asc, subject_desc, sentiment_asc,
   *                  sentiment_desc}</code>
   * @param state     If given, only complaints in that state will be returned.
   * @param dateMin   If given, no complaints that were uploaded before that date will be returned.
   * @param dateMax   If given, no complaints that were uploaded after that date will be returned.
   * @param sentiment If given, only complaints with this sentiment will be returned.
   * @param subject   If given, only complaints with this subject will be returned.
   * @param keywords  If given, only complaints that contain the keywords will returned.
   * @return a response entity with status code 200, containing the filtered and sorted list of
   * complaints as response body.
   * @throws ResponseStatusException with
   *                                 <ul>
   *                                 <li>status code 400: with empty response body, if the
   *                                 parameters are malformed.</li>
   *                                 <li>status code 5XX: some unexpected server error
   *                                 occurred.</li>
   *                                 </ul>
   */
  @GetMapping("/api/complaints")
  public ResponseEntity<List<Complaint>> getComplaints(
      @RequestParam("count") Optional<Integer> count,
      @RequestParam("page") Optional<Integer> page,
      @RequestParam("sort_by") Optional<String[]> sortBy,
      @RequestParam("state") Optional<String> state,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("sentiment") Optional<String[]> sentiment,
      @RequestParam("subject") Optional<String[]> subject,
      @RequestParam("keywords") Optional<String[]> keywords
  ) {
    return new ResponseEntity<>(complaintManager.getComplaints(count, page, sortBy,
        state, dateMin, dateMax, sentiment, subject, keywords), HttpStatus.OK);
  }

  /**
   * This endpoint is used for uploading files with complaint texts. It accepts txt, word and pdf
   * files and extracts the text from them. Then the complaint gets analyzed with a given
   * configuration or the currently active configuration.
   *
   * @param file     the multipart file which gets transferred via http.
   * @param configId the ID of the configuration that should be used to analyze the complaint.
   * @return a response entity with status code 201, the response body is the new complaint object.
   * @throws ResponseStatusException with
   *                                 <ul>
   *                                 <li>status code 400: with empty response body, if the file
   *                                 could not be processed or the text is too long.</li>
   *                                 <li>status code 5XX: Bad configuration, external services can
   *                                 not be accessed or some unexpected server error occurred.</li>
   *                                 </ul>
   */
  @PostMapping(value = "/api/complaints/import", produces = "application/json",
               consumes = "multipart/form-data")
  public ResponseEntity<Complaint> uploadComplaint(
      @RequestParam("file") MultipartFile file,
      @RequestParam("configId") Optional<Integer> configId) {
    Complaint complaint = complaintManager.uploadComplaint(file, configId);
    return new ResponseEntity<>(complaint, HttpStatus.CREATED);
  }

  /**
   * This endpoint is used for importing raw complaint texts. The complaint gets analyzed with a
   * given configuration or the currently active configuration.
   *
   * @param input    the complaint text that should be imported.
   * @param configId the ID of the configuration that should be used to analyze the complaint.
   * @return a response entity with status code 201, the response body is the new complaint object.
   * @throws ResponseStatusException with
   *                                 <ul>
   *                                 <li>status code 400: with empty response body, if the
   *                                 text is too long.</li>
   *                                 <li>status code 5XX: Bad configuration, external services can
   *                                 not be accessed or some unexpected server error occurred.</li>
   *                                 </ul>
   */
  @PostMapping(value = "/api/complaints/import", produces = "application/json",
               consumes = "application/json")
  public ResponseEntity<Complaint> uploadText(@RequestBody TextInput input,
                                              @RequestParam Optional<Integer> configId) {
    return new ResponseEntity<>(complaintManager.uploadText(input, configId), HttpStatus.CREATED);
  }

  /**
   * Returns a specific complaint with the given complaint complaintId.
   *
   * @param complaintId the if of the complaint.
   * @return a response entity with the complaint as response body.
   * @throws ResponseStatusException with
   *                                 <ul>
   *                                 <li>status code 404: no complaint with the given Id
   *                                 exists.</li>
   *                                 <li>status code 5XX: some unexpected server error
   *                                 occurred.</li>
   *                                 </ul>
   */
  @GetMapping("/api/complaints/{complaintId}")
  public ResponseEntity<Complaint> getComplaint(@PathVariable int complaintId) {
    return new ResponseEntity<>(complaintManager.getComplaint(complaintId), HttpStatus.OK);
  }

  /**
   * With this method sentiment, subject and state of a complaint can be updated.
   *
   * @param complaintId   the id of the complaint that should be updated.
   * @param updateRequest the request body with the new values for the attributes.
   * @return a response entity with response code 200 and the complaint that was updated
   * successfully as response body.
   * @throws ResponseStatusException with:
   *                                 <ul>
   *                                 <li>status code 404: no complaint with the given Id
   *                                 exists.</li>
   *                                 <li>status code 5XX: some unexpected server error
   *                                 occurred.</li>
   *                                 </ul>
   */
  @PatchMapping("/api/complaints/{complaintId}")
  public ResponseEntity<Complaint> updateComplaint(
      @PathVariable int complaintId,
      @RequestBody ComplaintUpdateRequest updateRequest) {

    return new ResponseEntity<>(complaintManager.updateComplaint(complaintId, updateRequest),
        HttpStatus.OK);
  }

  /**
   * Deletes the complaint with the given id.
   *
   * @return a response entity with status code 204: the complaint was deleted successfully.
   * @throws ResponseStatusException with:
   *                                 <ul>
   *                                 <li>status code 404: no complaint with the given Id
   *                                 exists.</li>
   *                                 <li>status code 5XX: some unexpected server error
   *                                 occurred.</li>
   *                                 </ul>
   */
  @DeleteMapping("/api/complaints/{complaintId}")
  public ResponseEntity deleteComplaint(@PathVariable int complaintId) {
    complaintManager.deleteComplaint(complaintId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
   * @return a response entity with status code 200 and the refreshed complaint as response body.
   * @throws ResponseStatusException with
   *                                 <ul>
   *                                 <li>status code 404: the complaintId or the configId do not
   *                                 belong to existing objects.</li>
   *                                 <li>status code 400: malformed parameters</li>
   *                                 <li>status code 5XX: Bad configuration, external services can
   *                                 not be accessed or some unexpected server error occurred.</li>
   *                                 </ul>
   */
  @PatchMapping("api/complaint/{complaintId}/refresh")
  public ResponseEntity<Complaint> refreshComplaint(
      @PathVariable int complaintId,
      @RequestParam Optional<Boolean> keepUserInformation,
      @RequestParam Optional<Integer> configId) {

    return new ResponseEntity<>(complaintManager.refreshComplaint(complaintId,
        keepUserInformation, configId), HttpStatus.OK);
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
   * @return a response entity that contains the number of complaints as response body and status
   * code 200.
   * @throws ResponseStatusException with
   *                                 <ul>
   *                                 <li>status code 400: with empty response body, if the
   *                                 parameters are malformed.</li>
   *                                 <li>status code 5XX: some unexpected server error
   *                                 occurred.</li>
   *                                 </ul>
   */
  @GetMapping("api/complaints/count")
  public ResponseEntity<Integer> countComplaints(
      @RequestParam("state") Optional<String> state,
      @RequestParam("date_min") Optional<String> dateMin,
      @RequestParam("date_max") Optional<String> dateMax,
      @RequestParam("sentiment") Optional<String[]> sentiment,
      @RequestParam("subject") Optional<String[]> subject,
      @RequestParam("keywords") Optional<String[]> keywords
  ) {
    return new ResponseEntity<>(complaintManager.countComplaints(state, dateMin, dateMax,
        sentiment, subject, keywords), HttpStatus.OK);
  }

  /**
   * Adds a new named entity to a complaint.
   *
   * @param complaintId the id of the complaint.
   * @param label       the label of the entity.
   * @param start       the start index of the entity. Must not be negative and in the bounds of the
   *                    complaint text.
   * @param end         the end index of the entity. Must be greater than start and in the bounds of
   *                    the complaint text.
   * @param extractor   the name of the extractor, that should have found the entity.
   * @return a response entity with status code 201 and a list of the named entities of the
   * complaint as response body.
   * @throws ResponseStatusException with:
   *                                 <ul>
   *                                 <li>400: the named entity that should be add is invalid or
   *                                 already there.</li>
   *                                 <li>5XX: some unexpected server error occurred.</li>
   *                                 </ul>
   */
  @PostMapping("api/complaints/{complaintId}/entities")
  public ResponseEntity<List<NamedEntity>> addEntity(
      @PathVariable int complaintId,
      @RequestParam String label,
      @RequestParam int start,
      @RequestParam int end,
      @RequestParam String extractor) {
    return new ResponseEntity<>(complaintManager.addEntity(complaintId, label, start, end,
        extractor), HttpStatus.CREATED);
  }

  /**
   * Deletes a named entity of a complaint.
   *
   * @param complaintId the id of the complaint.
   * @param label       the label of the entity.
   * @param start       the start index of the entity.
   * @param end         the end index of the entity.
   * @param extractor   the name of the extractor, that should have found the entity.
   * @return a response entity with status code 200 and a list of the named entities of the
   * complaint as response body.
   * @throws ResponseStatusException with:
   *                                 <ul>
   *                                 <li>404: The named entity does not exist</li>
   *                                 <li>5XX: some unexpected server error occurred.</li>
   *                                 </ul>
   */
  @DeleteMapping("api/complaints/{complaintId}/entities")
  public ResponseEntity<List<NamedEntity>> removeEntity(
      @PathVariable int complaintId,
      @RequestParam String label,
      @RequestParam int start,
      @RequestParam int end,
      @RequestParam String extractor) {
    return new ResponseEntity<>(complaintManager.removeEntity(complaintId, label, start, end,
        extractor), HttpStatus.OK);
  }

  /**
   * Deletes all complaints in the database.
   *
   * @return a response entity with status code 204.
   * @throws ResponseStatusException on some unexpected server error.
   */
  @DeleteMapping("/api/complaints/all")
  public ResponseEntity deleteAllComplaints() {
    complaintManager.deleteAllComplaints();
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
