
package de.fraunhofer.iao.querimonia.response.component;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Unit test class for ResponseComponent
 */

public class ResponseSliceTest {

    private class TestResponseSlice {

        private boolean isPlaceholder;
        private String content;

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
        String componentText = "Guten Tag, Herr ${Name}. Wir haben Ihnen ${Geldbetrag} überwiesen. Auf Wiedersehen!";

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

    @Test(expected = IllegalArgumentException.class)
    public void testSetComponentTextMissingClosingBracket() {
        String componentText = "Guten Tag, Herr ${Name, wie geht es Ihnen?";
        ResponseSlice.createSlices(componentText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetComponentTextMissingOpeningBracket() {
        String componentText = "Guten Tag, Herr $Name}, wie geht es Ihnen?";
        ResponseSlice.createSlices(componentText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetComponentTextMissingDollarSign() {
        String componentText = "Guten Tag, Herr {Name}, wie geht es Ihnen?";
        ResponseSlice.createSlices(componentText);
    }

    @Test(expected = IllegalArgumentException.class)
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
        String componentText = "Guten Tag, Herr ${Name}. Wir haben Ihnen am ${Datum} einen Betrag von ${Geldbetrag} überwiesen.";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setComponentTexts(Collections.singletonList(componentText));

        List<String> correctRequiredEntities = new ArrayList<>();
        correctRequiredEntities.add("Name");
        correctRequiredEntities.add("Datum");
        correctRequiredEntities.add("Geldbetrag");

        List<String> testRequiredEntities = responseComponent.getRequiredEntities();

        for(int i = 0; i < correctRequiredEntities.size(); i++) {
            assertEquals(correctRequiredEntities.get(i), testRequiredEntities.get(i));
        }
    }

    @Test
    public void testGetRequiredEntitiesDuplicatePlaceholder() {
        String componentText = "Guten Tag, Herr ${Name}. Wir haben Ihnen am ${Datum} einen Betrag von ${Geldbetrag} überwiesen. " +
                "Auf Wiedersehen, Herr ${Name}!";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setComponentTexts(Collections.singletonList(componentText));

        List<String> correctRequiredEntities = new ArrayList<>();
        correctRequiredEntities.add("Name");
        correctRequiredEntities.add("Datum");
        correctRequiredEntities.add("Geldbetrag");

        List<String> testRequiredEntities = responseComponent.getRequiredEntities();

        for(int i = 0; i < correctRequiredEntities.size(); i++) {
            assertEquals(correctRequiredEntities.get(i), testRequiredEntities.get(i));
        }
    }

    @Test
    public void testGetRequiredEntitiesNoPlaceholder() {
        String componentText = "Guten Tag!";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setComponentTexts(Collections.singletonList(componentText));

        assertEquals(new ArrayList<>(), responseComponent.getRequiredEntities());
    }
}
