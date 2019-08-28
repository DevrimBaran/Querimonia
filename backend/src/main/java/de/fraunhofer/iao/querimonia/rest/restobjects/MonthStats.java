package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedHashMap;

/**
 * MonthStats summarizes all stats for a specific month.
 */
@JsonPropertyOrder(value = {
    "count",
    "status"
})
public class MonthStats {

  private long count;
  private LinkedHashMap<String, Double> status;

  @JsonCreator
  public MonthStats(long count, LinkedHashMap<String, Double> status) {
    this.count = count;
    this.status = status;
  }

  @SuppressWarnings("unused")
  private MonthStats() {
    // for hibernate
  }

  public long getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public LinkedHashMap<String, Double> getStatus() {
    return status;
  }

  public void setStatus(LinkedHashMap<String, Double> status) {
    this.status = status;
  }


  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("count", count)
        .append("status", status)
        .toString();
  }
}
