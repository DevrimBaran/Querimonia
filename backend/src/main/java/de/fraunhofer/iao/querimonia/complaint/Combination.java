package de.fraunhofer.iao.querimonia.complaint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.Nullable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import tec.uom.lib.common.function.Identifiable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This POJO is a combination of a line, a stop and a place. It is used to group named entities.
 */
@Entity
public class Combination implements Identifiable<Long> {

  @Id
  @GeneratedValue
  @JsonIgnore
  private long id;

  @JsonProperty("Linie")
  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String line;

  @JsonProperty("Haltestelle")
  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String stop;

  @JsonProperty("Ort")
  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String place;

  @SuppressWarnings("unused")
  @JsonCreator
  public Combination(
      @Nullable
      @JsonProperty("Linie")
          String line,
      @Nullable
      @JsonProperty("Haltestelle")
          String stop,
      @Nullable
      @JsonProperty("Ort")
          String place
  ) {
    this.line = line;
    this.stop = stop;
    this.place = place;
  }

  @SuppressWarnings("unused")
  private Combination() {
    // for hibernate
  }

  public Long getId() {
    return id;
  }

  @JsonProperty("Linie")
  @Nullable
  public String getLine() {
    return line;
  }

  @JsonProperty("Haltestelle")
  @Nullable
  public String getStop() {
    return stop;
  }

  @JsonProperty("Ort")
  @Nullable
  public String getPlace() {
    return place;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Combination that = (Combination) o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(line, that.line)
        .append(stop, that.stop)
        .append(place, that.place)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(id)
        .append(line)
        .append(stop)
        .append(place)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("line", line)
        .append("stop", stop)
        .append("place", place)
        .toString();
  }
}
