package de.fraunhofer.iao.querimonia.nlp.analyze;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit test class for TokenAnalyzer
 *
 * @author Simon Weiler
 */
public class TokenAnalyzerTest {

    private static TokenAnalyzer testTokenAnalyzer;

    @BeforeClass
    public static void initialize() {
        testTokenAnalyzer = new TokenAnalyzer();
    }

    @Test
    public void testFilterStopWordsStandard() {
        String testString = "JUnit ist ein Framework zum Testen von Java-Programmen, das besonders für das " +
                "automatisierte Testen einzelner Units (Klassen oder Methoden) geeignet ist.";

        Map<String, Integer> correctFilterResult = new HashMap<>();
        correctFilterResult.put("junit", 1);
        correctFilterResult.put("framework", 1);
        correctFilterResult.put("testen", 2);
        correctFilterResult.put("java-programmen", 1);
        correctFilterResult.put("besonders", 1);
        correctFilterResult.put("automatisiert", 1);
        correctFilterResult.put("einzeln", 1);
        correctFilterResult.put("units", 1);
        correctFilterResult.put("klasse", 1);
        correctFilterResult.put("methode", 1);
        correctFilterResult.put("geeignet", 1);

        assertEquals(correctFilterResult, testTokenAnalyzer.filterStopWords(testString));
    }

    @Test
    public void testFilterStopWordsEmptyString() {
        String testString = "";

        Map<String, Integer> correctFilterResult = new HashMap<>();

        assertEquals(correctFilterResult, testTokenAnalyzer.filterStopWords(testString));
    }

    @Test
    public void testFilterStopWordsPunctuations() {
        String testString = ". , : ; ! hehl";

        Map<String, Integer> correctFilterResult = new HashMap<>();

        assertEquals(correctFilterResult, testTokenAnalyzer.filterStopWords(testString));
    }

    @Test
    public void testFilterStopWordsBrackets() {
        String testString = "( ) [ ] { }";

        Map<String, Integer> correctFilterResult = new HashMap<>();

        assertEquals(correctFilterResult, testTokenAnalyzer.filterStopWords(testString));
    }

    @Test
    public void testFilterStopWordsResultSorting() {
        String testString = "Hi hi hi hallo hallo Welt!";

        Map<String, Integer> correctFilterResult = new HashMap<>();
        correctFilterResult.put("welt", 1);
        correctFilterResult.put("hallo", 2);
        correctFilterResult.put("hi", 3);

        assertEquals(correctFilterResult, testTokenAnalyzer.filterStopWords(testString));
    }

    @Test
    public void testFilterStopWordsNumbers() {
        String testString = "123 4 5 6 7,5 8.25 9,0 123 4 4 10.00";

        Map<String, Integer> correctFilterResult = new HashMap<>();
        correctFilterResult.put("123", 2);
        correctFilterResult.put("4", 3);
        correctFilterResult.put("5", 1);
        correctFilterResult.put("6", 1);
        correctFilterResult.put("7,5", 1);
        correctFilterResult.put("8.25", 1);
        correctFilterResult.put("9,0", 1);
        correctFilterResult.put("10.00", 1);

        assertEquals(correctFilterResult, testTokenAnalyzer.filterStopWords(testString));
    }
}
