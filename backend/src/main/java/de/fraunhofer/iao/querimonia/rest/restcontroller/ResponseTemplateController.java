package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.ResponseTemplate;
import de.fraunhofer.iao.querimonia.db.ResponseTemplateManager;
import de.fraunhofer.iao.querimonia.db.repositories.TemplateRepository;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * REST Controller for creating, viewing and deleting Response Templates.
 * Serves as the REST API for the ResponseTemplateManager.
 * @author Simon Weiler
 */
@RestController
public class ResponseTemplateController {

    private ResponseTemplateManager responseTemplateManager;

    @Autowired
    TemplateRepository templateRepository;

    private void initialize() {
        responseTemplateManager = ResponseTemplateManager.instantiate();
    }

    /**
     * Add a new template to the repository
     * @param templateText The actual text of the template
     * @param subject The subject/category of the template
     * @param responsePart the The role/position of this template in a response
     * @return the created template
     */
    @PostMapping("api/templates")
    public ResponseTemplate addTemplate(@RequestParam TextInput templateText, @RequestParam TextInput subject, @RequestParam TextInput responsePart) {
        initialize();
        return responseTemplateManager.addTemplate(templateRepository, templateText.getText(), subject.getText(), responsePart.getText());
    }

    /**
     * Find a template with the given subject
     * @param subject the subject to look for
     * @return a response template with the given subject
     */
    @GetMapping("api/templates/subjects/{subject}")
    public ResponseTemplate getTemplateBySubject(@PathVariable String subject) {
        initialize();
        return responseTemplateManager.getTemplateBySubject(templateRepository, subject);
    }

    /**
     * Find the template with the given ID
     * @param id the ID to look for
     * @return the response template with the given ID
     */
    @GetMapping("api/templates/{id}")
    public ResponseTemplate getTemplateByID(@PathVariable int id) {
        initialize();
        return responseTemplateManager.getTemplateByID(templateRepository, id);
    }

    /**
     * Delete the template with the given ID
     * @param id the ID of the template to delete
     */
    @DeleteMapping("api/templates/{id}")
    public void deleteTemplate(@PathVariable int id) {
        initialize();
        responseTemplateManager.deleteTemplateByID(templateRepository, id);
    }
}
