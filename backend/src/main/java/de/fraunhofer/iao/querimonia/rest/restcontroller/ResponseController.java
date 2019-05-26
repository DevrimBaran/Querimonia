package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.*;
import de.fraunhofer.iao.querimonia.db.repositories.ComplaintRepository;
import de.fraunhofer.iao.querimonia.db.repositories.ResponseRepository;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for generating, receiving and deleting responses
 * @author Simon Weiler
 */
@RestController
public class ResponseController {

    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    ComplaintRepository complaintRepository;

    /**
     * Generate a new response for the complaint with the given id
     * @param ID the id of the complaint to respond to
     * @return the new response
     */
    @PostMapping("api/responses/new/{ID}")
    public ResponseSuggestion generateResponse(@PathVariable int ID) {
        Complaint complaint = complaintRepository.findById(ID).orElse(null);

        // Only respond to existing complaints
        if (complaint == null) {
            return null;
        }

        ResponseTemplateManager responseTemplateManager = ResponseTemplateManager.instantiate();
        ResponseTemplate responseTemplate = responseTemplateManager.getTemplateBySubject(templateRepository, complaint.getSubject());
        ResponseSuggestion responseSuggestion = ResponseSuggestionFactory.createResponseSuggestionWithDate(complaint, responseTemplate);
        responseRepository.save(responseSuggestion);
        return responseSuggestion;
    }

    /**
     * Find the response with the given id
     * @param id an id of a response
     * @return the response with the given id
     */
    @GetMapping("api/responses/{id}")
    public ResponseSuggestion getResponse(@PathVariable int id) {
        return responseRepository.findById(id).orElse(null);
    }

    /**
     * Delete the response with the given ID
     * @param id the ID of the response to delete
     */
    @DeleteMapping("api/responses/{id}")
    public void deleteResponse(@PathVariable int id) {
        responseRepository.deleteById(id);
    }
}
