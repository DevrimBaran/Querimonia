package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.KikukoConatct;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.ExtractorPipelines;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.ExtractorResponse;

import java.util.LinkedList;
import java.util.List;

public class KikukoExtractor extends KikukoConatct<ExtractorResponse> implements EntityExtractor {

  public KikukoExtractor() {
    super("domain", "QuerimoniaExtract");
  }

  @Override
  public List<NamedEntity> extractEntities(String text) {
    ExtractorResponse response = executeKikukoRequest(text, ExtractorResponse[].class);
    ExtractorPipelines allPipes = response.getPipelines();

    List<NamedEntity> entities = new LinkedList<>();

    allPipes.getBusInformationen().forEach(e -> entities.add(new NamedEntity("Bushaltestelle",
        e.getStartposition(), e.getEndposition())));
    allPipes.getDatumExtraktor().forEach(e -> entities.add(new NamedEntity("Datum",
        e.getStartposition(), e.getEndposition())));
    allPipes.getExtdatumExtraktor().forEach(e -> entities.add(new NamedEntity("Datum",
        e.getStartposition(), e.getEndposition())));
    // for some reason we have to subtract 1 from the end position here
    allPipes.getExtgeldbetrag().forEach(e -> entities.add(new NamedEntity("Geldbetrag",
        e.getStartposition(), e.getEndposition() - 1)));/*
    allPipes.getExtpersonExtraktor().forEach(e -> entities.add(new NamedEntity("Name",
        e.getStartposition(), e.getEndposition())));*/
    allPipes.getPersonExtraktor().forEach(e -> entities.add(new NamedEntity("Name",
        e.getStartposition(), e.getEndposition())));/*
    allPipes.getPersonNeu().forEach(e -> entities.add(new NamedEntity("Name",
        e.getStartposition(), e.getEndposition() - 1)));*/

    return entities;
  }

}
