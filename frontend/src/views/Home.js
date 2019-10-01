/**
 * This class creates the Home view.
 *
 * @version <0.1>
 */

import React from 'react';

import Block from './../components/Block';
import Row from './../components/Row';
import Sentiment from './../components/Sentiment';
import Content from './../components/Content';
import spotlight from './../assets/img/spotlight.svg';
import '../assets/scss/_images.scss';
import '../assets/scss/toSort/_entity.scss';
import '../assets/scss/toSort/_boxWithMargin.scss';
import '../assets/scss/toSort/_fiftyFiftyBox.scss';

function Home () {
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <Content style={{ width: '80%', margin: 'auto' }}>
            <div className='header'>
              <img src={spotlight} className='spotlight' alt='logo' />
            </div>
            <h1 style={{ textAlign: 'center' }}> Was kann Querimonia?</h1>
            <br />
            <div className='boxWithMargin clearfix' style={{ maxWidth: '100%', padding: '2rem 0' }}>
              <div className='fiftyFifty'>
                <div className='leftBox'>
                  <h2>Erkennen von Entitäten</h2>
                  <br />
                  <p>
                  Unser intelligentes System extrahiert durch KI-Verfahren wichtige Elemente eines Textes.
                  Das Erkennen von Entitäten läuft automatisiert ab und liefert Ihnen die wichtigsten Elemente,
                  die in unterschiedlichen Farben gekennzeichnet werden. Die extrahierten Entitäten können an Ihre Wünsche
                  angepasst werden.
                  </p>
                  <i className='fas fa-arrow-right fa-3x' id='arrow' />
                </div>
                <div className='rightBox' id='entityBox'>
                  <p>
                  Die Buslinie <mark className='entity_buslinie'>637</mark> ist am <mark className='entity_datum'>01.03.2019</mark> an
                  der <mark className='entity_haltestelle'>Gartenheimstr.</mark> in Langenberg(Schule!) um 13.46 Uhr
                  nicht gekommen. Meine Tochter Anita hat eine
                  halbe Stunde warten müssen, bis ich ihr dann ein
                  Taxi habe schicken müssen. Die Fahrt hat <mark className='entity_preis'>14,60 Euro</mark> gekostet.
                  Warum kaufe ich ein Schokoticket? Ich
                  bitte um Erstattung meiner zusätzlichen Kosten.
                  Viele Grüße <mark className='entity_name'>Maria Seifert</mark>
                  </p>
                </div>
              </div>
              <div className='fiftyFifty'>
                <div className='leftBox'>
                  <h2>Erkennung der Stimmung</h2>
                  <br />
                  <p>
                    Querimonia liefert einen regelbasierten Ansatz zur Erkennung und Interpretation der Stimmung und Emotionen aus Ihrem Dokument.
                    Mittels unserem intelligenten Algorithmus, können bis zu 7 verschiedene Emotionen erkannt werden. Die Stimmung wird durch eine intern entworfene Zahlenskala anschaulich repräsentiert.
                    Diese zwei Analyseverfahren bieten Ihnen neue Erkentnisse und Aussagen über das zu analysierende Dokument.
                  </p>
                  <i className='fas fa-arrow-right fa-3x' id='arrow' />
                </div>
                <div className='rightBox'>
                  <div className='tooltipStartseite'>
                    <p>
                      Ich bin total sauer! Es ist eine absolute Frechheit, wie sich ihre Mitarbeiter verhalten.
                      Es darf nicht sein, dass der Busfahrer mich als zahlenden Kunden beleidigt. Sowas geht gar nicht
                      und ich möchte eine Entschuldigung!
                      Seit 10 Jahren fahre ich jeden Tag mit dem Bus zur Arbeit, seit 10 Jahren bin ich Kunde bei Ihnen.
                      Wenn ihnen ihre Kunden wichtig sind, dann reden sie bitte mit ihren Mitarbeitern und geben Sie jedem
                      erstmal einen Kurs in Freundlichkeit und anständiges Benehmen.
                    </p>
                    <Sentiment className='tooltiptext' tendency='-1' />
                  </div>
                </div>
              </div>
              <div className='fiftyFifty'>
                <div className='leftBox'>
                  <h2>Antwort generieren</h2>
                  <br />
                  <p>
                    Mit Querimonia können Sie eine automatisch generierte Antwort zu einer Beschwerde erstellen.
                    Durch das Erkennen der Entitäten und der Stimmung werden Ihnen passende Antwortbausteine
                    vorgeschlagen, aus denen Sie eine Antwort zusammenbauen können.
                  </p>
                  <i className='fas fa-arrow-right fa-3x' id='arrow' />
                </div>
                <div className='rightBox' id='antwortGenerierung'>
                  <p>Sehr geehrte</p>
                  <p className='generierteAntwort'>
                    <mark className='entity_name'>Frau Seifert</mark>,
                      wir bedauern sehr die Ihnen
                      entstandenen
                      Unannehmlichkeiten.
                      Ihren Antrag auf Erstattung von
                      Taxikosten in Höhe von <mark className='entity_preis'>14,60 Euro</mark> haben wir erhalten.
                      Demnach konnten Sie am <mark className='entity_datum'>01.03.2019</mark>,
                      einen Bus der <mark className='entity_buslinie'>637</mark>, ab der
                      Haltestelle <mark className='entity_haltestelle'>„Gartenheimstraße“</mark> nicht nehmen.
                      Der Grund hierfür war ein
                      krankheitsbedingter Ausfall.
                      Damit der Betrag zeitnah Ihrem
                      Konto gutgeschrieben werden
                      kann, senden Sie uns bitte Ihre
                      Bankverbindung zu.
                  </p>
                  <p>Freundliche Grüße
                      Ihr Schokorobo</p>
                </div>
              </div>
            </div>
            <br />
            <p style={{ margin: 'auto' }}>&copy; 2019 Fraunhofer IAO, IAT Universität Stuttgart</p>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default Home;
