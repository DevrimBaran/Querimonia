package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {
                        "[Fuzzy] Haltestellen",
                        "Linien Extraktor",
                        "[Extern] Personen Extraktor",
                        "[Extern] Datum Extraktor",
                        "[Extern] Geldbetrag",
                        "[Extern] Telefonnummer",
                        "[Fuzzy] Ortsnamen",
                        "Vorgangsnummer"
                    })
public class ExtractorPipelines {

  private List<Pipeline> fuzhaltestellen;
  private List<Pipeline> linienExtraktor;
  private List<Pipeline> extpersonExtraktor;
  private List<Pipeline> extdatumExtraktor;
  private List<Pipeline> extgeldbetrag;
  private List<Pipeline> exttelefonnummer;
  private List<Pipeline> fuzortsnamen;
  private List<Pipeline> vorgangsnummer;

  /**
   * Extractorpipelines from Kikuko.
   *
   * @param fuzhaltestellen    Results from bus information pipeline.
   * @param linienExtraktor    Results from datum extractor pipeline.
   * @param extpersonExtraktor Results from ext person extractor pipeline.
   * @param extdatumExtraktor  Results from ext datum extractor pipeline.
   * @param extgeldbetrag      Results from  money amount pipeline.
   * @param exttelefonnummer   Results from  money amount pipeline.
   * @param fuzortsnamen       Results from fuzzy location extractor pipeline.
   * @param vorgangsnummer     Results from operation-number extractor pipeline.
   */
  public ExtractorPipelines(
      @JsonProperty("[Fuzzy] Haltestellen") List<Pipeline> fuzhaltestellen,
      @JsonProperty("Linien Extraktor") List<Pipeline> linienExtraktor,
      @JsonProperty("[Extern] Personen Extraktor") List<Pipeline>
          extpersonExtraktor,
      @JsonProperty("[Extern] Datum Extraktor") List<Pipeline>
          extdatumExtraktor,
      @JsonProperty("[Extern] Geldbetrag") List<Pipeline> extgeldbetrag,
      @JsonProperty("[Extern] Telefonnummer") List<Pipeline> exttelefonnummer,
      @JsonProperty("[Fuzzy] Ortsnamen") List<Pipeline> fuzortsnamen,
      @JsonProperty("Vorgangsnummer") List<Pipeline> vorgangsnummer) {
    this.fuzhaltestellen = fuzhaltestellen;
    this.linienExtraktor = linienExtraktor;
    this.extpersonExtraktor = extpersonExtraktor;
    this.extdatumExtraktor = extdatumExtraktor;
    this.extgeldbetrag = extgeldbetrag;
    this.exttelefonnummer = exttelefonnummer;
    this.fuzortsnamen = fuzortsnamen;
    this.vorgangsnummer = vorgangsnummer;
  }

  public List<Pipeline> getFuzhaltestellen() {
    return fuzhaltestellen;
  }

  public void setFuzhaltestellen(List<Pipeline> fuzhaltestellen) {
    this.fuzhaltestellen = fuzhaltestellen;
  }

  public List<Pipeline> getLinienExtraktor() {
    return linienExtraktor;
  }

  public void setLinienExtraktor(List<Pipeline> linienExtraktor) {
    this.linienExtraktor = linienExtraktor;
  }

  public List<Pipeline> getExtpersonExtraktor() {
    return extpersonExtraktor;
  }

  public void setExtpersonExtraktor(List<Pipeline> extpersonExtraktor) {
    this.extpersonExtraktor = extpersonExtraktor;
  }

  public List<Pipeline> getExtdatumExtraktor() {
    return extdatumExtraktor;
  }

  public void setExtdatumExtraktor(List<Pipeline> extdatumExtraktor) {
    this.extdatumExtraktor = extdatumExtraktor;
  }

  public List<Pipeline> getExtgeldbetrag() {
    return extgeldbetrag;
  }

  public void setExtgeldbetrag(List<Pipeline> extgeldbetrag) {
    this.extgeldbetrag = extgeldbetrag;
  }

  public List<Pipeline> getExttelefonnummer() {
    return exttelefonnummer;
  }

  public void setExttelefonnummer(List<Pipeline> exttelefonnummer) {
    this.exttelefonnummer = exttelefonnummer;
  }

  public List<Pipeline> getFuzortsnamen() {
    return fuzortsnamen;
  }

  public void setFuzortsnamen(List<Pipeline> fuzortsnamen) {
    this.fuzortsnamen = fuzortsnamen;
  }

  public List<Pipeline> getVorgangsnummer() {
    return vorgangsnummer;
  }

  public void setVorgangsnummer(List<Pipeline> vorgangsnummer) {
    this.vorgangsnummer = vorgangsnummer;
  }

}
