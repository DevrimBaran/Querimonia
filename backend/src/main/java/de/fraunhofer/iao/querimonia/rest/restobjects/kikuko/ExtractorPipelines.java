package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
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

  private List<TempPipeline> fuzhaltestellen;
  private List<TempPipeline> linienExtraktor;
  private List<TempPipeline> extpersonExtraktor;
  private List<TempPipeline> extdatumExtraktor;
  private List<TempPipeline> extgeldbetrag;
  private List<TempPipeline> exttelefonnummer;
  private List<TempPipeline> fuzortsnamen;
  private List<TempPipeline> vorgangsnummer;
  /**
   * Extractorpipelines from Kikuko.
   *
   * @param fuzhaltestellen   Results from bus information pipeline.
   * @param linienExtraktor     Results from datum extractor pipeline.
   * @param extpersonExtraktor Results from ext person extractor pipeline.
   * @param extdatumExtraktor  Results from ext datum extractor pipeline.
   * @param extgeldbetrag      Results from  money amount pipeline.
   * @param exttelefonnummer      Results from  money amount pipeline.
   * @param fuzortsnamen       Results from fuzzy location extractor pipeline.
   * @param vorgangsnummer     Results from operation-number extractor pipeline.
   */
  @SuppressWarnings("CheckStyle")
  public ExtractorPipelines(@JsonProperty("[Fuzzy] Haltestellen") List<TempPipeline> fuzhaltestellen,
                            @JsonProperty("Linien Extraktor") List<TempPipeline> linienExtraktor,
                            @JsonProperty("[Extern] Personen Extraktor") List<TempPipeline>
                                extpersonExtraktor,
                            @JsonProperty("[Extern] Datum Extraktor") List<TempPipeline>
                                extdatumExtraktor,
                            @JsonProperty("[Extern] Geldbetrag") List<TempPipeline> extgeldbetrag,
                            @JsonProperty("[Extern] Telefonnummer") List<TempPipeline> exttelefonnummer,
                            @JsonProperty("[Fuzzy] Ortsnamen") List<TempPipeline> fuzortsnamen,
                            @JsonProperty("Vorgangsnummer") List<TempPipeline> vorgangsnummer) {
    this.fuzhaltestellen = fuzhaltestellen;
    this.linienExtraktor=linienExtraktor;
    this.extpersonExtraktor = extpersonExtraktor;
    this.extdatumExtraktor = extdatumExtraktor;
    this.extgeldbetrag = extgeldbetrag;
    this.exttelefonnummer = exttelefonnummer;
    this.fuzortsnamen = fuzortsnamen;
    this.vorgangsnummer=vorgangsnummer;
  }

  public List<TempPipeline> getFuzhaltestellen() {
    return fuzhaltestellen;
  }

  public void setFuzhaltestellen(List<TempPipeline> fuzhaltestellen) {
    this.fuzhaltestellen = fuzhaltestellen;
  }

  public List<TempPipeline> getLinienExtraktor() {
    return linienExtraktor;
  }

  public void setLinienExtraktor(List<TempPipeline> linienExtraktor) {
    this.linienExtraktor = linienExtraktor;
  }

  public List<TempPipeline> getExtpersonExtraktor() {
    return extpersonExtraktor;
  }

  public void setExtpersonExtraktor(List<TempPipeline> extpersonExtraktor) {
    this.extpersonExtraktor = extpersonExtraktor;
  }

  public List<TempPipeline> getExtdatumExtraktor() {
    return extdatumExtraktor;
  }

  public void setExtdatumExtraktor(List<TempPipeline> extdatumExtraktor) {
    this.extdatumExtraktor = extdatumExtraktor;
  }

  public List<TempPipeline> getExtgeldbetrag() {
    return extgeldbetrag;
  }

  public void setExtgeldbetrag(List<TempPipeline> extgeldbetrag) {
    this.extgeldbetrag = extgeldbetrag;
  }

  public List<TempPipeline> getExttelefonnummer() {
    return exttelefonnummer;
  }

  public void setExttelefonnummer(List<TempPipeline> exttelefonnummer) {
    this.exttelefonnummer = exttelefonnummer;
  }

  public List<TempPipeline> getFuzortsnamen() {
    return fuzortsnamen;
  }

  public void setFuzortsnamen(List<TempPipeline> fuzortsnamen) {
    this.fuzortsnamen = fuzortsnamen;
  }

  public List<TempPipeline> getVorgangsnummer() {
    return vorgangsnummer;
  }

  public void setVorgangsnummer(List<TempPipeline> vorgangsnummer) {
    this.vorgangsnummer = vorgangsnummer;
  }

}
