package de.fraunhofer.iao.querimonia.rest.restobjects.kikuko;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder( {
        "Businformationen",
        "Datum Extraktor",
        "[Extern] Personen Extraktor",
        "[Extern] Datum Extraktor",
        "Personen NEU",
        "Money amount",
        "Personen Extraktor"
})
public class ExtractorPipelines {

    public ExtractorPipelines(@JsonProperty("Businformationen") List<TempPipeline> busInformationen,
                              @JsonProperty("Datum Extraktor") List<TempPipeline> datumExtraktor,
                              @JsonProperty("[Extern] Personen Extraktor") List<TempPipeline> extpersonExtraktor,
                              @JsonProperty("[Extern] Datum Extraktor") List<TempPipeline> extdatumExtraktor,
                              @JsonProperty("Personen NEU") List<TempPipeline> personNeu,
                              @JsonProperty("Money amount") List<TempPipeline> moneyAmount,
                              @JsonProperty("Personen Extraktor") List<TempPipeline> personExtraktor) {
        this.busInformationen = busInformationen;
        this.datumExtraktor = datumExtraktor;
        this.extpersonExtraktor = extpersonExtraktor;
        this.extdatumExtraktor = extdatumExtraktor;
        this.personNeu = personNeu;
        this.moneyAmount = moneyAmount;
        this.personExtraktor = personExtraktor;
    }

    private List<TempPipeline> busInformationen;
    private List<TempPipeline> datumExtraktor ;
    private List<TempPipeline> extpersonExtraktor;
    private List<TempPipeline> extdatumExtraktor;
    private List<TempPipeline> personNeu;
    private List<TempPipeline> moneyAmount;
    private List<TempPipeline> personExtraktor;

    public List<TempPipeline> getBusInformationen() {
        return busInformationen;
    }

    public void setBusInformationen(List<TempPipeline> busInformationen) {
        this.busInformationen = busInformationen;
    }

    public List<TempPipeline> getDatumExtraktor() {
        return datumExtraktor;
    }

    public void setDatumExtraktor(List<TempPipeline> datumExtraktor) {
        this.datumExtraktor = datumExtraktor;
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

    public List<TempPipeline> getPersonNeu() {
        return personNeu;
    }

    public void setPersonNeu(List<TempPipeline> personNeu) {
        this.personNeu = personNeu;
    }

    public List<TempPipeline> getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(List<TempPipeline> moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public List<TempPipeline> getPersonExtraktor() {
        return personExtraktor;
    }

    public void setPersonExtraktor(List<TempPipeline> personExtraktor) {
        this.personExtraktor = personExtraktor;
    }


}
