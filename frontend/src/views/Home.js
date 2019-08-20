/**
 * This class creates the Home view.
 *
 * @version <0.1>
 */

import React from 'react';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import logo from './../assets/img/StuproLogo2.svg';
import iaoPartner from './../assets/img/iao.png';
import iatPartner from './../assets/img/iat.png';
import screen1 from './../assets/img/screen1.jpg';
import screen2 from './../assets/img/screen2.jpg';
import screen3 from './../assets/img/screen3.jpg';

function Home () {
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <Content>
            <center>
              <div className='header'>
                <img src={logo} className='logo' alt='logo' width='40%' />
                <br />
              </div>
              <div className='margin'>
                <br />
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
              </div>
              <br />
              <h2>Beispiel:</h2>
              <div className='example'>
                <br />
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
                <br />
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
              <br />
              <h1> Wie funktioniert Querimonia?</h1>
              <div className='introduction_box'>
                <h6>Hinzufügen einer Beschwerde</h6>
                <p>Zum hinzufügen einer Beschwerde hat der Nutzer die Möglichkeit,
                  durch den Upload einer Datei (bisher unterstützte Dateiformate: PDF, WORD, TXT)
                  eine Beschwerde zu importieren oder manuell eine einzugeben.
                </p>
                <img src={screen1} alt={'screen1'} />
              </div>
              <div className='introduction_box'>
                <h6>Beschwerde ansehen</h6>
                <p>Der Nutzer hat eine Übersicht von allen importierten Beschwerden. Es gibt verschiedene Filtermöglichkeiten
                  und die Beschwerden sind in einer Tabelle (Anliegen, Vorschau, Emotion, Sentiment, Kategorie, Datum)
                  eingeordnet.
                </p>
                <img src={screen2} alt={'screen1'} />
              </div>
              <div className='introduction_box'>
                <h6>Antwort generieren</h6>
                <p>Wenn der Nutzer auf eine Beschwerde klickt, hat er die Möglichkeit
                  eine Antwort zu generieren. Auf der linken Seite ist ein Textfeld,
                  um die Antwort zu bearbeiten und automatisch generierte Antwortbausteine,
                  um eine Antwort zusammenzuklicken. Auf der rechten Seite sieht der Nutzer
                  den Originaltext und die entdeckten Entitäten.
                </p>
                <img src={screen3} alt={'screen1'} />
              </div>
            </center>
            <center>
              <div className=''>
                <br />
                <p>&copy; 2019 Fraunhofer IAO, IAT Universität Stuttgart</p>
                <br />
                <a href='https://www.iao.fraunhofer.de/' rel='noopener noreferrer' target='_blank' ><img src={iaoPartner} className=' center margin' alt='logo' width='20%' /></a>
                <a href='https://www.iat.uni-stuttgart.de/' rel='noopener noreferrer' target='_blank' ><img src={iatPartner} className=' center margin' alt='logo' width='20%' /></a>
              </div>
            </center>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default Home;
