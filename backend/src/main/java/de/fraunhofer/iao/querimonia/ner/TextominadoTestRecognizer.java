package de.fraunhofer.iao.querimonia.ner;

import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.Match;
import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.Response;
import de.fraunhofer.iao.querimonia.rest.restobjects.textominado.TextominadoText;
import io.micrometer.core.ipc.http.HttpSender;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This recognizer is used to find money values in the complaint and generate a simple answer message.
 * It uses textominado as external extractor.
 */
public class TextominadoTestRecognizer {

    private static final String URL = "https://textominado.iao.fraunhofer.de/backend/entity-recognition/money-amount";

    public AnnotatedText annotateText(String input) {
        TextominadoText text = new TextominadoText(input, "1", "de");

        Response matchesResponse;

        HttpEntity<TextominadoText> request = new HttpEntity<>(text);
        RestTemplate template = new RestTemplateBuilder()
                .basicAuthentication("textominado", "TeamPass2018!")
                .build();

        // get response
        ResponseEntity<Response> responseEntity = template.exchange(URL, HttpMethod.POST,
                request, Response.class);

        matchesResponse = responseEntity.getBody();
        List<Match> matches = null;
        if (matchesResponse != null) {
            matches = Objects.requireNonNull(matchesResponse.getBody()).payload;
        }

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
