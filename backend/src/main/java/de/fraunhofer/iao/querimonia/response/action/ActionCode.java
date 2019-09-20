package de.fraunhofer.iao.querimonia.response.action;

public enum ActionCode {
  ATTACH_VOUCHER("Ein Gutschein wird dem Kunden versendet"),
  COMPENSATION("Schadensersatz für aufgekommene Kosten werden erstattet"),
  SEND_MAIL("Eine Mail an einen Dritten wird versendet");

  private final String description;

  ActionCode(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
