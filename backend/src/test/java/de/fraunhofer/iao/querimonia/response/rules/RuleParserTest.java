package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit test class for the RuleParser
 * @author Simon Weiler
 */
public class RuleParserTest {

  @Test
  public void testEmptyRule() {
    assertEquals(Rule.TRUE, RuleParser.parseRules(""));
  }

  @Test(expected = QuerimoniaException.class)
  public void testRandomString() {
    RuleParser.parseRules("hello how are you?");
  }

  @Test(expected = QuerimoniaException.class)
  public void testInvalidStartTag() {
    RuleParser.parseRules("<Regeln></Regeln>");
  }

  @Test(expected = QuerimoniaException.class)
  public void testInvalidXML() {
    RuleParser.parseRules("<Rules><Rules>");
  }

  @Test(expected = QuerimoniaException.class)
  public void testRuleMalformed() {
    RuleParser.parseRules("<Rules><SomeBS></SomeBS></Rules>");
  }

  @Test
  public void testRuleProperty() {
    PropertyRule correctRule = new PropertyRule("Kategorie", "Sonstiges");
    PropertyRule testRule = (PropertyRule) RuleParser.parseRules("<Rules>" +
        "<Property name=\"Kategorie\" matches=\"Sonstiges\" />" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRuleSentiment() {
    SentimentRule correctRule = new SentimentRule(0, 1, "Freude");
    SentimentRule testRule = (SentimentRule) RuleParser.parseRules("<Rules>" +
        "<Sentiment min=\"0\" max=\"1\" emotion=\"Freude\" />" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRuleUploadDate() {
    UploadDateRule correctRule = new UploadDateRule(LocalDate.of(2019, 8, 1),
        LocalDate.of(2019, 8, 31));
    UploadDateRule testRule = (UploadDateRule) RuleParser.parseRules("<Rules>" +
        "<UploadDate min=\"2019-08-01\" max=\"2019-08-31\" />" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRuleUploadTime() {
    UploadTimeRule correctRule = new UploadTimeRule(LocalTime.of(8, 0, 0),
        LocalTime.of(20,0,0));
    UploadTimeRule testRule = (UploadTimeRule) RuleParser.parseRules("<Rules>" +
        "<UploadTime min=\"08:00:00\" max=\"20:00:00\" />" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRuleEntityAvailable() {
    EntityRule correctRule = new EntityRule("Name", "Peter", 1, Integer.MAX_VALUE);
    EntityRule testRule = (EntityRule) RuleParser.parseRules("<Rules>" +
        "<EntityAvailable label=\"Name\" matches=\"Peter\" />" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRulePredecessor() {
    PredecessorRule correctRule = new PredecessorRule("Erstattung", "last");
    PredecessorRule testRule = (PredecessorRule) RuleParser.parseRules("<Rules>" +
        "<Predecessor matches=\"Erstattung\" position=\"last\" />" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRulePredecessorCount() {
    PredecessorCountRule correctRule = new PredecessorCountRule(1, 3);
    PredecessorCountRule testRule = (PredecessorCountRule) RuleParser.parseRules("<Rules>" +
        "<PredecessorCount min=\"1\" max=\"3\" />" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRuleNot() {
    NotRule correctRule = new NotRule(new EntityRule("Name", "Hans", 1,Integer.MAX_VALUE));
    NotRule testRule = (NotRule) RuleParser.parseRules("<Rules>" +
        "<Not>" +
        "<EntityAvailable label=\"Name\" matches=\"Hans\" />" +
        "</Not>" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRuleOr() {
    OrRule correctRule = new OrRule(List.of(new EntityRule("Name", "Hans", 1, Integer.MAX_VALUE),
        new EntityRule("Name", "Peter", 1, Integer.MAX_VALUE)));
    OrRule testRule = (OrRule) RuleParser.parseRules("<Rules>" +
        "<Or>" +
        "<EntityAvailable label=\"Name\" matches=\"Hans\" />" +
        "<EntityAvailable label=\"Name\" matches=\"Peter\" />" +
        "</Or>" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }

  @Test
  public void testRuleAnd() {
    AndRule correctRule = new AndRule(List.of(new EntityRule("Name", "Hans", 1, Integer.MAX_VALUE),
        new EntityRule("Name", "Peter", 1, Integer.MAX_VALUE)));
    AndRule testRule = (AndRule) RuleParser.parseRules("<Rules>" +
        "<And>" +
        "<EntityAvailable label=\"Name\" matches=\"Hans\" />" +
        "<EntityAvailable label=\"Name\" matches=\"Peter\" />" +
        "</And>" +
        "</Rules>");
    assertEquals(correctRule, testRule);
  }
}
