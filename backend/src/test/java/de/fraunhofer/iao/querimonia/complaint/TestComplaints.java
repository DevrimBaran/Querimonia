package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.config.TestConfigurations;
import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import de.fraunhofer.iao.querimonia.nlp.NamedEntityBuilder;
import de.fraunhofer.iao.querimonia.nlp.Sentiment;
import de.fraunhofer.iao.querimonia.nlp.TestEntities;
import de.fraunhofer.iao.querimonia.response.component.TestComponents;
import de.fraunhofer.iao.querimonia.response.generation.CompletedResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseSuggestion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.TestDates.*;
import static de.fraunhofer.iao.querimonia.complaint.TestComplaints.TestTexts.*;

public class TestComplaints {

  public static final Complaint COMPLAINT_A = new ComplaintBuilder(TEXT_A)
      .setId(1)
      .setPreview(PREVIEW_A)
      .setReceiveDate(DATE_A)
      .setReceiveTime(TIME_A)
      .setConfiguration(TestConfigurations.CONFIGURATION_A)
      .setState(ComplaintState.NEW)
      .setProperties(TestProperties.PROPERTIES_A)
      .setResponseSuggestion(TestResponses.SUGGESTION_A)
      .setSentiment(new Sentiment(new ComplaintProperty("Emotion", "Unbekannt"), 0.0))
      .createComplaint();

  public static final Complaint COMPLAINT_B = new ComplaintBuilder(TEXT_B)
      .setId(2)
      .setPreview(PREVIEW_B)
      .setReceiveDate(DATE_B)
      .setReceiveTime(TIME_B)
      .setConfiguration(TestConfigurations.CONFIGURATION_A)
      .setState(ComplaintState.NEW)
      .setProperties(TestProperties.PROPERTIES_A)
      .setResponseSuggestion(TestResponses.SUGGESTION_B)
      .setEntities(List.of(TestEntities.ENTITY_A, TestEntities.ENTITY_B))
      .setSentiment(new Sentiment(new ComplaintProperty("Emotion", "Unbekannt"), 0.1))
      .createComplaint();

  public static final Complaint COMPLAINT_C = new ComplaintBuilder(TEXT_C)
      .setId(3)
      .setPreview(PREVIEW_C)
      .setReceiveDate(DATE_C)
      .setReceiveTime(TIME_C)
      .setConfiguration(TestConfigurations.CONFIGURATION_B)
      .setState(ComplaintState.IN_PROGRESS)
      .setProperties(TestProperties.PROPERTIES_B)
      .setResponseSuggestion(TestResponses.SUGGESTION_A)
      .setSentiment(new Sentiment(new ComplaintProperty("Emotion", "Unbekannt"), -0.5))
      .createComplaint();

  public static final Complaint COMPLAINT_D = new ComplaintBuilder(TEXT_D)
      .setId(4)
      .setPreview(PREVIEW_D)
      .setReceiveDate(DATE_D)
      .setReceiveTime(TIME_D)
      .setConfiguration(TestConfigurations.CONFIGURATION_B)
      .setState(ComplaintState.CLOSED)
      .setProperties(TestProperties.PROPERTIES_B)
      .setResponseSuggestion(TestResponses.SUGGESTION_A)
      .setSentiment(new Sentiment(new ComplaintProperty("Emotion", "Unbekannt"), 0.0))
      .createComplaint();

  public static final Complaint COMPLAINT_E = new ComplaintBuilder(TEXT_E)
      .setId(5)
      .setPreview(PREVIEW_E)
      .setReceiveDate(DATE_E)
      .setReceiveTime(TIME_E)
      .setConfiguration(TestConfigurations.CONFIGURATION_B)
      .setProperties(TestProperties.PROPERTIES_B)
      .setResponseSuggestion(TestResponses.SUGGESTION_B)
      .setSentiment(new Sentiment(new ComplaintProperty("Emotion", "Unbekannt"), -0.1))
      .createComplaint();

  public static final Complaint COMPLAINT_F = new ComplaintBuilder(TEXT_F)
      .setId(6)
      .setPreview(PREVIEW_F)
      .setReceiveDate(DATE_F)
      .setReceiveTime(TIME_F)
      .setConfiguration(TestConfigurations.CONFIGURATION_D)
      .setProperties(TestProperties.PROPERTIES_F)
      .setState(ComplaintState.NEW)
      .setResponseSuggestion(TestResponses.SUGGESTION_C)
      .setSentiment(new Sentiment(new ComplaintProperty("Emotion", "Wut"), 0.0))
      .setEntities(ENTITIES_F)
      .createComplaint();

  public static final Complaint COMPLAINT_G = new ComplaintBuilder(TEXT_G)
      .setId(7)
      .setPreview(PREVIEW_G)
      .setReceiveDate(DATE_G)
      .setReceiveTime(TIME_G)
      .setConfiguration(TestConfigurations.CONFIGURATION_A)
      .setState(ComplaintState.IN_PROGRESS)
      .setProperties(TestProperties.PROPERTIES_G)
      .setResponseSuggestion(TestResponses.SUGGESTION_B)
      .setSentiment(new Sentiment(new ComplaintProperty("Emotion", "Wut"), 0.0))
      .createComplaint();

  public static final Complaint COMPLAINT_H = new ComplaintBuilder(TEXT_H)
      .setId(8)
      .setPreview(PREVIEW_H)
      .setReceiveDate(DATE_H)
      .setReceiveTime(TIME_H)
      .setConfiguration(TestConfigurations.CONFIGURATION_A)
      .setState(ComplaintState.IN_PROGRESS)
      .setProperties(TestProperties.PROPERTIES_H)
      .setResponseSuggestion(TestResponses.SUGGESTION_B)
      .setSentiment(new Sentiment(new ComplaintProperty("Emotion", "Trauer"), 0.0))
      .createComplaint();

  public static final Complaint COMPLAINT_I = new ComplaintBuilder(COMPLAINT_H)
      .setEntities(ENTITIES_I)
      .createComplaint();

  @SuppressWarnings("WeakerAccess")
  public static class TestProperties {

    public static final Map<String, Double> baseMap = Collections.singletonMap("Unbekannt", 1.0);

    public static final List<ComplaintProperty> PROPERTIES_A
        = List.of(
        new ComplaintProperty("Kategorie", baseMap)
    );

    public static final List<ComplaintProperty> PROPERTIES_B
        = List.of(
        new ComplaintProperty("Kategorie", "Fahrer unfreundlich"),
        new ComplaintProperty("Domäne", "Wuppertal")
    );

    public static final List<ComplaintProperty> PROPERTIES_C
        = List.of(
        new ComplaintProperty("Kategorie", "Sonstiges")
    );

    public static final List<ComplaintProperty> PROPERTIES_F
        = List.of(
        new ComplaintProperty("Kategorie", "Beschwerde")
    );

    public static final List<ComplaintProperty> PROPERTIES_G
        = List.of(
        new ComplaintProperty("Kategorie", "Test")
    );

    public static final List<ComplaintProperty> PROPERTIES_H
        = List.of(
        new ComplaintProperty("Kategorie", "Beschwerde")
    );
  }

  @SuppressWarnings("WeakerAccess")
  public static class TestResponses {

    public static final ResponseSuggestion SUGGESTION_A = new ResponseSuggestion(
        List.of(), ""
    );

    public static final ResponseSuggestion SUGGESTION_B = new ResponseSuggestion(
        List.of(new CompletedResponseComponent(
                TestComponents.COMPONENT_A.toPersistableComponent(),
                List.of(TestEntities.ENTITY_A)
            ), new CompletedResponseComponent(
                TestComponents.COMPONENT_B.toPersistableComponent(),
                Collections.emptyList()
            )
        ),
        ""
    );

    public static final ResponseSuggestion SUGGESTION_C = new ResponseSuggestion(
        List.of(new CompletedResponseComponent(
                TestComponents.COMPONENT_C.toPersistableComponent(),
                ENTITIES_F
            ), new CompletedResponseComponent(
                TestComponents.COMPONENT_D.toPersistableComponent(),
                Collections.emptyList()
            )
        ),
        ""
    );
  }

  @SuppressWarnings("WeakerAccess")
  public static class TestDates {
    public static final LocalDate DATE_A = LocalDate.of(2010, 1, 1);

    public static final LocalTime TIME_A = LocalTime.of(12, 0, 0);

    public static final LocalDate DATE_B = LocalDate.of(2010, 1, 2);

    public static final LocalTime TIME_B = LocalTime.of(12, 0, 0);

    public static final LocalDate DATE_C = LocalDate.of(2010, 1, 1);

    public static final LocalTime TIME_C = LocalTime.of(13, 0, 0);

    public static final LocalDate DATE_D = LocalDate.of(2010, 1, 2);

    public static final LocalTime TIME_D = LocalTime.of(12, 0, 0);

    public static final LocalDate DATE_E = LocalDate.of(2009, 10, 1);

    public static final LocalTime TIME_E = LocalTime.of(12, 0);

    public static final LocalDate DATE_F = LocalDate.of(2019, 6, 20);

    public static final LocalTime TIME_F = LocalTime.of(12, 0);

    public static final LocalDate DATE_G = LocalDate.of(2019, 7, 29);

    public static final LocalTime TIME_G = LocalTime.now();

    public static final LocalDate DATE_H = LocalDate.of(2019, 8, 1);

    public static final LocalTime TIME_H = LocalTime.now();
  }

  public static class TestTexts {

    public static final String TEXT_A = "In Ihrem Schreiben vom 8. 2. 2013 schreiben Sie, nur "
        + "\"ein\" Bus der Linie CE62 von Wuppertal-Elberfeld nach Wuppertal-Ronsdorf sei "
        + "ausgefallen. Dies kann so nicht stimmen. Vielmehr stellt sich mir und anderen die "
        + "Frage, weshalb ständig und gehäuft Buslinien der WSW ausfallen und Fahrten entgegen dem"
        + " Fahrplan nicht angeboten werden. Als Rad-, Bahn- und gelegentlicher Autofahrer benutze"
        + " ich eher selten die Busse der WSW. Wenn ich sie benutze, ergeben sich auffällig häufig"
        + " Ausfälle und deutliche Verspätungen. Dies betrifft auffallend oft die als schnell"
        + " konzipierten CE-Linien, nach meiner jüngeren Erfahrung CE61/CE62, "
        + "weniger auch CE64/CE65 (Elberfeld-Cronenberg), und Nebenlinien wie etwa 643. Einzelne "
        + "Ihrer Mitarbeiter berichten mir übereinstimmend, die Personaldecke sei zu dünn, um "
        + "Ausfälle einzelner Mitarbeiter auszugleichen. Wegen spürbaren Personalmangels - nicht, "
        + "wie Sie schreiben, vereinzelt wegen Krankheit - würden häufig Fahrten ausfallen. Viele "
        + "Fahrer berichten anschaulich, dass in manchen Stoßzeiten vergeblich wartende Passagiere "
        + "an Haltestellen stünden. Teilweise berichtet man mir von gezielten Maßnahmen, um "
        + "Einsparungen herbeizuführen sowie nicht ausgeglichenen Überstunden. Ihre Fahrer sehen "
        + "dies unmittelbar mit der Politik der WSW verknüpft, die CE-Linien ab März/April 2013 "
        + "auszudünnen.Der Vorgang, auf den sich Ihr Schreiben bezieht, lief tatsächlich so ab: "
        + "Am 1. 2. 2013 wartete ich an der Morianstraße in Wuppertal mit meinem x Monate alten "
        + "Sohn nachmittags auf eine Busverbindung nach Ronsdorf, Haltestelle Kniprodestraße. Ich "
        + "beabsichtigte, den Bus CE62 um 15:47 Uhr zu nehmen. Wir waren auf dem Weg zu einer "
        + "Nachmittagsveranstaltung für Eltern mit Kleinstkindern, die ich verpasste:Die CE62 um "
        + "15:47 Uhr fuhr nicht, ebenso nicht die nachfolgenden Busse der Linie CE62:- 16:07 Uhr- "
        + "16:27 UhrEs fuhren ferner nicht die Busse der Linie 620 um- 15:55 Uhr- 16:15 Uhr- 16:35 "
        + "Uhrdie Haltestelle an. Ihr Schreiben legt nahe, dass es sich nur um vereinzelte Fälle "
        + "handele. Unmittelbar bei meinem nächsten Fahrtantritt mit der WSW eine Woche später kam "
        + "es wiederum zu einem Ausfall. Am Freitag, 8. 2. 2013, fiel Linie CE61 um 15:38 Uhr an "
        + "der Haltestelle Am Stadtbahnhof in Richtung Barmen aus. Mit ca. 25 Minuten Verspätung "
        + "erreichte ich mit der Linie 640 (ab 15:48 Uhr) gegen 16:25 Uhr den Alten Markt in "
        + "Barmen.Heute, am 21. 2. 2013, fielen die Busse der Linie CE64 von Wuppertal Hbf in "
        + "Richtung Cronenberg um 06:40 Uhr und um 07:00 Uhr aus. Ein Fahrgast erreichte deshalb "
        + "nicht pünktlich seine Arbeitsstelle in Solingen. Würde ich häufiger fahren, würde ich "
        + "vermutlich noch häufiger dadurch Zeit verlieren. Ich bitte Sie daher freundlich, Ihre "
        + "Dienstleistungen bestmöglich fahrplangemäß anzubieten.";

    public static final String PREVIEW_A = TEXT_A.substring(0, 250);

    public static final String TEXT_B = ""
        + "Sehr geehrte Damen und Herren, am heutigen Tag wollte ich die Linie 647 um 8.20 Uhr von "
        + "Hattingen in Richtung Wuppertal nutzen. Als ich um 8.15 Uhr am Busbahnhof ankam, "
        + "informierten mich weitere Fahrgäste, dass der Bus um 7.40 Uhr bereits ausgefallen war. "
        + "Um 8.25 Uhr erkundigte ich mich im Kundencenter der Bogestra, warum die 647 um 8.20 Uhr "
        + "auch nicht kam. Dort telefonierte die Dame mit Ihrer Gesellschaft und erhielt die "
        + "Auskunft, dass es krankheitsbedingt Ausfälle gäbe, aber der Bus um 8.40 Uhr planmäßig "
        + "fahren würde. Also wartete ich geduldig auf diesen Bus, der dann auch wieder nicht kam. "
        + "Wiederum bekam ich nach einer erneuten Rückfrage bei Ihrer Gesellschaft die Auskunft, "
        + "dass alle Busse planmäßig fahren würden. Da ich auf dem Weg zur Arbeit war, stieg ich "
        + "auf die S-Bahn um und fuhr über Steele nach Neviges und von dort nach Velbert.. Ich kam "
        + "dort mit einer Stunde Verspätung an.  Auf dem Weg dorthin musste ich in Langenberg "
        + "feststellen, dass die 647 tatsächlich unterwegs war, aber offensichtlich nicht  den Weg "
        + "bis nach Hattingen gefunden hatte. Ich kann es nur als Unverschämtheit bezeichnen, dass "
        + "man als Fahrgast keine ehrliche Auskunft erhält. Hätte ich direkt gewusst, dass der Bus "
        + "Hattingen nicht anfährt, dann hätte sich meine Verspätung zumindest verkürzen können. "
        + "So habe ich umsonst in der Kälte ausgeharrt und eine Stunde Verspätung muss jetzt "
        + "wieder aufgearbeitet werden. Da ich mein Ticket bei der Bogestra erworben habe, konnte "
        + "ich leider kein Taxi in Anspruch nehmen. Leider ist dies kein Einzelfall. Für die mir "
        + "entstandenen Unannehmlichkeiten erwarte ich von Ihnen zumindest eine  Entschädigung in "
        + "Form eines  4-Fahrtentickets.Freundliche Grüße Meike Gepperth";

    public static final String PREVIEW_B = TEXT_B.substring(0, 250);

    public static final String TEXT_C = "leider ist es in den letzten Tagen vermehrt dazu "
        + "gekommen das die Bus Linie 649 Velbert – Wuppertal ausgefallen ist. Dieses ist kein "
        + "zustand. Es kann nicht sein das die Kunden nicht Informiert werden und die Busse "
        + "gestrichen werden ohne nennenswerten Grund.Nicht mal Taxis werden morgens bereit "
        + "gestellt. Es war Mittwochs so das 2 Busse am Späten Nachmittag in Richtung Velbert "
        + "ausgefallen waren, wo Ihre Leitstelle zudem noch sagte, das alle Busse PLANMÄSIG "
        + "unterwegs sind, was da nicht der Fall war (hierzu sollten bei Ihnen weitere Beschwerde"
        + " E-Mails angekommen sein). Des Weiteren muss ich noch dazu sagen, dass Ihr Fahrer sich"
        + " an diesem Tage sehr im Ton vergriffen hat und den Fahrgästen gegenüber sehr "
        + "ausfallend war!Donnerstag und auch Heute (Freitag) sind beide Busse Velbert -> "
        + "Wuppertal um 6:33 an der Haltestelle Tönisheide Mitte ausgefallen, so dass ich mir "
        + "heute ein Taxi nehmen musste, welches ich mir mit einem anderem Fahrgast geteilt habe,"
        + " dabei entstanden mir zusätzliche Kosten in Höhe von 5 Euro.Wie geht es nun weiter? "
        + "Muss ich mir für Montag morgens gleich ein Taxi bestellen oder schafft Ihr es endlich "
        + "mal alle Busse fahren zu lassen oder stellt Ihr gleich ein bereits bezahltes Taxi an "
        + "die Haltestelle?Ich weiß ja nicht wo das Problem liegt, ob es defekte Busse sind oder "
        + "durch Krankheit ausgefallene Fahrer.Aber wenn es Busse sind, wie hat die Vestische das"
        + " hin bekommen mit einem komplett Zerstörten Fuhrpark (Bottrop)? Es gibt genügend "
        + "andere Bus Unternehmen in Ihrer Nähe mit denen man garantiert reden kann und Hilfe "
        + "bekommt. Sollten Ihnen Fahrer fehlen, dazu gibt es heutzutage doch ausreichend "
        + "Zeitarbeitsfirmen die Ihnen Personal zur Verfügung stellen können. Ich hoffe das sie "
        + "dieses nun endlich in den Griff bekommen und mir die 5 Euro Taxi Kosten erstattet "
        + "werden.";

    public static final String PREVIEW_C = TEXT_C.substring(0, 250);

    public static final String TEXT_D = "am 26.02.2013 kam ich um ca. 20.30 Uhr nach "
        + "erfolgreicher Anreise mit dem Bus aus Bochum an der Haltestelle Hattingen Mitte an, um"
        + " dort mit einem Anschlussbus Linie 647 in Fahrtrichtung Velbert Neviges Rosenhügel "
        + "Bahnhof weiter zu fahren. Die Planmäßige Abfahrt dieser Linie sollte um 20.39 Uhr sein"
        + ". Doch leider passierte wieder einmal aus unerklärbaren dingen nichts. Die Linie ist "
        + "wohl einfach ohne Information ausgefallen. Auf der Anzeigentafel stand weiterhin die "
        + "Planmäßige Abfahrt für 20.39Uhr. Ich wartete mit einigen weiteren Fahrgästen die auch "
        + "diese Linie nutzen wollten bis um 20.50Uhr. Bei sehr kalten 0°C und rundherum keine "
        + "Möglichkeit irgendwo einzukehren und sich aufzuwärmen entschied ich mich nun ein Taxi "
        + "zu nehmen da die Bedingungen alles andere als akzeptabel und auszuhalten waren. Von "
        + "dem Taxifahrer habe ich mir eine Quittung der Fahrtkosten in Höhe von 29,80€ "
        + "ausstellen lassen. Ich bitte hiermit mir die Fahrtkosten in Höhe von 29,80€ zu "
        + "begleichen da es wie oben schon beschrieben keinen Zustand darstellte auf den nächsten"
        + " Bus zu hoffen.Bitte nehmen sie mit mir bezüglich meiner Kontodaten Kontakt auf";

    public static final String PREVIEW_D = TEXT_D.substring(0, 250);

    public static final String TEXT_E = "heute Morgen rief mich Frau Kluge an, ADRESSE.  Sie muss"
        + " jeden Morgen vom Clefkothen zum Kaisergarten, hat ein Ticket und benutzt die Buslinie"
        + " 635, die wohl nur alle 20 Min. fährt. Nun fällt dieser Bus mehrmals in der Woche aus "
        + "und heute Morgen hat sie wieder eine halbe Stunde in der Kälte gestanden. Sie versteht"
        + " nicht, warum in solchen Fällen die Linien CE 65 und CE 64 die Leute nicht mitnehmen "
        + "können.  Sie bezahlt ihr Ticket und kann es nicht nutzen. Zweimal schon ist sie mit "
        + "einem Taxi zur Arbeit gefahren und hat jedes mal 14 Euro bezahlt. Ich habe ihr gesagt,"
        + " dass ich ihre Kritik an die Kollegen vom Verkehr weiterleite und man sich bei ihr "
        + "melden wird.  Sie hat die Telefonnr. 0711 2468100";

    public static final String PREVIEW_E = TEXT_E.substring(0, 250);

    public static final String TEXT_F = "Hallo, ich bin Peter. Die Haltestelle Hauptbahnhof der" +
        " Linie U6 ist schlecht. Gebt mir 20€!";

    public static final String PREVIEW_F = TEXT_F;

    public static final String TEXT_G = "Dies ist eine Testbeschwerde. test test test test test 123";

    public static final String PREVIEW_G = TEXT_G;

    public static final String TEXT_H = "Die Linie U7 hat überfüllte Haltestellen. hallo hallo 42";

    public static final String PREVIEW_H = TEXT_H;

    private static final NamedEntity testEntityName = new NamedEntityBuilder()
        .setLabel("Name")
        .setStart(15)
        .setEnd(19)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("Peter")
        .createNamedEntity();

    private static final NamedEntity testEntityStop = new NamedEntityBuilder()
        .setLabel("Haltestelle")
        .setStart(38)
        .setEnd(49)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("Hauptbahnhof")
        .createNamedEntity();

    private static final NamedEntity testEntityLine = new NamedEntityBuilder()
        .setLabel("Linie")
        .setStart(61)
        .setEnd(62)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("U6")
        .createNamedEntity();

    private static final NamedEntity testEntityPlace = new NamedEntityBuilder()
        .setLabel("Ort")
        .setStart(70)
        .setEnd(79)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("Stuttgart")
        .createNamedEntity();

    private static final NamedEntity testEntityMoney = new NamedEntityBuilder()
        .setLabel("Geldbetrag")
        .setStart(87)
        .setEnd(88)
        .setSetByUser(false)
        .setExtractor("None")
        .setValue("20€")
        .createNamedEntity();

    public static final List<NamedEntity> ENTITIES_F = List.of(testEntityName, testEntityStop,
        testEntityLine, testEntityMoney);

    public static final List<NamedEntity> ENTITIES_I = List.of(testEntityLine, testEntityPlace,
        testEntityStop);
  }
}
