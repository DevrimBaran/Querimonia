package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

/**
 * MonthStats summarizes all stats for a specific month.
 */
@JsonPropertyOrder(value = {
    "count",
    "status",
    "processingDuration"
})
public class MonthStats {

  private long count;
  private LinkedHashMap<String, Double> status;
  private double processingDuration;

  @JsonCreator
  public MonthStats(long count, LinkedHashMap<String, Double> status, double processingDuration) {
    this.count = count;
    this.status = status;
    this.processingDuration = processingDuration;
  }

  @SuppressWarnings("unused")
  private MonthStats() {
    // for hibernate
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public LinkedHashMap<String, Double> getStatus() {
    return status;
  }

  public void setStatus(LinkedHashMap<String, Double> status) {
    this.status = status;
  }

  public double getProcessingDuration() {
    return processingDuration;
  }

  public void setProcessingDuration(double processingDuration) {
    this.processingDuration = processingDuration;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("count", count)
        .append("status", status)
        .toString();
  }
}
