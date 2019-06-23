package de.fraunhofer.iao.querimonia.nlp.extractor;

import de.fraunhofer.iao.querimonia.nlp.KikukoConatct;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.ExtractorPipelines;
import de.fraunhofer.iao.querimonia.rest.restobjects.kikuko.ExtractorResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KikukoExtractor extends KikukoConatct<ExtractorResponse> implements EntityExtractor {

  public KikukoExtractor() {
    super("domain", "QuerimoniaExtract");
  }

  @Override
  public List<NamedEntity> extractEntities(String text) {
    ExtractorResponse response = executeKikukoRequest(text, ExtractorResponse[].class);
    ExtractorPipelines allPipes = response.getPipelines();

    List<NamedEntity> entities = new LinkedList<>();

    allPipes.getFuzhaltestellen().forEach(e -> entities.add(new NamedEntity("Bushaltestelle",
                                                                             e.getStartposition(),
                                                                             e.getEndposition())));
    allPipes.getLinienExtraktor().forEach(e -> entities.add(new NamedEntity("Linie",
                                                                       e.getStartposition()+matchesNumber(e.getText())[0],
                                                                        e.getEndposition()-matchesNumber(e.getText())[1])));
    allPipes.getExtdatumExtraktor().forEach(e -> entities.add(new NamedEntity("Datum",
                                                                             e.getStartposition(),
                                                                             e.getEndposition())));
    allPipes.getExtgeldbetrag().forEach(e -> entities.add(new NamedEntity("Geldbetrag",
                                                                             e.getStartposition(),
                                                                             e.getEndposition())));
    allPipes.getExttelefonnummer().forEach(e -> entities.add(new NamedEntity("Telefonnummer",
                                                                             e.getStartposition(),
                                                                             e.getEndposition())));
    allPipes.getFuzortsnamen().forEach(e -> entities.add(new NamedEntity("Ortsname",
                                                                             e.getStartposition(),
                                                                             e.getEndposition())));
    allPipes.getVorgangsnummer().forEach(e -> entities.add(new NamedEntity("Vorgangsnummer",
                                                                        e.getStartposition()+matchesNumber(e.getText())[0],
                                                                        e.getEndposition()-matchesNumber(e.getText())[1])));
    allPipes.getExtpersonExtraktor().forEach(e -> entities.add(new NamedEntity("Name",
                                                                             e.getStartposition(),
                                                                             e.getEndposition())));
    return entities;
  }

  /**
   * Determines the position of the number range (Important: Prefix with length max 2/3 are ignored)
   * @param text text to be evaluated
   * @return Array containing the distance between start-index/end-index of the number range and the beginning/ending
   */
  private static int[] matchesNumber(String text) {
    Pattern pattern = Pattern.compile("[0-9]+");
    Matcher matcher = pattern.matcher(text);
    // Check all occurrences
    int[] numberPosition={0,text.length()};
    while(matcher.find()) {
      //Prefix of line number should not be omitted
      if(matcher.start()>2&&(matcher.start()>3||text.charAt(2)!=' ')){
         numberPosition[0]=matcher.start();
      }
      numberPosition[1]=text.length()-matcher.end();
    }
    return numberPosition;
  }

}