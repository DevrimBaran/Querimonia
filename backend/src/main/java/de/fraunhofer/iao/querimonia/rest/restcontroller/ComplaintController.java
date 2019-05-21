package de.fraunhofer.iao.querimonia.rest.restcontroller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * This controller manages complaint view, import and export.
 */
@RestController
public class ComplaintController {

    @PostMapping
    public void uploadComplaint(MultipartFile file) {

    }
}
