package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedHashMap;

/**
 * CategoryStats summarizes all stats for a specific category.
 */
@JsonPropertyOrder(value = {
    "count",
    "tendency",
    "emotion",
    "status"
})

public class CategoryStats {

  private int count;
  private double tendency;
  private LinkedHashMap<String, Double> emotion;
  private LinkedHashMap<String, Double> status;


  @JsonCreator
  public CategoryStats(int count, double tendency, LinkedHashMap<String, Double> emotion,
                       LinkedHashMap<String, Double> status) {
    this.count = count;
    this.tendency = tendency;
    this.emotion = emotion;
    this.status = status;
  }

  @SuppressWarnings("unused")
  private CategoryStats() {
    // for hibernate
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public double getTendency() {
    return tendency;
  }

  public void setTendency(double tendency) {
    this.tendency = tendency;
  }

  public LinkedHashMap<String, Double> getEmotion() {
    return emotion;
  }

  public void setEmotion(LinkedHashMap<String, Double> emotion) {
    this.emotion = emotion;
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
        .append("tendency", tendency)
        .append("emotion", emotion)
        .append("status", status)
        .toString();
  }
}
