package de.fraunhofer.iao.querimonia.nlp.extractor;

import com.fasterxml.jackson.databind.util.JSONPObject;
import de.fraunhofer.iao.querimonia.nlp.KikukoConatct;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.ExtractorPipelines;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.ExtractorResponse;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.Pipelines;
import jdk.nashorn.internal.ir.debug.JSONWriter;

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

      allPipes.getBusInformationen().forEach(e -> entities.add(new NamedEntity("Bushaltestelle", e.getStartposition(),e.getEndposition())));
      allPipes.getDatumExtraktor().forEach(e -> entities.add(new NamedEntity("Datum", e.getStartposition(),e.getEndposition())));
      allPipes.getExtdatumExtraktor().forEach(e -> entities.add(new NamedEntity("Datum", e.getStartposition(),e.getEndposition())));
      allPipes.getMoneyAmount().forEach(e -> entities.add(new NamedEntity("Money Amount", e.getStartposition(),e.getEndposition())));
      allPipes.getExtpersonExtraktor().forEach(e -> entities.add(new NamedEntity("Person", e.getStartposition(),e.getEndposition())));
      allPipes.getPersonExtraktor().forEach(e -> entities.add(new NamedEntity("Person", e.getStartposition(),e.getEndposition())));
      allPipes.getPersonNeu().forEach(e -> entities.add(new NamedEntity("Person", e.getStartposition(),e.getEndposition())));

      return entities;
  }

}
