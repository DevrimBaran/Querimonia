package de.fraunhofer.iao.querimonia.response.component;

import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponentBuilder;

import java.util.Collections;
import java.util.List;

public class TestComponents {

  public static final ResponseComponent COMPONENT_A = new ResponseComponentBuilder()
      .setId(1)
      .setComponentName("A")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Guten Morgen", "Sehr geehrter Herr ${Name},"))
      .setPriority(0)
      .setRulesXml("<Rules><Property name=\"Kategorie\" value=\"Sonstiges\"/></Rules>")
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_B = new ResponseComponentBuilder()
      .setId(2)
      .setComponentName("B")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Beispiel"))
      .setRulesXml("")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_C = new ResponseComponentBuilder()
      .setId(3)
      .setComponentName("C")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Test test 123"))
      .setRulesXml("")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_D = new ResponseComponentBuilder()
      .setId(4)
      .setComponentName("D")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Tests sind toll"))
      .setRulesXml("")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_E = new ResponseComponentBuilder()
      .setId(5)
      .setComponentName("E")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Die Linie ${Linie} ist leider ausgefallen.",
          "Die Linie ${Linie} konnte heute nicht fahren"))
      .setRulesXml("<Rules><EntityAvailable label=\"Linie\" /></Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_NAME = new ResponseComponentBuilder()
      .setId(6)
      .setComponentName("Begrüßung")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Hallo ${Name}!"))
      .setRulesXml("<Rules>" +
          "<And>" +
          "<PredecessorCount max=\"0\" />" +
          "<EntityAvailable label=\"Name\" />" +
          "</And>" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_DATE = new ResponseComponentBuilder()
      .setId(7)
      .setComponentName("Eingangsdatum")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Danke für deine Beschwerde vom ${Eingangsdatum}."))
      .setRulesXml("<Rules>" +
          "<Predecessor matches=\"Begrüßung\" position=\"last\" />" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_TIME = new ResponseComponentBuilder()
      .setId(8)
      .setComponentName("Eingangszeit")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Diese ist um ${Eingangszeit} Uhr eingetroffen."))
      .setRulesXml("<Rules>" +
          "<Predecessor matches=\"Eingangsdatum\" position=\"last\" />" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_PROBLEM_NONE = new ResponseComponentBuilder()
      .setId(9)
      .setComponentName("ProblembeschreibungKeinePlatzhalter")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Darin hast du dich über ein Problem mit unserem Betrieb beschwert."))
      .setRulesXml("<Rules>" +
          "<And>" +
          "<Predecessor matches=\"Eingangszeit\" position=\"last\" />" +
          "<Not>" +
          "<EntityAvailable label=\"Haltestelle\" />" +
          "</Not>" +
          "<Not>" +
          "<EntityAvailable label=\"Linie\" />" +
          "</Not>" +
          "</And>" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_PROBLEM_ONLY_STOP = new ResponseComponentBuilder()
      .setId(10)
      .setComponentName("ProblembeschreibungNurHaltestelle")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Darin hast du dich über die Haltestelle ${Haltestelle} beschwert."))
      .setRulesXml("<Rules>" +
          "<And>" +
          "<Predecessor matches=\"Eingangszeit\" position=\"last\" />" +
          "<EntityAvailable label=\"Haltestelle\" />" +
          "<Not>" +
          "<EntityAvailable label=\"Linie\" />" +
          "</Not>" +
          "</And>" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_PROBLEM_ONLY_LINE = new ResponseComponentBuilder()
      .setId(11)
      .setComponentName("ProblembeschreibungNurLinie")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Darin hast du dich über die Linie ${Linie} beschwert."))
      .setRulesXml("<Rules>" +
          "<And>" +
          "<Predecessor matches=\"Eingangszeit\" position=\"last\" />" +
          "<Not>" +
          "<EntityAvailable label=\"Haltestelle\" />" +
          "</Not>" +
          "<EntityAvailable label=\"Linie\" />" +
          "</And>" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_PROBLEM_STOP_AND_LINE = new ResponseComponentBuilder()
      .setId(12)
      .setComponentName("ProblembeschreibungHaltestelleUndLinie")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Darin hast du dich über die Haltestelle ${Haltestelle} der Linie ${Linie} beschwert."))
      .setRulesXml("<Rules>" +
          "<And>" +
          "<Predecessor matches=\"Eingangszeit\" position=\"last\" />" +
          "<EntityAvailable label=\"Haltestelle\" />" +
          "<EntityAvailable label=\"Linie\" />" +
          "</And>" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_MONEY_WITH = new ResponseComponentBuilder()
      .setId(13)
      .setComponentName("ErstattungMitGeldbetrag")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Dafür zahlen wir dir ${Geldbetrag}!"))
      .setRulesXml("<Rules>" +
          "<And>" +
          "<Predecessor matches=\"Problembeschreibung.*\" position=\"last\" />" +
          "<EntityAvailable label=\"Geldbetrag\" />" +
          "</And>" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_MONEY_WITHOUT = new ResponseComponentBuilder()
      .setId(14)
      .setComponentName("ErstattungOhneGeldbetrag")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Dafür zahlen wir dir etwas Geld!"))
      .setRulesXml("<Rules>" +
          "<And>" +
          "<Predecessor matches=\"Problembeschreibung\" position=\"last\" />" +
          "<Not>" +
          "<EntityAvailable label=\"Geldbetrag\" />" +
          "</Not>" +
          "</And>" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();

  public static final ResponseComponent COMPONENT_FINISH = new ResponseComponentBuilder()
      .setId(15)
      .setComponentName("Schluss")
      .setActions(Collections.emptyList())
      .setComponentTexts(List.of("Tschüss!"))
      .setRulesXml("<Rules>" +
          "<Predecessor matches=\"Erstattung.*\" position=\"last\" />" +
          "</Rules>")
      .setPriority(0)
      .createResponseComponent();
}
