package de.fraunhofer.iao.querimonia.response.generation;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Unit test class for ResponseSuggestion
 * @author Simon Weiler
 */
public class ResponseSuggestionTest {

  @Test
  public void testHashCode() {
    String componentText1 = "test test 123";
    ResponseComponent responseComponent1 =
        new ResponseComponentBuilder()
            .setComponentName("Komponente")
            .setPriority(0)
            .setComponentTexts(Collections.singletonList(componentText1))
            .setActions(Collections.emptyList())
            .setRulesXml("<Rules><EntityAvailable label=\"Name\"/></Rules>")
            .createResponseComponent();

    String componentText2 = "test test 123";
    ResponseComponent responseComponent2 =
        new ResponseComponentBuilder()
            .setComponentName("Komponente")
            .setPriority(0)
            .setComponentTexts(Collections.singletonList(componentText2))
            .setActions(Collections.emptyList())
            .setRulesXml("<Rules><EntityAvailable label=\"Name\"/></Rules>")
            .createResponseComponent();

    String componentText3 = "40 grad ist zu warm";
    ResponseComponent responseComponent3 =
        new ResponseComponentBuilder()
            .setComponentName("KlimawandelIstReal")
            .setPriority(99)
            .setComponentTexts(Collections.singletonList(componentText3))
            .setActions(Collections.emptyList())
            .setRulesXml("<Rules><EntityAvailable label=\"Wasser\"/></Rules>")
            .createResponseComponent();

    CompletedResponseComponent completedResponseComponent1 = new CompletedResponseComponent(
        responseComponent1.toPersistableComponent(), Collections.emptyList());
    CompletedResponseComponent completedResponseComponent2 = new CompletedResponseComponent(
        responseComponent2.toPersistableComponent(), Collections.emptyList());
    CompletedResponseComponent completedResponseComponent3 = new CompletedResponseComponent(
        responseComponent3.toPersistableComponent(), Collections.emptyList());

    ResponseSuggestion suggestion1 = new ResponseSuggestion(
        Collections.singletonList(completedResponseComponent1), "");

    ResponseSuggestion suggestion2 = new ResponseSuggestion(
        Collections.singletonList(completedResponseComponent2), "");

    ResponseSuggestion suggestion3 = new ResponseSuggestion(
        Collections.singletonList(completedResponseComponent3), "");

    int hashCode1 = suggestion1.hashCode();
    int hashCode2 = suggestion2.hashCode();
    int hashCode3 = suggestion3.hashCode();

    assertNotEquals(hashCode1, hashCode2);
    assertNotEquals(hashCode2,hashCode3);
  }

  @Test
  public void testToString() {
    String componentText1 = "test test 123";
    ResponseComponent responseComponent1 =
        new ResponseComponentBuilder()
            .setComponentName("Komponente")
            .setPriority(0)
            .setComponentTexts(Collections.singletonList(componentText1))
            .setActions(Collections.emptyList())
            .setRulesXml("<Rules><EntityAvailable label=\"Name\"/></Rules>")
            .createResponseComponent();

    CompletedResponseComponent completedResponseComponent1 = new CompletedResponseComponent(
        responseComponent1.toPersistableComponent(), Collections.emptyList());

    ResponseSuggestion suggestion1 = new ResponseSuggestion(
        Collections.singletonList(completedResponseComponent1), "");

    String subString0 = "de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion";
    String subString1 = "[id=0,responseComponents=[de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent";
    String subString2 = "],actions=[]]";
    String suggestionString = suggestion1.toString();

    assertTrue(suggestionString.contains(subString0));
    assertTrue(suggestionString.contains(subString1));
    assertFalse(suggestionString.contains(subString2));
  }
}
