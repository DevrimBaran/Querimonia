package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.rest.contact.KiKuKoContact;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.FoundEntity;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.KikukoResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KikukoExtractor extends KiKuKoContact implements EntityExtractor {

  private final String domainName;
  private static HashMap<String, String> knownExtractors;
  static {
    knownExtractors = new HashMap<>();
    knownExtractors.put("Linien Extraktor", "Linie");
    knownExtractors.put("Vorgangsnummer", "Vorgangsnummer");
    knownExtractors.put("[Extern] Datum Extraktor", "Datum");
    knownExtractors.put("[Extern] Geldbetrag", "Geldbetrag");
    knownExtractors.put("[Extern] Personen Extraktor", "Person");
    knownExtractors.put("[Extern] Telefonnummer", "Telefon");
    knownExtractors.put("[Fuzzy] Haltestellen", "Halltestelle");
    knownExtractors.put("[Fuzzy] Ortsnamen", "Ort");
  }

  public KikukoExtractor(String domainType, String domainName) {
    super(domainType, domainName);
    this.domainName = domainName;
  }

  @Override
  public List<NamedEntity> extractEntities(String text) {
    KikukoResponse response = executeKikukoRequest(text);
    LinkedHashMap<String, List<FoundEntity>> allPipes = response.getPipelines();

    List<NamedEntity> entities = new LinkedList<>();

    try {
      allPipes.forEach((name, entityList) -> {
        for (FoundEntity entity : entityList) {
          entities.add(
              new NamedEntityBuilder().setLabel(name)
                  .setStart(entity.getStartposition())
                  .setEnd(entity.getEndposition())
                  .setExtractor(domainName)
                  .setValue(entity.getTyp().containsValue(1.0d)
                      ? entity.getText()
                      : entity.getTyp().keySet().stream().findFirst().orElse(entity.getText()))
                  .createNamedEntity());
        }
      });
    } catch (NoSuchElementException ignored) {

    }

    return entities;
  }

  /**
   * Determines the position of the number range (Important: Prefix with length max 2/3 are
   * ignored).
   *
   * @param text text to be evaluated
   *
   * @return Array containing the distance between start-index/end-index of the number range and the
   *     beginning/ending
   */
  private static int[] matchesNumber(String text) {
    Pattern pattern = Pattern.compile("[0-9]+");
    Matcher matcher = pattern.matcher(text);
    // Check all occurrences
    int[] numberPosition = {0, text.length()};
    while (matcher.find()) {
      //Prefix of line number should not be omitted
      if (matcher.start() > 2 && (matcher.start() > 3 || text.charAt(2) != ' ')) {
        numberPosition[0] = matcher.start();
      }
      numberPosition[1] = text.length() - matcher.end();
    }
    return numberPosition;
  }

}
