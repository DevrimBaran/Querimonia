package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.KikukoConatct;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;

import java.util.List;

public class KikukoExtractor extends KikukoConatct implements EntityExtractor{

  public KikukoExtractor(String domainType, String domainName) {
      super("domain", "QuerimoniaExtract");
  }

  @Override
  public List<NamedEntity> extractEntities(String text) {
      KikukoResponse response = executeKikukoRequest(text);


      return null;
  }
}
