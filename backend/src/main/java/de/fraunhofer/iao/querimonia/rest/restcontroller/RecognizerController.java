package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.ner.AnnotatedText;
import de.fraunhofer.iao.querimonia.ner.SimpleTestRecognizer;
import de.fraunhofer.iao.querimonia.ner.TextominadoTestRecognizer;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a rest controller for the integration tests. For example requests, see doc/rest-examples.http
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RecognizerController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * This method annotates a given text input with "line" and "date" entities. It can be called with a POST-Request.
     *
     * @param input the text which will be annotated.
     * @return the text with the annotated entities.
     */
    @PostMapping(value = "/api/test/recognizer")
    public List<AnnotatedText> annotateText(@RequestBody TextInput input) {
        List<AnnotatedText> results = new ArrayList<>();
        results.add(new SimpleTestRecognizer().annotateText(input.getText()));
        return results;
    }

    /**
     * This methods finds the money value in the given text and creates a simple answer. The extraction is done using
     * textominado.
     *
     * @param //input the complaint texts where answers should be generated.
     * @return // TODO documentation for return
     *
     @PostMapping(value = "/api/test/textominado")
     public List<AnnotatedText> getAnswer(@RequestBody TextInput input) {
     List<AnnotatedText> results = new ArrayList<>();
     results.add(new TextominadoTestRecognizer().annotateText(input.getText()));
     return results;
     }
     */
    @PostMapping(value = "/api/test/textominado-batch")
    public List<AnnotatedText> getAnswersWithTextominado(@RequestParam("file") MultipartFile file) throws IOException {
        Logger logger = LoggerFactory.getLogger("Test");
        String fileName = fileStorageService.storeFile(file);

        FileReader reader = new FileReader("src/main/resources/uploads/" + fileName);
        BufferedReader bufferedReader = new BufferedReader(reader);

        List<AnnotatedText> results = new ArrayList<>();
        SimpleTestRecognizer recognizer = new SimpleTestRecognizer();
        bufferedReader.lines()
                .map(recognizer::annotateText)
                .peek(annotatedText -> logger.info(annotatedText.getAnswer()))
                .forEach(results::add);

        File uploadFile = new File("src/main/resources/uploads/" + fileName);
        if (uploadFile.delete()) {
            System.out.println("File deleted.");
        } else {
            System.out.println("File could not be deleted.");
        }

        return results;
    }
}
