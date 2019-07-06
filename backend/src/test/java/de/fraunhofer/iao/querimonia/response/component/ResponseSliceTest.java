
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
    public void testSetTemplateTextStandard() {
        String templateText = "Guten Tag, Herr ${Name}. Wir haben Ihnen ${Geldbetrag} überwiesen. Auf Wiedersehen!";

        ArrayList<TestResponseSlice> correctSlices = new ArrayList<>();
        correctSlices.add(new TestResponseSlice(false, "Guten Tag, Herr "));
        correctSlices.add(new TestResponseSlice(true, "Name"));
        correctSlices.add(new TestResponseSlice(false, ". Wir haben Ihnen "));
        correctSlices.add(new TestResponseSlice(true, "Geldbetrag"));
        correctSlices.add(new TestResponseSlice(false, " überwiesen. Auf Wiedersehen!"));

        List<ResponseSlice> testSlices = ResponseSlice.createSlices(templateText);

        for (int i = 0; i < correctSlices.size(); i++) {
            TestResponseSlice correctSlice = correctSlices.get(i);
            ResponseSlice testSlice = testSlices.get(i);

            assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
            assertEquals(correctSlice.getContent(), testSlice.getContent());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTemplateTextMissingClosingBracket() {
        String templateText = "Guten Tag, Herr ${Name, wie geht es Ihnen?";
        ResponseSlice.createSlices(templateText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTemplateTextMissingOpeningBracket() {
        String templateText = "Guten Tag, Herr $Name}, wie geht es Ihnen?";
        ResponseSlice.createSlices(templateText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTemplateTextMissingDollarSign() {
        String templateText = "Guten Tag, Herr {Name}, wie geht es Ihnen?";
        ResponseSlice.createSlices(templateText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTemplateTextOnlyPlaceholdersIllegal() {
        String templateText = "${So}${viele}${Placeholder}";
        ResponseSlice.createSlices(templateText);
    }

    @Test
    public void testSetTemplateTextNoPlaceholders() {
        String templateText = "Guten Tag, wie geht es Ihnen?";
        List<ResponseSlice> testSliceList = ResponseSlice.createSlices(templateText);
        ResponseSlice testSlice = testSliceList.get(0);

        assertEquals(1, testSliceList.size());
        assertFalse(testSlice.isPlaceholder());
        assertEquals(templateText, testSlice.getContent());
    }

    @Test
    public void testSetTemplateTextOnlyPlaceholdersLegal() {
        String templateText = "-${So}-${viele}-${Placeholder}-";

        List<TestResponseSlice> correctSlices = new ArrayList<>();
        correctSlices.add(new TestResponseSlice(false, "-"));
        correctSlices.add(new TestResponseSlice(true, "So"));
        correctSlices.add(new TestResponseSlice(false, "-"));
        correctSlices.add(new TestResponseSlice(true, "viele"));
        correctSlices.add(new TestResponseSlice(false, "-"));
        correctSlices.add(new TestResponseSlice(true, "Placeholder"));
        correctSlices.add(new TestResponseSlice(false, "-"));

        List<ResponseSlice> testSlices = ResponseSlice.createSlices(templateText);

        for (int i = 0; i < correctSlices.size(); i++) {
            TestResponseSlice correctSlice = correctSlices.get(i);
            ResponseSlice testSlice = testSlices.get(i);

            assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
            assertEquals(correctSlice.getContent(), testSlice.getContent());
        }
    }

    @Test
    public void testSetTemplateTextEmptyPlaceholder() {
        String templateText = "Guten Tag, Herr ${}, wie geht es Ihnen?";

        List<TestResponseSlice> correctSlices = new ArrayList<>();
        correctSlices.add(new TestResponseSlice(false, "Guten Tag, Herr "));
        correctSlices.add(new TestResponseSlice(true, ""));
        correctSlices.add(new TestResponseSlice(false, ", wie geht es Ihnen?"));

        List<ResponseSlice> testSlices = ResponseSlice.createSlices(templateText);

        for (int i = 0; i < correctSlices.size(); i++) {
            TestResponseSlice correctSlice = correctSlices.get(i);
            ResponseSlice testSlice = testSlices.get(i);

            assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
            assertEquals(correctSlice.getContent(), testSlice.getContent());
        }
    }

    @Test
    public void testGetRequiredEntitiesStandard() {
        String templateText = "Guten Tag, Herr ${Name}. Wir haben Ihnen am ${Datum} einen Betrag von ${Geldbetrag} überwiesen.";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateTexts(Collections.singletonList(templateText));

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
        String templateText = "Guten Tag, Herr ${Name}. Wir haben Ihnen am ${Datum} einen Betrag von ${Geldbetrag} überwiesen. " +
                "Auf Wiedersehen, Herr ${Name}!";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateTexts(Collections.singletonList(templateText));

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
        String templateText = "Guten Tag!";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateTexts(Collections.singletonList(templateText));

        assertEquals(new ArrayList<>(), responseComponent.getRequiredEntities());
    }
}
