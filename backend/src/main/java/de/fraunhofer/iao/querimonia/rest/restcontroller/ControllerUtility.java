package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

/**
 * Class with helper functions for the REST controllers.
 */
public class ControllerUtility {

  private static final Logger logger = LoggerFactory.getLogger("EXCEPTION");

  /**
   * Tries to execute the given try-clause and catches QuerimoniaExceptions and other exception
   * which get handled as unexpected, fatal exceptions.
   *
   * @param tryClause this is the code that may throw exceptions. The supplier should return the
   *                  response body for the response entity or null, if their should be no body.
   *
   * @return a response entity with either status code 200 and the returned value of the tryClause
   *     as body or a response entity with the QuerimoniaException that was thrown as body.
   */
  public static ResponseEntity<?> tryAndCatch(Supplier<?> tryClause) {
    return tryAndCatch(tryClause, HttpStatus.OK);
  }

  /**
   * Tries to execute the given try-clause and catches QuerimoniaExceptions and other exception
   * which get handled as unexpected, fatal exceptions.
   *
   * @param tryClause this is the code that may throw exceptions.
   *
   * @return a response entity with either status code 204 and no body or a response entity with the
   *     QuerimoniaException that was thrown as body.
   */
  public static ResponseEntity<?> tryAndCatch(Runnable tryClause) {
    return tryAndCatch(() -> {
      tryClause.run();
      return null;
    }, HttpStatus.NO_CONTENT);
  }

  /**
   * Tries to execute the given try-clause and catches QuerimoniaExceptions and other exception
   * which get handled as unexpected, fatal exceptions.
   *
   * @param tryClause this is the code that may throw exceptions. The supplier should return the
   *                  response body for the response entity or null, if their should be no body.
   * @param onSuccess this status code gets returned by the controller, when no exceptions
   *                  occurred.
   *
   * @return a response entity with either the onSuccess status code and the returned value of the
   *     tryClause as body or a response entity with the QuerimoniaException that was thrown as
   *     body.
   */
  public static ResponseEntity<?> tryAndCatch(Supplier<?> tryClause, HttpStatus onSuccess) {
    try {
      return new ResponseEntity<>(tryClause.get(), onSuccess);
    } catch (QuerimoniaException e) {
      logger.info("Exception occurred", e);
      return new ResponseEntity<>(e, e.getStatus());

    } catch (Exception e) {
      logger.error("Unexpected Exception occurred", e);
      return new ResponseEntity<>(new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR,
          e.getMessage(), e, "Unerwarteter Fehler"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
