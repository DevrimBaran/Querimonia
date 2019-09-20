
package de.fraunhofer.iao.querimonia.response.generation;

import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Unit test class for ResponseComponent
 */

public class ResponseComponentTest {

  private static class TestResponseSlice {

    private final boolean isPlaceholder;
    private final String content;

    private TestResponseSlice(boolean isPlaceholder, String content) {
      this.isPlaceholder = isPlaceholder;
      this.content = content;
    }

    private boolean isPlaceholder() {
      return isPlaceholder;
    }

    private String getContent() {
      return content;
    }
  }

  @Test
  public void testSetComponentTextStandard() {
    String componentText =
        "Guten Tag, Herr ${Name}. Wir haben Ihnen ${Geldbetrag} überwiesen. Auf Wiedersehen!";

    ArrayList<TestResponseSlice> correctSlices = new ArrayList<>();
    correctSlices.add(new TestResponseSlice(false, "Guten Tag, Herr "));
    correctSlices.add(new TestResponseSlice(true, "Name"));
    correctSlices.add(new TestResponseSlice(false, ". Wir haben Ihnen "));
    correctSlices.add(new TestResponseSlice(true, "Geldbetrag"));
    correctSlices.add(new TestResponseSlice(false, " überwiesen. Auf Wiedersehen!"));

    List<ResponseSlice> testSlices = ResponseSlice.createSlices(componentText);

    for (int i = 0; i < correctSlices.size(); i++) {
      TestResponseSlice correctSlice = correctSlices.get(i);
      ResponseSlice testSlice = testSlices.get(i);

      assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
      assertEquals(correctSlice.getContent(), testSlice.getContent());
    }
  }

  @Test(expected = QuerimoniaException.class)
  public void testSetComponentTextMissingClosingBracket() {
    String componentText = "Guten Tag, Herr ${Name, wie geht es Ihnen?";
    ResponseSlice.createSlices(componentText);
  }

  @Test(expected = QuerimoniaException.class)
  public void testSetComponentTextMissingOpeningBracket() {
    String componentText = "Guten Tag, Herr $Name}, wie geht es Ihnen?";
    ResponseSlice.createSlices(componentText);
  }

  @Test(expected = QuerimoniaException.class)
  public void testSetComponentTextMissingDollarSign() {
    String componentText = "Guten Tag, Herr {Name}, wie geht es Ihnen?";
    ResponseSlice.createSlices(componentText);
  }

  @Test(expected = QuerimoniaException.class)
  public void testSetComponentTextOnlyPlaceholdersIllegal() {
    String componentText = "${So}${viele}${Placeholder}";
    ResponseSlice.createSlices(componentText);
  }

  @Test
  public void testSetComponentTextNoPlaceholders() {
    String componentText = "Guten Tag, wie geht es Ihnen?";
    List<ResponseSlice> testSliceList = ResponseSlice.createSlices(componentText);
    ResponseSlice testSlice = testSliceList.get(0);

    assertEquals(1, testSliceList.size());
    assertFalse(testSlice.isPlaceholder());
    assertEquals(componentText, testSlice.getContent());
  }

  @Test
  public void testSetComponentTextOnlyPlaceholdersLegal() {
    String componentText = "-${So}-${viele}-${Placeholder}-";

    List<TestResponseSlice> correctSlices = new ArrayList<>();
    correctSlices.add(new TestResponseSlice(false, "-"));
    correctSlices.add(new TestResponseSlice(true, "So"));
    correctSlices.add(new TestResponseSlice(false, "-"));
    correctSlices.add(new TestResponseSlice(true, "viele"));
    correctSlices.add(new TestResponseSlice(false, "-"));
    correctSlices.add(new TestResponseSlice(true, "Placeholder"));
    correctSlices.add(new TestResponseSlice(false, "-"));

    List<ResponseSlice> testSlices = ResponseSlice.createSlices(componentText);

    for (int i = 0; i < correctSlices.size(); i++) {
      TestResponseSlice correctSlice = correctSlices.get(i);
      ResponseSlice testSlice = testSlices.get(i);

      assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
      assertEquals(correctSlice.getContent(), testSlice.getContent());
    }
  }

  @Test
  public void testSetComponentTextEmptyPlaceholder() {
    String componentText = "Guten Tag, Herr ${}, wie geht es Ihnen?";

    List<TestResponseSlice> correctSlices = new ArrayList<>();
    correctSlices.add(new TestResponseSlice(false, "Guten Tag, Herr "));
    correctSlices.add(new TestResponseSlice(true, ""));
    correctSlices.add(new TestResponseSlice(false, ", wie geht es Ihnen?"));

    List<ResponseSlice> testSlices = ResponseSlice.createSlices(componentText);

    for (int i = 0; i < correctSlices.size(); i++) {
      TestResponseSlice correctSlice = correctSlices.get(i);
      ResponseSlice testSlice = testSlices.get(i);

      assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
      assertEquals(correctSlice.getContent(), testSlice.getContent());
    }
  }

  @Test
  public void testGetRequiredEntitiesStandard() {
    String componentText =
        "Guten Tag, Herr ${Name}. Wir haben Ihnen am ${Datum} einen Betrag von ${Geldbetrag} überwiesen.";
    ResponseComponent responseComponent =
        new ResponseComponentBuilder()
            .setComponentTexts(Collections.singletonList(componentText))
            .createResponseComponent();

    List<String> correctRequiredEntities = new ArrayList<>();
    correctRequiredEntities.add("Name");
    correctRequiredEntities.add("Datum");
    correctRequiredEntities.add("Geldbetrag");

    List<String> testRequiredEntities = responseComponent.getRequiredEntities();

    for (int i = 0; i < correctRequiredEntities.size(); i++) {
      assertEquals(correctRequiredEntities.get(i), testRequiredEntities.get(i));
    }
  }

  @Test
  public void testGetRequiredEntitiesDuplicatePlaceholder() {
    String componentText =
        "Guten Tag, Herr ${Name}. Wir haben Ihnen am ${Datum} einen Betrag von ${Geldbetrag} überwiesen. "
            +
            "Auf Wiedersehen, Herr ${Name}!";
    ResponseComponent responseComponent =
        new ResponseComponentBuilder().setComponentTexts(Collections.singletonList(componentText))
            .createResponseComponent();

    List<String> correctRequiredEntities = new ArrayList<>();
    correctRequiredEntities.add("Name");
    correctRequiredEntities.add("Datum");
    correctRequiredEntities.add("Geldbetrag");

    List<String> testRequiredEntities = responseComponent.getRequiredEntities();

    for (int i = 0; i < correctRequiredEntities.size(); i++) {
      assertEquals(correctRequiredEntities.get(i), testRequiredEntities.get(i));
    }
  }

  @Test
  public void testGetRequiredEntitiesNoPlaceholder() {
    String componentText = "Guten Tag!";
    ResponseComponent responseComponent =
        new ResponseComponentBuilder()
            .setComponentTexts(Collections.singletonList(componentText))
            .createResponseComponent();

    assertEquals(new ArrayList<>(), responseComponent.getRequiredEntities());
  }

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

    int hashCode1 = responseComponent1.hashCode();
    int hashCode2 = responseComponent2.hashCode();
    int hashCode3 = responseComponent3.hashCode();
    assertEquals(hashCode1, hashCode2);
    assertNotEquals(hashCode2,hashCode3);
  }

  /*@Test
  public void testToString() {
    String componentText = "test test 123";
    ResponseComponent responseComponent =
        new ResponseComponentBuilder()
            .setComponentName("Komponente")
            .setPriority(0)
            .setComponentTexts(Collections.singletonList(componentText))
            .setActions(Collections.emptyList())
            .setRulesXml("<Rules><EntityAvailable label=\"Name\"/></Rules>")
            .createResponseComponent();
    String subString0 = "de.fraunhofer.iao.querimonia.response.generation.ResponseComponent";
    String subString1 = "[\n" +
        "  componentId=0\n" +
        "  componentName=Komponente\n" +
        "  priority=0\n" +
        "  componentTexts=[test test 123]\n" +
        "  actions=[]\n" +
        "  rulesXml=<Rules><EntityAvailable label=\"Name\"/></Rules>\n" +
        "  rootRule=de.fraunhofer.iao.querimonia.response.rules.EntityRule";
    String subString2 = "\n" +
        "  componentSlices=[[de.fraunhofer.iao.querimonia.response.generation.ResponseSlice";
    String subString3 = "]]\n" +
        "]";
    String componentString = responseComponent.toString();
    assertTrue(componentString.contains(subString0));
    assertTrue(componentString.contains(subString1));
    assertTrue(componentString.contains(subString2));
    assertTrue(componentString.contains(subString3));
  }*/
}
