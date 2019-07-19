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


}
