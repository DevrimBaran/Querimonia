package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Simple wrapper class for a list of strings. This is necessary to allow the JSON syntax in the POST-requests.
 */
public class MultiTextInput {

    private List<TextInput> texts;

    @JsonCreator
    public MultiTextInput(@JsonProperty("texts") List<TextInput> texts) {
        this.texts = texts;
    }

    public List<TextInput> getTexts() {
        return texts;
    }
}
