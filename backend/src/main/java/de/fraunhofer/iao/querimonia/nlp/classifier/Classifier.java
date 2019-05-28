package de.fraunhofer.iao.querimonia.nlp.classifier;

import java.util.LinkedHashMap;

/**
 * This interface is used to classify a text in a category.
 */
public interface Classifier {

    /**
     * Classifies the text in a category.
     *
     * @param text the text that should be classified.
     * @return a linked hash map which maps a category to the probability that the text belongs to that
     * category, sorted by the probability.
     */
    LinkedHashMap<String, Double> classifyText(String text);
}
