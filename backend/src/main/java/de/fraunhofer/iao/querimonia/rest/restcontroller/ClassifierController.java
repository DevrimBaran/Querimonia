package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.db.Complaint;
import de.fraunhofer.iao.querimonia.ner.KIKuKoClassifier;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.Typ;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ClassifierController {

    @PostMapping("/api/classify/kikuko")
    public Typ getBeschwerde3KlassifikatorClass(@RequestParam("id") int id){
        Complaint complaint = new ComplaintController().getComplaint(id);
        if(complaint == null){
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "No Complaint with this id");
        }
        String text = complaint.getText();

        return new KIKuKoClassifier().getClassification(text);
    }
}
