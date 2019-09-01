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
// import screen1 from './../assets/img/screen1.jpg';
// import screen2 from './../assets/img/screen2.jpg';
// import screen3 from './../assets/img/screen3.jpg';
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
                <br />
              </div>
              <div className='boxWithMargin'>
                <br />
                <br />
                <center>
                  <h2>
                Querimonia ist eine webbasierte Plattform für die Erprobung und Anwendung <br />
                von Verfahren zur Analyse und Aufbereitung von Texten am Beispiel des Beschwerdemanagements.
                  </h2>
                </center>
                <br />
              </div>
              <br />
              <h1> Was kann Querimonia?</h1>
              <div className='boxWithMargin'>
                <div className='fiftyFifty'>
                  <div className='leftBox'>
                    <h6>Erkennen von Entitäten</h6>
                    <p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
                      et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
                      Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
                      consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
                      sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
                      takimata sanctus est Lorem ipsum dolor sit amet.
                    </p>
                  </div>
                  <div className='rightBox' id='entityBox'>
                    <h6>Beschwerdenachricht</h6>
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
                    <h6>Erkennung der Stimmung</h6>
                    <p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
                        et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
                        Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
                        consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
                        sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
                        takimata sanctus est Lorem ipsum dolor sit amet.
                    </p>
                  </div>
                  <div className='rightBox'>
                    <p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
                        et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
                        Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
                        consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
                        sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
                        takimata sanctus est Lorem ipsum dolor sit amet.
                    </p>
                  </div>
                </div>
                <div className='fiftyFifty'>
                  <div className='leftBox'>
                    <h6>Antwort generieren</h6>
                    <p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
                        et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
                        Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
                        consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
                        sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
                        takimata sanctus est Lorem ipsum dolor sit amet.
                    </p>
                  </div>
                  <div className='rightBox' id='antwortGenerierung'>
                    <h6>Automatisch generierte Antwort</h6>
                    <p>
                        Sehr geehrte <mark className='entity_name'>Frau Seifert</mark>,
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
                        Freundliche Grüße
                        Ihr Schokorobo
                    </p>
                  </div>
                </div>
                <div className='footer'>
                  <br />
                  <a href='https://www.iao.fraunhofer.de/' rel='noopener noreferrer' target='_blank' ><img src={iaoPartner} className=' center margin' alt='logo' width='20%' /></a>
                  <a href='https://www.iat.uni-stuttgart.de/' rel='noopener noreferrer' target='_blank' ><img src={iatPartner} className=' center margin' alt='logo' width='20%' /></a>
                </div>
              &copy; 2019 Fraunhofer IAO, IAT Universität Stuttgart
              </div>
            </center>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default Home;
