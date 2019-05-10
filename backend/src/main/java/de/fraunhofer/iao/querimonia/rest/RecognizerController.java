package de.fraunhofer.iao.querimonia.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iao.querimonia.ner.AnnotatedText;
import de.fraunhofer.iao.querimonia.ner.SimpleTestRecognizer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public AnnotatedText annotateText(@RequestBody TextInput input) {
        return new SimpleTestRecognizer().annotateText(input.getText());
    }

    /**
     * This methods finds the money values in the given texts and creates a simple answer. The extraction is done using
     * textominado.
     *
     * @param input all the complaint texts, where answers should be generated.
     * @return // TODO documentation for return
     */
    @PostMapping(value = "/api/test/textominado")
    public String getAnswers(@RequestBody MultiTextInput input) {
        // TODO implement the connection to textominado
        return "NOT IMPLEMENTED YET";
    }

    /**
     * Simple wrapper class for Strings. This is necessary to allow the JSON syntax in the POST-requests.
     */
    public static class TextInput {

        private String text;

        @JsonCreator
        public TextInput(@JsonProperty String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * Simple wrapper class for a list of strings. This is necessary to allow the JSON syntax in the POST-requests.
     */
    public static class MultiTextInput {

        private List<TextInput> texts;

        @JsonCreator
        public MultiTextInput(@JsonProperty("texts") List<TextInput> texts) {
            this.texts = texts;
        }

        public List<TextInput> getTexts() {
            return texts;
        }
    }
}
