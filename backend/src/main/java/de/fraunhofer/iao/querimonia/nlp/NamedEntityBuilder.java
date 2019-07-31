package de.fraunhofer.iao.querimonia.nlp;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

public class NamedEntityBuilder {
  @Nullable
  private String label;
  private int start;
  private int end;
  private boolean setByUser = false;
  private boolean preferred = false;
  @Nullable
  private String extractor;
  @Nullable
  private String value;
  @NonNull
  private String color = "#222222";

  public NamedEntityBuilder setLabel(String label) {
    this.label = label;
    return this;
  }

  public NamedEntityBuilder setStart(int start) {
    this.start = start;
    return this;
  }

  public NamedEntityBuilder setEnd(int end) {
    this.end = end;
    return this;
  }

  public NamedEntityBuilder setSetByUser(boolean setByUser) {
    this.setByUser = setByUser;
    return this;
  }

  public NamedEntityBuilder setExtractor(String extractor) {
    this.extractor = extractor;
    return this;
  }

  public NamedEntityBuilder setValue(String value) {
    this.value = value;
    return this;
  }

  public NamedEntityBuilder setPreferred(boolean preferred) {
    this.preferred = preferred;
    return this;
  }

  public NamedEntityBuilder setColor(@Nullable String color) {
    this.color = color;
    return this;
  }

  public NamedEntity createNamedEntity() {
    return new NamedEntity(Objects.requireNonNull(label), start, end, setByUser, preferred,
        Objects.requireNonNull(extractor), Objects.requireNonNull(value),
        Objects.requireNonNull(color));
  }
}