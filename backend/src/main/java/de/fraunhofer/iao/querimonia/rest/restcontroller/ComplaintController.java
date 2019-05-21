package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.complaints.ComplaintFactory;
import de.fraunhofer.iao.querimonia.complaints.ComplaintRepository;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * This controller manages complaint view, import and export.
 */
@RestController("/api")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @PostMapping
    public void uploadComplaint(MultipartFile file) {

    }

    @PostMapping("/import/text")
    public void uploadText(@RequestBody TextInput input) {
        complaintRepository.save(ComplaintFactory.createComplaint(input.getText()));
    }
}
