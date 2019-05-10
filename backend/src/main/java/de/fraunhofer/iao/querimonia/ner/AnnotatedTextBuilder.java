package de.fraunhofer.iao.querimonia.ner;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a simple builder for the {@link AnnotatedText} class.
 */
public class AnnotatedTextBuilder {
    private String text;
    private String answer;
    private List<NamedEntity> entities = new ArrayList<>();

    public AnnotatedTextBuilder(String text) {
        this.text = text;
    }

    public AnnotatedTextBuilder setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public AnnotatedTextBuilder setEntities(List<NamedEntity> entities) {
        this.entities = entities;
        return this;
    }

    public AnnotatedTextBuilder addEntity(String label, int start, int end) {
        this.entities.add(new NamedEntity(label, start, end));
        return this;
    }

    public AnnotatedText createAnnotatedText() {
        return new AnnotatedText(text, answer, entities);
    }
}