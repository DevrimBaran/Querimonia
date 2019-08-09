package de.fraunhofer.iao.querimonia.nlp;

import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

public class NamedEntityBuilder {
  private long id;
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

  /**
   * Creates a new named entity builder with no attributes set.
   */
  public NamedEntityBuilder() {

  }

  /**
   * Creates a new named entity builder with all the attributes from the given named entity.
   *
   * @param entity the entity which attributes are copied.
   */
  public NamedEntityBuilder(NamedEntity entity) {
    this.color = entity.getColor();
    this.end = entity.getEndIndex();
    this.extractor = entity.getExtractor();
    this.id = entity.getId();
    this.label = entity.getLabel();
    this.preferred = entity.isPreferred();
    this.setByUser = entity.isSetByUser();
    this.start = entity.getStartIndex();
    this.value = entity.getValue();
  }

  public NamedEntityBuilder setId(long id) {
    this.id = id;
    return this;
  }

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

  public NamedEntityBuilder setColor(@NotNull String color) {
    this.color = color;
    return this;
  }

  /**
   * Creates a new named entity object out of the attributes of the builder.
   *
   * @return a new named entity object.
   */
  public NamedEntity createNamedEntity() {
    return new NamedEntity(id, Objects.requireNonNull(label), Objects.requireNonNull(value), start,
        end, setByUser, preferred, Objects.requireNonNull(extractor),
        Objects.requireNonNull(color));
  }
}