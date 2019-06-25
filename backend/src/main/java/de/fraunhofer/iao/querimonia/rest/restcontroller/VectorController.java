package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.nlp.wordvector.FlaskCalculations;
import de.fraunhofer.iao.querimonia.rest.restobjects.CalculationRequest;
import de.fraunhofer.iao.querimonia.rest.restobjects.TextInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This controller manages calculations with word vectors.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
public class VectorController {

  private static final Logger logger = LoggerFactory.getLogger(VectorController.class);


  @PostMapping(value = "/api/vector/nearest_neighbour")
  public Map<String, Double> nearestNeighbor(@RequestBody TextInput input,
                                             @RequestParam(value = "corpus",
                                                 defaultValue = "beschwerden3kPolished.bin") String corpus) {
    FlaskCalculations calculator = new FlaskCalculations();
    logger.info("Recieved: " + input.getText());
    return calculator.nearestNeighbour(input.getText(), corpus);
  }

  @PostMapping(value = "/api/vector/calculation", produces = "application/json",
      consumes = "application/json")
  public Map<String, Double> calculation(@RequestBody CalculationRequest request) {
    FlaskCalculations calculator = new FlaskCalculations();
    if (!(request.getVector3() == null)) {
      logger.info("Extended detected");
      return calculator.vectorCalculations(request.getVector1(), request.getOperator1(),
          request.getVector2(), request.getOperator2(),
          request.getVector3(), request.getCorpus());
    }

    return calculator.vectorCalculations(request.getVector1(), request.getOperator1(),
        request.getVector2(), request.getCorpus());
  }

}
