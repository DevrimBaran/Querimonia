package de.fraunhofer.iao.querimonia.rest;

import de.fraunhofer.iao.querimonia.ner.AnnotatedText;
import de.fraunhofer.iao.querimonia.ner.SimpleTestRecognizer;
import de.fraunhofer.iao.querimonia.ner.TextominadoTestRecognizer;
import de.fraunhofer.iao.querimonia.rest.restobjects.MultiTextInput;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public List<AnnotatedText> getAnswersWithTextominado() {
        return null;
    }
}
