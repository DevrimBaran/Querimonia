package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.ner.AnnotatedText;
import de.fraunhofer.iao.querimonia.ner.SimpleTestRecognizer;
import de.fraunhofer.iao.querimonia.ner.TextominadoTestRecognizer;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import de.fraunhofer.iao.querimonia.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
     * @param input the complaint texts where answers should be generated.
     * @return // TODO documentation for return
     */
    @PostMapping(value = "/api/test/textominado")
    public List<AnnotatedText> getAnswer(@RequestBody TextInput input) {
        List<AnnotatedText> results = new ArrayList<>();
        results.add(new TextominadoTestRecognizer().annotateText(input.getText()));
        return results;
    }

    @PostMapping(value = "/api/test/textominado-batch")
    public List<AnnotatedText> getAnswersWithTextominado(@RequestParam("file") MultipartFile file) {
        Logger logger = LoggerFactory.getLogger(RecognizerController.class);
        String fileName = fileStorageService.storeFile(file);
        String fullFilePath = "src/main/resources/uploads/" + fileName;

        List<AnnotatedText> results;
        // read out the file and recognize text
        try(FileReader reader = new FileReader(fullFilePath)) {
            BufferedReader bufferedReader = new BufferedReader(reader);

            results = new ArrayList<>();
            SimpleTestRecognizer recognizer = new SimpleTestRecognizer();
            bufferedReader.lines()
                    .map(recognizer::annotateText)
                    .forEach(results::add);

            File uploadFile = new File(fullFilePath);
            if (uploadFile.delete()) {
                logger.debug("Temporary file deleted");
            } else {
                logger.warn("Temporary file " + fileName + " could not be deleted");
            }
        } catch (IOException e) {
            logger.error("Fehler beim Datei-Upload");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Lesen der Datei.");
        }

        return results;
    }
}
