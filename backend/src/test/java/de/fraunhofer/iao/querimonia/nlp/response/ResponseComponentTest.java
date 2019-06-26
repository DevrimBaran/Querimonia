/*
package de.fraunhofer.iao.querimonia.nlp.response;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

*/
/**
 * Unit test class for ResponseComponent
 *//*

public class ResponseComponentTest {

    @Test
    public void testSetTemplateTextStandard() {
        String templateText = "Guten Tag, Herr ${Name}. Wir haben Ihnen ${Geldbetrag} überwiesen. Auf Wiedersehen!";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);

        List<ResponseComponent.ResponseSlice> correctSlices = new ArrayList<>();
        correctSlices.add(new ResponseComponent.ResponseSlice(false, "Guten Tag, Herr "));
        correctSlices.add(new ResponseComponent.ResponseSlice(true, "Name"));
        correctSlices.add(new ResponseComponent.ResponseSlice(false, ". Wir haben Ihnen "));
        correctSlices.add(new ResponseComponent.ResponseSlice(true, "Geldbetrag"));
        correctSlices.add(new ResponseComponent.ResponseSlice(false, " überwiesen. Auf Wiedersehen!"));

        List<ResponseComponent.ResponseSlice> testSlices = responseComponent.getSlices();

        for (int i = 0; i < correctSlices.size(); i++) {
            ResponseComponent.ResponseSlice correctSlice = correctSlices.get(i);
            ResponseComponent.ResponseSlice testSlice = testSlices.get(i);

            assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
            assertEquals(correctSlice.getContent(), testSlice.getContent());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTemplateTextMissingClosingBracket() {
        String templateText = "Guten Tag, Herr ${Name, wie geht es Ihnen?";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTemplateTextMissingOpeningBracket() {
        String templateText = "Guten Tag, Herr $Name}, wie geht es Ihnen?";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTemplateTextMissingDollarSign() {
        String templateText = "Guten Tag, Herr {Name}, wie geht es Ihnen?";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);
    }

    @Test
    public void testSetTemplateTextNoPlaceholders() {
        String templateText = "Guten Tag, wie geht es Ihnen?";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);
        ResponseComponent.ResponseSlice testSlice = responseComponent.getSlices().get(0);

        assertEquals(1, responseComponent.getSlices().size());
        assertFalse(testSlice.isPlaceholder());
        assertEquals(templateText, testSlice.getContent());
    }

    @Test
    public void testSetTemplateTextOnlyPlaceholdersLegal() {
        String templateText = "-${So}-${viele}-${Placeholder}-";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);

        List<ResponseComponent.ResponseSlice> correctSlices = new ArrayList<>();
        correctSlices.add(new ResponseComponent.ResponseSlice(false, "-"));
        correctSlices.add(new ResponseComponent.ResponseSlice(true, "So"));
        correctSlices.add(new ResponseComponent.ResponseSlice(false, "-"));
        correctSlices.add(new ResponseComponent.ResponseSlice(true, "viele"));
        correctSlices.add(new ResponseComponent.ResponseSlice(false, "-"));
        correctSlices.add(new ResponseComponent.ResponseSlice(true, "Placeholder"));
        correctSlices.add(new ResponseComponent.ResponseSlice(false, "-"));

        List<ResponseComponent.ResponseSlice> testSlices = responseComponent.getSlices();

        for (int i = 0; i < correctSlices.size(); i++) {
            ResponseComponent.ResponseSlice correctSlice = correctSlices.get(i);
            ResponseComponent.ResponseSlice testSlice = testSlices.get(i);

            assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
            assertEquals(correctSlice.getContent(), testSlice.getContent());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTemplateTextOnlyPlaceholdersIllegal() {
        String templateText = "${So}${viele}${Placeholder}";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);
    }

    @Test
    public void testSetTemplateTextEmptyPlaceholder() {
        String templateText = "Guten Tag, Herr ${}, wie geht es Ihnen?";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);

        List<ResponseComponent.ResponseSlice> correctSlices = new ArrayList<>();
        correctSlices.add(new ResponseComponent.ResponseSlice(false, "Guten Tag, Herr "));
        correctSlices.add(new ResponseComponent.ResponseSlice(true, ""));
        correctSlices.add(new ResponseComponent.ResponseSlice(false, ", wie geht es Ihnen?"));

        List<ResponseComponent.ResponseSlice> testSlices = responseComponent.getSlices();

        for (int i = 0; i < correctSlices.size(); i++) {
            ResponseComponent.ResponseSlice correctSlice = correctSlices.get(i);
            ResponseComponent.ResponseSlice testSlice = testSlices.get(i);

            assertEquals(correctSlice.isPlaceholder(), testSlice.isPlaceholder());
            assertEquals(correctSlice.getContent(), testSlice.getContent());
        }
    }

    @Test
    public void testGetRequiredEntitiesStandard() {
        String templateText = "Guten Tag, Herr ${Name}. Wir haben Ihnen am ${Datum} einen Betrag von ${Geldbetrag} überwiesen.";
        ResponseComponent responseComponent = new ResponseComponent();
        responseComponent.setTemplateText(templateText);

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
        responseComponent.setTemplateText(templateText);

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
        responseComponent.setTemplateText(templateText);

        assertEquals(new ArrayList<>(), responseComponent.getRequiredEntities());
    }
}
*/
