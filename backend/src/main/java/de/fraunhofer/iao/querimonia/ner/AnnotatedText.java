package de.fraunhofer.iao.querimonia.ner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * This POJO represents a text with annotated named entities and a response to the complaint. An instance of this class
 * can be created using a {@link AnnotatedTextBuilder}.
 */
public class AnnotatedText {

    private String text;
    @Nullable
    private String answer;
    private List<NamedEntity> entities;

    /**
     * Constructs a new instance. This constructor is package private, for instance creation use the
     * {@link AnnotatedTextBuilder}.
     */
    @JsonCreator
    AnnotatedText(@JsonProperty String text, @Nullable @JsonProperty String answer,
                  @JsonProperty List<NamedEntity> entities) {
        this.text = text;
        this.answer = answer;
        this.entities = entities;
    }

    /**
     * @return the original text, where the entities can be found.
     */
    public String getText() {
        return text;
    }

    /**
     * @return the response to the complaint. This may be null.
     */
    @Nullable
    public String getAnswer() {
        return answer;
    }

    /**
     * @return all entities in the texts.
     */
    public List<NamedEntity> getEntities() {
        return entities;
    }


}
