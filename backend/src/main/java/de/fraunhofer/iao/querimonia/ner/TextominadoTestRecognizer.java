package de.fraunhofer.iao.querimonia.ner;

import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.Match;
import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.Response;
import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.TextPosition;
import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.TextominadoText;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This recognizer is used to find money values in the complaint and generate a simple answer message.
 * It uses textominado as external extractor.
 */
public class TextominadoTestRecognizer {

    private static final String URL = "https://textominado.iao.fraunhofer.de/backend/entity-recognition/money-amount";

    /**
     * Annotates the text with money tags and generates a simple response.
     */
    public AnnotatedText annotateText(String input) {
        ResponseEntity<Response> responseEntity = executeTextominadoRequest(input);

        return generateAnnotatedText(input, responseEntity);
    }

    private AnnotatedText generateAnnotatedText(String input, ResponseEntity<Response> responseEntity) {
        AnnotatedTextBuilder textBuilder = new AnnotatedTextBuilder(input);

        Response matchesResponse = responseEntity.getBody();
        List<Match> matches = null;
        if (matchesResponse != null) {
            matches = Objects.requireNonNull(matchesResponse.getBody()).payload;
        }

        String message = "Gerne erstatten wir Ihnen den Betrag.";
        if (matches != null) {
            // find all matches of type money
            List<Match> moneyMatches = matches.stream()
                    .filter(match -> match.getTag().getType().equalsIgnoreCase("Geldbetrag"))
                    .collect(Collectors.toList());
            // add matches to the annotated text
            moneyMatches.forEach(match -> {
                List<TextPosition> positions = match.getPositions();
                positions.forEach(textPosition -> textBuilder.addEntity("money",
                        textPosition.getStartPos(), textPosition.getEndPos()));
            });

            // generate answer with one money value
            Optional<String> moneyValue = moneyMatches.stream()
                    .map(Match::getToken)
                    .findAny();
            if (moneyValue.isPresent()) {
                message = "Gerne erstatten wir Ihnen den Betrag von " + moneyValue.get();
            }
        }

        return textBuilder
                .setAnswer(message)
                .createAnnotatedText();
    }

    private ResponseEntity<Response> executeTextominadoRequest(String input) {
        TextominadoText text = new TextominadoText(input, getTextID(input), "de");

        HttpEntity<TextominadoText> request = new HttpEntity<>(text);
        // add authentication token
        RestTemplate template = new RestTemplateBuilder()
                .basicAuthentication("textominado", "TeamPass2018!")
                .build();

        // get response
        return template.exchange(URL, HttpMethod.POST,
                request, Response.class);
    }

    private String getTextID(String text) {
        long milis = System.currentTimeMillis();
        return "" + text.hashCode() + milis;
    }
}
