package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.rest.contact.KiKuKoContact;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.ExtractorPipelines;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.ExtractorResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KikukoExtractor extends KiKuKoContact<ExtractorResponse> implements EntityExtractor {

  private final String domainName;

  public KikukoExtractor(String domainType, String domainName) {
    super(domainType, domainName);
    this.domainName = domainName;
  }

  @Override
  public List<NamedEntity> extractEntities(String text) {
    ExtractorResponse response = executeKikukoRequest(text, ExtractorResponse[].class);
    ExtractorPipelines allPipes = response.getPipelines();

    List<NamedEntity> entities = new LinkedList<>();

    allPipes.getFuzhaltestellen().forEach(e -> entities.add(
        new NamedEntityBuilder().setLabel("Haltestelle")
            .setStart(e.getStartposition())
            .setEnd(e.getEndposition())
            .setExtractor(domainName)
            .setValue(e.getText())
            .createNamedEntity()));
    allPipes.getLinienExtraktor().forEach(e -> entities.add(
        new NamedEntityBuilder().setLabel("Linie")
            .setStart(e.getStartposition() + matchesNumber(e.getText())[0])
            .setEnd(e.getEndposition() - matchesNumber(e.getText())[1])
            .setExtractor(domainName)
            .setValue(e.getText())
            .createNamedEntity()));
    allPipes.getExtdatumExtraktor().forEach(e -> entities.add(
        new NamedEntityBuilder().setLabel("Datum")
            .setStart(e.getStartposition())
            .setEnd(e.getEndposition())
            .setExtractor(domainName)
            .setValue(e.getText())
            .createNamedEntity()));
    allPipes.getExtgeldbetrag().forEach(e -> entities.add(
        new NamedEntityBuilder().setLabel("Geldbetrag")
            .setStart(e.getStartposition())
            .setEnd(e.getEndposition())
            .setExtractor(domainName)
            .setValue(e.getText())
            .createNamedEntity()));
    allPipes.getExttelefonnummer().forEach(e -> entities.add(
        new NamedEntityBuilder().setLabel("Telefonnummer")
            .setStart(e.getStartposition())
            .setEnd(e.getEndposition())
            .setExtractor(domainName)
            .setValue(e.getText())
            .createNamedEntity()));
    allPipes.getFuzortsnamen().forEach(e -> entities.add(
        new NamedEntityBuilder().setLabel("Ortsname")
            .setStart(e.getStartposition())
            .setEnd(e.getEndposition())
            .setExtractor(domainName)
            .setValue(e.getText())
            .createNamedEntity()));
    allPipes.getVorgangsnummer().forEach(e -> entities.add(
        new NamedEntityBuilder().setLabel("Vorgangsnummer")
            .setStart(e.getStartposition() + matchesNumber(e.getText())[0])
            .setEnd(e.getEndposition() - matchesNumber(e.getText())[1])
            .setExtractor(domainName)
            .setValue(e.getText())
            .createNamedEntity()));
    allPipes.getExtpersonExtraktor().forEach(e -> entities.add(
        new NamedEntityBuilder().setLabel("Name")
            .setStart(e.getStartposition())
            .setEnd(e.getEndposition())
            .setExtractor(domainName)
            .setValue(e.getText())
            .createNamedEntity()));
    return entities;
  }

  /**
   * Determines the position of the number range (Important: Prefix with length max 2/3 are
   * ignored).
   *
   * @param text text to be evaluated
   * @return Array containing the distance between start-index/end-index of the number range and the
   * beginning/ending
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
