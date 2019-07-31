package de.fraunhofer.iao.querimonia.db.combination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class LineStopCombination implements Identifiable<Long> {

  @Id
  @GeneratedValue
  private long id;

  private String line;

  private String stop;

  private String place;

  @SuppressWarnings("unused")
  @JsonCreator
  public LineStopCombination(@JsonProperty("line") String line,
                             @JsonProperty("stop") String stop,
                             @JsonProperty("place") String place) {
    this.line = line;
    this.stop = stop;
    this.place = place;
  }

  @SuppressWarnings("unused")
  private LineStopCombination() {
    // for hibernate
  }

  public Long getId() {
    return id;
  }

  public String getLine() {
    return line;
  }

  public String getStop() {
    return stop;
  }

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

    LineStopCombination that = (LineStopCombination) o;

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
