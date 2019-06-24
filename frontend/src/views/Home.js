/**
 * This class creates the Home view.
 *
 * @version <0.1>
 */

import React from 'react';

import Block from 'components/Block/Block';
import Row from 'components/Row/Row';
import Content from 'components/Content/Content';
import logo from './../assets/img/StuproLogo2.svg';
import team from './../assets/img/Gruppenfoto.jpg';
import iaoPartner from './../assets/img/iao.png';
import iatPartner from './../assets/img/iat.png';

import 'assets/scss/Home.scss';

function Home () {
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <Content>
            <div className='header'>
              <img src={logo} className='logo' alt='logo' width='60%' />
            </div>
            <div className='body'>
              <br />
              <p>
              Im Rahmen des Studienprojektes der Universität Stuttgart im Sommersemester 2019
              wurde am IAT der Universität Stuttgart und dem Fraunhofer IAO die webbasierte
              Plattform Querimonia für die Erprobung und Anwendung von Verfahren zur Analyse
              und Aufbereitung von Texten am Beispiel des Beschwerdemanagements entwickelt.
              </p>
              <br />
              <p>
              Ein typisches Anwendungsbeispiel ist die automatische Generierung einer Antwort
              auf eine Beschwerdenachricht mit Hilfe von Mensch-Maschine-Kollaboration mit
              selbstlernenden Textminingverfahren.
              </p>
              <br />
              <h3>Beispiel:</h3>
              <br />
              <h6>Beschwerdenachricht</h6>
              <p>
              Die Buslinie 637 ist am 01.03.2019 an der
              Gartenheimstr. in Langenberg(Schule!) um 13.46 Uhr
              nicht gekommen. Meine Tochter Anita hat eine
              halbe Stunde warten müssen, bis ich ihr dann ein
              Taxi habe schicken müssen. Die Fahrt hat 14,60 Euro
              gekostet. Warum kaufe ich ein Schokoticket? Ich
              bitte um Erstattung meiner zusätzlichen Kosten.
              Viele Grüße Maria Seifert
              </p>
              <br />
              <h6>Automatisch generierte Antwort</h6>
              <p>
              Sehr geehrte Frau Seifert,
              wir bedauern sehr die Ihnen
              entstandenen
              Unannehmlichkeiten.
              Ihren Antrag auf Erstattung von
              Taxikosten in Höhe von 14,60 Euro
              haben wir erhalten.
              Demnach konnten Sie am 1.3.2019,
              einen Bus der Linie 637, ab der
              Haltestelle „Gartenheimstraße“
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
              <br />
              <h1>Unser Team</h1>
              <img src={team} className='team' alt='logo' width='60%' />
              <p>&copy; 2019 Fraunhofer IAO, IAT Universität Stuttgart</p>
            </div>
            <div className='footer'>
              <h1>Unsere Partner</h1>
              <img src={iaoPartner} className='iaoPartner' alt='logo' width='30%' />
              <img src={iatPartner} className='iatPartner' alt='logo' width='30%' />
            </div>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default Home;
