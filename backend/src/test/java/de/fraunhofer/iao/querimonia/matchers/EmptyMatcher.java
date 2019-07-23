package de.fraunhofer.iao.querimonia.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class EmptyMatcher<T extends Iterable<?>> extends TypeSafeMatcher<T> {

  @Override
  protected boolean matchesSafely(T item) {
    return !item.iterator().hasNext();
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("empty");
  }

  public static <T extends Iterable<?>> EmptyMatcher<T> empty() {
    return new EmptyMatcher<>();
  }
}
