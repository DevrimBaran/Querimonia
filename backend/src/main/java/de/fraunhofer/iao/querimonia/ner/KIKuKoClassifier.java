package de.fraunhofer.iao.querimonia.ner;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.Typ;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * This class sends a text to the KIKuKo API to classify the text with a given classifier
 */
public class KIKuKoClassifier {

    private static final String URL = "https://kikuko.iao.fraunhofer.de/apitext";
    private static final String DOMAIN_TYPE = "Beschwerde3Klassifikator";

    public static void main(String[] args) {
        /*
        Testing to get an Answer from KiKuKo
         */

        KikukoResponse matchesResponse;
            matchesResponse = new KIKuKoClassifier().executeKikukoRequest("ausfal iansic");

        System.out.println(matchesResponse);
        //List<TempPipeline> pipe = matchesResponse.getPipelines().getTempPipeline();
        //double erg = pipe.get(0).getTyp().getFahrtNichtErfolgt();

        //System.out.println(erg);

    }

    public Typ getClassification(String input){
        KikukoResponse response = executeKikukoRequest(input);

        return response.getPipelines()
                .getTempPipeline()
                .get(0)
                .getTyp();
    }

    private KikukoResponse executeKikukoRequest(String input) {

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", input);
        map.add("type", "tool");
        map.add("domainType", DOMAIN_TYPE);

        HttpEntity<MultiValueMap> request = new HttpEntity<>(map, header);

        // add authentication token
        RestTemplate template = new RestTemplateBuilder()
                .basicAuthentication("admin", "KIKuKoPass2018")
                .build();

        // get response
        String returnValue = template.exchange(URL, HttpMethod.POST,
                request, String.class).getBody();
        // map string to json
        ObjectMapper mapper = new ObjectMapper();
        KikukoResponse[] responses;
        // exception for illegal answers
        HttpServerErrorException kikukoException =
                new HttpServerErrorException(HttpStatus.CONFLICT, "No response from KiKUKO available");
        try {
            responses = mapper.readValue(returnValue, KikukoResponse[].class);
            // check for available response
            if (responses == null || responses.length == 0) {
                throw kikukoException;
            }
        } catch (IOException e) {
            // illegal json format
            throw kikukoException;
        }

        return responses[0];
    }

}
