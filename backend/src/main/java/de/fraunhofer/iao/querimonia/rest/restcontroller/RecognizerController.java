package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.ner.AnnotatedText;
import de.fraunhofer.iao.querimonia.ner.SimpleTestRecognizer;
import de.fraunhofer.iao.querimonia.ner.TextominadoTestRecognizer;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is a rest controller for the integration tests. For example requests, see doc/rest-examples.http
 */
@RestController
public class RecognizerController {

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
    public List<AnnotatedText> getAnswersWithTextominado(@RequestBody MultipartFile file) throws IOException {

        FileReader reader = new FileReader(Objects.requireNonNull(file.getOriginalFilename()));
        BufferedReader bufferedReader = new BufferedReader(reader);

        List<AnnotatedText> results = new ArrayList<>();
        TextominadoTestRecognizer recognizer = new TextominadoTestRecognizer();
        bufferedReader.lines()
                .map(recognizer::annotateText)
                .forEach(results::add);

        return results;
    }
}
