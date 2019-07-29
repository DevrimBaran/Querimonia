package de.fraunhofer.iao.querimonia.nlp;

import org.springframework.lang.Nullable;

import java.util.Objects;

public class NamedEntityBuilder {
  @Nullable
  private String label;
  private int start;
  private int end;
  private boolean setByUser = false;
  @Nullable
  private String extractor;
  @Nullable
  private String value;

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

  public NamedEntity createNamedEntity() {
    return new NamedEntity(Objects.requireNonNull(label), start, end, setByUser,
        Objects.requireNonNull(extractor), Objects.requireNonNull(value));
  }
}