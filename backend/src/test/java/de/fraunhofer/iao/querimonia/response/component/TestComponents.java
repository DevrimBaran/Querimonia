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
}
