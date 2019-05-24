package de.fraunhofer.iao.querimonia.ner;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * This class sends a text to the KIKuKo API to classify the text with a given classifier
 */
public class KIKuKoClassifier {

    private static final String URL = "https://kikuko.iao.fraunhofer.de/apitext";
    private static final String DOMAIN_TYPE = "Beschwerde3Klassifikator";



    private ResponseEntity<String> executeKikukoRequest(String input) {

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("content", input);
        map.add("type", "tool");
        map.add("domainType", DOMAIN_TYPE);

        HttpEntity<MultiValueMap> request = new HttpEntity<>(map ,header);

        // add authentication token
        RestTemplate template = new RestTemplateBuilder()
                .basicAuthentication("admin", "KIKuKoPass2018")
                .build();

        /* One Error was missing Message Converter for our class

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
        messageConverters.add(converter);

        template.setMessageConverters(messageConverters);
        */

        // get response
        return template.exchange(URL, HttpMethod.POST,
                request, String.class);
    }

    public static void main(String[] args) {
        /*
        Testing to get an Answer from KiKuKo
         */

        ResponseEntity<String> res= new KIKuKoClassifier().executeKikukoRequest("ausfal iansic");

        String matchesResponse = Objects.requireNonNull(res.getBody());

        System.out.println(matchesResponse);
        //List<TempPipeline> pipe = matchesResponse.getPipelines().getTempPipeline();
        //double erg = pipe.get(0).getTyp().getFahrtNichtErfolgt();

        //System.out.println(erg);


    }

}
