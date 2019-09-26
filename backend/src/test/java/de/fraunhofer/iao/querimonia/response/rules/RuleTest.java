package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.complaint.ComplaintBuilder;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponentBuilder;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.COMPLAINT_F;
import static org.junit.Assert.*;

/**
 * Unit test class for the different types of XML rules used in response components
 *
 * @author Simon Weiler
 */
public class RuleTest {

  @Test
  public void testTrueRule() {
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();

    assertTrue(Rule.TRUE.isPotentiallyRespected(complaint));
    assertTrue(Rule.TRUE.isRespected(complaint, completedResponseComponents));
  }

  @Test
  public void testAndRule() {
    MockRule rule1 = new MockRule(true, true);
    MockRule rule2 = new MockRule(false, false);
    List<Rule> rules1 = List.of(rule1, rule1);
    List<Rule> rules2 = List.of(rule1, rule2);
    List<Rule> rules3 = List.of(rule2, rule2);
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();
    AndRule andRule1 = new AndRule(rules1);
    AndRule andRule2 = new AndRule(rules2);
    AndRule andRule3 = new AndRule(rules3);

    assertTrue(andRule1.isPotentiallyRespected(complaint));
    assertTrue(andRule1.isRespected(complaint, completedResponseComponents));
    assertFalse(andRule2.isPotentiallyRespected(complaint));
    assertFalse(andRule2.isRespected(complaint, completedResponseComponents));
    assertFalse(andRule3.isPotentiallyRespected(complaint));
    assertFalse(andRule3.isRespected(complaint, completedResponseComponents));

    assertEquals(andRule1, andRule1);
    assertNotEquals(andRule1, null);
  }

  @Test
  public void testOrRule() {
    MockRule rule1 = new MockRule(true, true);
    MockRule rule2 = new MockRule(false, false);
    List<Rule> rules1 = List.of(rule1, rule1);
    List<Rule> rules2 = List.of(rule1, rule2);
    List<Rule> rules3 = List.of(rule2, rule2);
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();
    OrRule orRule1 = new OrRule(rules1);
    OrRule orRule2 = new OrRule(rules2);
    OrRule orRule3 = new OrRule(rules3);

    assertTrue(orRule1.isPotentiallyRespected(complaint));
    assertTrue(orRule1.isRespected(complaint, completedResponseComponents));
    assertTrue(orRule2.isPotentiallyRespected(complaint));
    assertTrue(orRule2.isRespected(complaint, completedResponseComponents));
    assertFalse(orRule3.isPotentiallyRespected(complaint));
    assertFalse(orRule3.isRespected(complaint, completedResponseComponents));

    assertEquals(orRule1, orRule1);
    assertNotEquals(orRule1, null);
  }

  @Test
  public void testNotRule() {
    MockRule rule1 = new MockRule(true, true);
    MockRule rule2 = new MockRule(false, false);
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();
    NotRule notRule1 = new NotRule(rule1);
    NotRule notRule2 = new NotRule(rule2);

    assertTrue(notRule1.isPotentiallyRespected(complaint));
    assertFalse(notRule1.isRespected(complaint, completedResponseComponents));
    assertTrue(notRule2.isPotentiallyRespected(complaint));
    assertTrue(notRule2.isRespected(complaint, completedResponseComponents));

    assertEquals(notRule1, notRule1);
    assertNotEquals(notRule1, null);
  }

  @Test
  public void testEntityRule() {
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();
    EntityRule entityRule1 = new EntityRule("Name", "Peter", 1, 1);
    EntityRule entityRule2 = new EntityRule("Eingangsdatum", "2019-06-20", 1, 1);
    EntityRule entityRule3 = new EntityRule("Eingangszeit", "12:00", 1, 1);
    EntityRule entityRule4 = new EntityRule("Datum", "heute", 1, 1);

    assertTrue(entityRule1.isRespected(complaint, completedResponseComponents));
    assertTrue(entityRule2.isRespected(complaint, completedResponseComponents));
    assertTrue(entityRule3.isRespected(complaint, completedResponseComponents));
    assertFalse(entityRule4.isRespected(complaint, completedResponseComponents));

    assertEquals(entityRule1, entityRule1);
    assertNotEquals(entityRule1, null);
  }

  @Test
  public void testPredecessorCountRule() {
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = new ArrayList<>();
    completedResponseComponents.add(new CompletedResponseComponent());
    completedResponseComponents.add(new CompletedResponseComponent());
    completedResponseComponents.add(new CompletedResponseComponent());
    PredecessorCountRule predecessorCountRule1 = new PredecessorCountRule(1, 5);
    PredecessorCountRule predecessorCountRule2 = new PredecessorCountRule(5, 1);

    assertTrue(predecessorCountRule1.isPotentiallyRespected(complaint));
    assertTrue(predecessorCountRule1.isRespected(complaint, completedResponseComponents));
    assertTrue(predecessorCountRule2.isPotentiallyRespected(complaint));
    assertFalse(predecessorCountRule2.isRespected(complaint, completedResponseComponents));

    assertEquals(predecessorCountRule1, predecessorCountRule1);
    assertNotEquals(predecessorCountRule1, null);
  }

  @Test
  public void testPredecessorRule() {
    ResponseComponent responseComponent1 = new ResponseComponentBuilder()
        .setComponentName("Komponente1")
        .createResponseComponent();
    ResponseComponent responseComponent2 = new ResponseComponentBuilder()
        .setComponentName("Komponente2")
        .createResponseComponent();
    ResponseComponent responseComponent3 = new ResponseComponentBuilder()
        .setComponentName("Komponente3")
        .createResponseComponent();

    CompletedResponseComponent completedResponseComponent1 =
        new CompletedResponseComponent(responseComponent1.toPersistableComponent(),
            Collections.emptyList());
    CompletedResponseComponent completedResponseComponent2 =
        new CompletedResponseComponent(responseComponent2.toPersistableComponent(),
            Collections.emptyList());
    CompletedResponseComponent completedResponseComponent3 =
        new CompletedResponseComponent(responseComponent3.toPersistableComponent(),
            Collections.emptyList());

    List<CompletedResponseComponent> completedResponseComponents1 = List.of(
        completedResponseComponent1, completedResponseComponent2, completedResponseComponent3);
    List<CompletedResponseComponent> completedResponseComponents2 = Collections.emptyList();
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);

    PredecessorRule predecessorRule1 = new PredecessorRule("Komponente2", "any");
    PredecessorRule predecessorRule2 = new PredecessorRule("Komponente3", "last");
    PredecessorRule predecessorRule3 = new PredecessorRule("Komponente1", "0");
    PredecessorRule predecessorRule4 = new PredecessorRule("Komponente1", "3");

    assertTrue(predecessorRule1.isPotentiallyRespected(complaint));
    assertTrue(predecessorRule1.isRespected(complaint, completedResponseComponents1));
    assertTrue(predecessorRule2.isPotentiallyRespected(complaint));
    assertTrue(predecessorRule2.isRespected(complaint, completedResponseComponents1));
    assertTrue(predecessorRule3.isPotentiallyRespected(complaint));
    assertTrue(predecessorRule3.isRespected(complaint, completedResponseComponents1));
    assertTrue(predecessorRule4.isPotentiallyRespected(complaint));
    assertFalse(predecessorRule4.isRespected(complaint, completedResponseComponents1));
    assertFalse(predecessorRule4.isRespected(complaint, completedResponseComponents2));

    assertEquals(predecessorRule1, predecessorRule1);
    assertNotEquals(predecessorRule1, null);
  }

  @Test
  public void testPropertyRule() {
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();
    PropertyRule propertyRule1 = new PropertyRule("Kategorie", "Beschwerde");
    PropertyRule propertyRule2 = new PropertyRule("Kategorie", "Sonstiges");

    assertTrue(propertyRule1.isPotentiallyRespected(complaint));
    assertTrue(propertyRule1.isRespected(complaint, completedResponseComponents));
    assertFalse(propertyRule2.isPotentiallyRespected(complaint));
    assertFalse(propertyRule2.isRespected(complaint, completedResponseComponents));

    assertEquals(propertyRule1, propertyRule1);
    assertNotEquals(propertyRule1, null);
  }

  @Test
  public void testSentimentRule() {
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();
    SentimentRule sentimentRule1 = new SentimentRule(-1, 1, "Wut");
    SentimentRule sentimentRule2 = new SentimentRule(-1, 1, "Trauer");

    assertTrue(sentimentRule1.isPotentiallyRespected(complaint));
    assertTrue(sentimentRule1.isRespected(complaint, completedResponseComponents));
    assertFalse(sentimentRule2.isPotentiallyRespected(complaint));
    assertFalse(sentimentRule2.isRespected(complaint, completedResponseComponents));

    assertEquals(sentimentRule1, sentimentRule1);
    assertNotEquals(sentimentRule1, null);
  }

  @Test
  public void testUploadDateRule() {
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();
    UploadDateRule uploadDateRule1 = new UploadDateRule(
        LocalDate.of(2019, 6, 1), null);
    UploadDateRule uploadDateRule2 = new UploadDateRule(
        null, LocalDate.of(2019, 6, 1));

    assertTrue(uploadDateRule1.isPotentiallyRespected(complaint));
    assertTrue(uploadDateRule1.isRespected(complaint, completedResponseComponents));
    assertFalse(uploadDateRule2.isPotentiallyRespected(complaint));
    assertFalse(uploadDateRule2.isRespected(complaint, completedResponseComponents));

    assertEquals(uploadDateRule1, uploadDateRule1);
    assertNotEquals(uploadDateRule1, null);
  }

  @Test
  public void testUploadTimeRule() {
    ComplaintBuilder complaint = new ComplaintBuilder(COMPLAINT_F);
    List<CompletedResponseComponent> completedResponseComponents = Collections.emptyList();
    UploadTimeRule uploadTimeRule1 = new UploadTimeRule(
        LocalTime.of(11, 30, 45), null);
    UploadTimeRule uploadTimeRule2 = new UploadTimeRule(
        null, LocalTime.of(11, 30, 45));

    assertTrue(uploadTimeRule1.isPotentiallyRespected(complaint));
    assertTrue(uploadTimeRule1.isRespected(complaint, completedResponseComponents));
    assertFalse(uploadTimeRule2.isPotentiallyRespected(complaint));
    assertFalse(uploadTimeRule2.isRespected(complaint, completedResponseComponents));

    assertEquals(uploadTimeRule1, uploadTimeRule1);
    assertNotEquals(uploadTimeRule1, null);
  }
}
