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
                    Zum Testen:
                    </p>
                    <center>
                      <i className='fas fa-arrow-right fa-3x' id='arrow' />
                    </center>
                  </div>
                  <div className='rightBox'>
                    <div className='tooltipStartseite'>
                      <p>
                        Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
                        et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
                        Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
                      </p>
                      <i className='tooltiptext'>Tooltip text</i>
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
                <div className='footer'>
                  <br />
                  <a href='https://www.iao.fraunhofer.de/' rel='noopener noreferrer' target='_blank' ><img src={iaoPartner} className=' center margin' alt='logo' width='20%' /></a>
                  <a href='https://www.iat.uni-stuttgart.de/' rel='noopener noreferrer' target='_blank' ><img src={iatPartner} className=' center margin' alt='logo' width='20%' /></a>
                  &copy; 2019 Fraunhofer IAO, IAT Universität Stuttgart
                </div>
              </div>
            </center>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default Home;
