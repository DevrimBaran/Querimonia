package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.rest.contact.KiKuKoContact;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.FoundEntity;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class KikukoExtractor extends KiKuKoContact implements EntityExtractor {

  private final String domainName;
  private final ExtractorDefinition extractorDefinition;

  public KikukoExtractor(String domainType, String domainName,
                         ExtractorDefinition extractorDefinition) {
    super(domainType, domainName);
    this.domainName = domainName;
    this.extractorDefinition = extractorDefinition;
  }

  @Override
  public List<NamedEntity> extractEntities(String text) {
    KikukoResponse response = executeKikukoRequest(text);
    LinkedHashMap<String, List<FoundEntity>> allPipes = response.getPipelines();

    List<NamedEntity> entities = new LinkedList<>();

    allPipes.forEach((name, entityList) -> {
      for (FoundEntity entity : entityList) {
        entities.add(
            new NamedEntityBuilder()
                .setLabel(extractorDefinition.getLabel())
                .setStart(entity.getStartposition())
                .setEnd(entity.getEndposition())
                .setExtractor(domainName)
                .setColor(extractorDefinition.getColor())
                .setValue(entity.getTyp().containsValue(1.0d)
                    ? entity.getText()
                    : entity
                        .getTyp()
                        .keySet()
                        .stream()
                        .findFirst()
                        .orElse(entity.getText()))
                .createNamedEntity());
      }
    });

    return entities;
  }

}
