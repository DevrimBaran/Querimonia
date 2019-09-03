/**
 * This class creates the Home view.
 *
 * @version <0.1>
 */

import React from 'react';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import spotlight from './../assets/img/spotlight.svg';
import iaoPartner from './../assets/img/iao.png';
import iatPartner from './../assets/img/iat.png';
import '../assets/scss/_images.scss';
import '../assets/scss/toSort/_entity.scss';
import '../assets/scss/toSort/_boxWithMargin.scss';
import '../assets/scss/toSort/_fiftyFiftyBox.scss';

function Home () {
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <Content>
            <center>
              <div className='header'>
                <img src={spotlight} className='spotlight' alt='logo' />
              </div>
              <h1> Was kann Querimonia?</h1>
              <br />
              <div className='boxWithMargin'>
                <div className='fiftyFifty'>
                  <div className='leftBox'>
                    <h2>Erkennen von Entitäten</h2>
                    <br />
                    <p>
                    Durch KI-Verfahren extrahiert unser intelligentes System wichtige Elemente eines Textes automatisch.
                    Dem Nutzer werden die Entitäten mit unterschliedlicher Farbe angezeigt.
                      <br />
                    Zum Testen:
                    </p>
                    <center>
                      <i className='fas fa-arrow-right fa-3x' id='arrow' />
                    </center>
                  </div>
                  <div className='rightBox' id='entityBox'>
                    <p>
                    Die Buslinie <mark className='entity_buslinie'>637</mark> ist am <mark className='entity_datum'>01.03.2019</mark> an der
                      <mark className='entity_haltestelle'>Gartenheimstr.</mark> in Langenberg(Schule!) um 13.46 Uhr
                    nicht gekommen. Meine Tochter Anita hat eine
                    halbe Stunde warten müssen, bis ich ihr dann ein
                    Taxi habe schicken müssen. Die Fahrt hat <mark className='entity_preis'>14,60 Euro</mark>
                    gekostet. Warum kaufe ich ein Schokoticket? Ich
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
                    Durch SoUndSo-Verfahren und DiesUndDas erkennt Querimonia die Stimmung (Emotion und Sentiment) einer
                    Beschwerde und wandelt diese in passende Emojis um.
                      <br />
                    Zum Testen:
                    </p>
                    <center>
                      <i className='fas fa-arrow-right fa-3x' id='arrow' />
                    </center>
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
                      <i className='tooltiptext'>
                        <i className='far fa-angry fa-4x' />
                      </i>
                    </div>
                  </div>
                </div>
                <div className='fiftyFifty'>
                  <div className='leftBox'>
                    <h2>Antwort generieren</h2>
                    <br />
                    <p>
                      Die oben genannten Funktionen ermöglichen dem Nutzer eine automatisch generierte Antwort
                      zu einer eingegangenen Beschwerde zu erstellen. Abhängig von den erkannten Entitäten und
                      der erkannten Stimmung wird eine Antwort vorgeschlagen, die bearbeitet werden kann.
                      <br />
                      Zum Testen:
                    </p>
                    <center>
                      <i className='fas fa-arrow-right fa-3x' id='arrow' />
                    </center>
                  </div>
                  <div className='rightBox' id='antwortGenerierung'>
                    <p>Sehr geehrte</p>
                    <p className='generierteAntwort'>
                      <mark className='entity_name'>Frau Seifert</mark>,
                        wir bedauern sehr die Ihnen
                        entstandenen
                        Unannehmlichkeiten.
                        Ihren Antrag auf Erstattung von
                        Taxikosten in Höhe von <mark className='entity_preis'>14,60 Euro</mark>
                        haben wir erhalten.
                        Demnach konnten Sie am <mark className='entity_datum'>01.03.2019</mark>,
                        einen Bus der <mark className='entity_buslinie'>637</mark>, ab der
                        Haltestelle <mark className='entity_haltestelle'>„Gartenheimstraße“</mark>
                        nicht nehmen.
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
                <center>
                  <div className='footer'>
                    <br />
                    &copy; 2019 Fraunhofer IAO, IAT Universität Stuttgart
                  </div>
                </center>
              </div>
            </center>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default Home;
