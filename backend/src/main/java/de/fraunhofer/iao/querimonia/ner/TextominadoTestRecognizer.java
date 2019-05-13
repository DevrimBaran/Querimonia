package de.fraunhofer.iao.querimonia.ner;

import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.Match;
import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.TextominadoText;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * This recognizer is used to find money values in the complaint and generate a simple answer message.
 * It uses textominado as external extractor.
 */
public class TextominadoTestRecognizer {

    public AnnotatedText annotateText(String input) {
        TextominadoText text = new TextominadoText(input, "1", "de");

        RestTemplate template = new RestTemplate();
        @SuppressWarnings("unchecked") List<Match> matches = (List<Match>) template.postForObject(
                "https://textominado.iao.fraunhofer.de/entity-recognition/money-amount", text, List.class);

        String message = "Gerne erstatten wir Ihnen den Betrag.";
        if (matches != null) {
            Optional<String> moneyValue = matches.stream()
                    .filter(match -> match.getTag().getType().equalsIgnoreCase("Geldbetrag"))
                    .map(Match::getToken)
                    .findAny();
            if (moneyValue.isPresent()) {
                message = "Gerne erstatten wir Ihnen den Betrag von " + moneyValue.get();
            }
        }

        return new AnnotatedTextBuilder(input)
                .setAnswer(message)
                .createAnnotatedText();

    }
}
