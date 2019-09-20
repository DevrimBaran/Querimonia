package de.fraunhofer.iao.querimonia.rest.restobjects;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class CallbackResponse {

  @JsonProperty("id")
  private long id;
  @JsonProperty("text")
  private String responseText;

  public CallbackResponse(long id, String responseText) {
    this.id = id;
    this.responseText = responseText;
  }

  public long getId() {
    return id;
  }

  public String getResponseText() {
    return responseText;
  }
}
