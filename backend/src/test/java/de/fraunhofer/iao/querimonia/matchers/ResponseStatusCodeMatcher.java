package de.fraunhofer.iao.querimonia.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

public class ResponseStatusCodeMatcher<T extends ResponseEntity<?>> extends TypeSafeMatcher<T> {

  private final HttpStatus status;

  private ResponseStatusCodeMatcher(HttpStatus status) {
    this.status = status;
  }

  @Override
  protected boolean matchesSafely(T item) {
    return Objects.equals(status, item.getStatusCode());
  }

  @Override
  public void describeTo(Description description) {
    description.appendValue(status);
  }

  public static <T extends ResponseEntity<?>> ResponseStatusCodeMatcher<T> hasStatusCode(
      HttpStatus status) {
    return new ResponseStatusCodeMatcher<>(status);
  }
}
