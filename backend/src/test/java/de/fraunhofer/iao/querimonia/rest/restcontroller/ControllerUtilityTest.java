package de.fraunhofer.iao.querimonia.rest.restcontroller;

import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Unit test class for Controller Utility
 *
 * @author simon
 */
public class ControllerUtilityTest {

  @SuppressWarnings( {"NumericOverflow", "divzero"})
  private static int testMethod(int behaviour) {
    switch (behaviour) {
      case 0:
        throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "error", "error");
      case 1:
        return 1 / 0;
      default:
        return behaviour;
    }
  }

  @Test
  public void testTryAndCatchSupplier() {
    Supplier<Integer> supplier0 = () -> testMethod(0);
    Supplier<Integer> supplier1 = () -> testMethod(1);
    Supplier<Integer> supplier2 = () -> testMethod(2);

    QuerimoniaException response0 = (QuerimoniaException) ControllerUtility
        .tryAndCatch(supplier0).getBody();
    QuerimoniaException response1 = (QuerimoniaException) ControllerUtility
        .tryAndCatch(supplier1).getBody();
    ResponseEntity<?> response2 = ControllerUtility.tryAndCatch(supplier2);

    assertNotNull(response0);
    assertNotNull(response1);
    assertNotNull(response2.getBody());

    assertEquals(QuerimoniaException.class, response0.getClass());
    assertEquals(QuerimoniaException.class, response1.getClass());
    assertEquals(Integer.class, response2.getBody().getClass());

    assertEquals(HttpStatus.BAD_REQUEST, response0.getStatus());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response1.getStatus());
    assertEquals(HttpStatus.OK, response2.getStatusCode());
  }

  @Test
  public void testTryAndCatchRunnable() {
    Runnable runnable0 = () -> testMethod(0);
    Runnable runnable1 = () -> testMethod(1);
    Runnable runnable2 = () -> testMethod(2);

    QuerimoniaException response0 = (QuerimoniaException) ControllerUtility
        .tryAndCatch(runnable0).getBody();
    QuerimoniaException response1 = (QuerimoniaException) ControllerUtility
        .tryAndCatch(runnable1).getBody();
    ResponseEntity<?> response2 = ControllerUtility.tryAndCatch(runnable2);

    assertNotNull(response0);
    assertNotNull(response1);
    assertNull(response2.getBody());

    assertEquals(QuerimoniaException.class, response0.getClass());
    assertEquals(QuerimoniaException.class, response1.getClass());
    assertEquals(ResponseEntity.class, response2.getClass());

    assertEquals(HttpStatus.BAD_REQUEST, response0.getStatus());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response1.getStatus());
    assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
  }

  @Test
  public void testTryAndCatchFull() {
    Supplier<Integer> supplier0 = () -> testMethod(0);
    Supplier<Integer> supplier1 = () -> testMethod(1);
    Supplier<Integer> supplier2 = () -> testMethod(2);

    QuerimoniaException response0 = (QuerimoniaException) ControllerUtility
        .tryAndCatch(supplier0, HttpStatus.OK).getBody();
    QuerimoniaException response1 = (QuerimoniaException) ControllerUtility
        .tryAndCatch(supplier1, HttpStatus.OK).getBody();
    ResponseEntity<?> response2 = ControllerUtility.tryAndCatch(supplier2, HttpStatus.OK);

    assertNotNull(response0);
    assertNotNull(response1);
    assertNotNull(response2.getBody());

    assertEquals(QuerimoniaException.class, response0.getClass());
    assertEquals(QuerimoniaException.class, response1.getClass());
    assertEquals(Integer.class, response2.getBody().getClass());

    assertEquals(HttpStatus.BAD_REQUEST, response0.getStatus());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response1.getStatus());
    assertEquals(HttpStatus.OK, response2.getStatusCode());
  }
}
