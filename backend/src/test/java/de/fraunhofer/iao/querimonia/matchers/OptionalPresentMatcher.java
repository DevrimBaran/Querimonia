package de.fraunhofer.iao.querimonia.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Optional;

public class OptionalPresentMatcher extends TypeSafeMatcher<Optional<?>> {
  @Override
  protected boolean matchesSafely(Optional<?> item) {
    return item.isPresent();
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("present");
  }

  public static OptionalPresentMatcher present() {
    return new OptionalPresentMatcher();
  }
}
